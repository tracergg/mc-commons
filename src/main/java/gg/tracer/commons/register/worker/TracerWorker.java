package gg.tracer.commons.register.worker;

import gg.tracer.commons.plugin.TracerPlugin;
import gg.tracer.commons.register.Registrable;
import gg.tracer.commons.util.Reflection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.*;

/**
 * @author Bradley Steele
 */
public class TracerWorker implements Registrable, Listener, Runnable {

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final boolean HAS_IS_CANCELLED;

    static {
        HAS_IS_CANCELLED = Reflection.hasMethod(BukkitTask.class, "isCancelled");
    }

    protected TracerPlugin plugin;
    protected WorkerType type;

    // WorkerType.BUKKIT_TASK
    private BukkitTask task;
    protected boolean sync;

    // WorkerType.THREAD
    private Thread thread;
    protected boolean threadRunning = false;
    protected String threadName;

    // WorkerType.SCHEDULED_TASK
    private ScheduledFuture<?> future;
    protected boolean fixed;

    // shared (BUKKIT_TASK, SCHEDULED_TASK)
    protected long period;
    protected long delay;

    @Override
    public final void internalRegister() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setRunning(true);

        plugin.logger.info("Registered worker: &a%s&r", getClass().getSimpleName());
    }

    @Override
    public final void internalUnregister() {
        HandlerList.unregisterAll(this);

        if (type != WorkerType.BUKKIT_TASK || plugin.isEnabled()) {
            setRunning(false);
        }
    }

    @Override
    public void run() {}

    private void setRunning(boolean run) {
        // null type is valid for workers not utilising a task/thread
        if (type == null) {
            return;
        }

        if (run) {
            if (isRunning()) {
                if (type != WorkerType.BUKKIT_TASK || task != null) {
                    plugin.logger.error("Attempted to run worker task &c%s&r while it is already running", getClass().getSimpleName());
                }

                return;
            }

            if (type == WorkerType.BUKKIT_TASK) {
                if (period <= 0) {
                    return;
                }

                if (sync) {
                    task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, delay, period);
                } else {
                    task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
                }
            } else if (type == WorkerType.THREAD) {
                if (threadName == null || threadName.isEmpty()) {
                    thread = new Thread(this);
                } else {
                    thread = new Thread(this, threadName);
                }

                threadRunning = true;
                thread.start();
            } else if (type == WorkerType.SCHEDULED_TASK) {
                if (fixed) {
                    future = executorService.scheduleAtFixedRate(this, delay, period, TimeUnit.MILLISECONDS);
                } else {
                    future = executorService.scheduleWithFixedDelay(this, delay, period, TimeUnit.MILLISECONDS);
                }
            }
        } else {
            if (type == WorkerType.BUKKIT_TASK) {
                if (task != null) {
                    if (plugin.isEnabled()) {
                        task.cancel();
                    }

                    task = null;
                }
            } else if (type == WorkerType.THREAD) {
                if (thread != null) {
                    threadRunning = false;

                    try {
                        thread.join(1000);
                    } catch (InterruptedException e) {
                        // ignored
                    }

                    // check if the thread is still alive
                    if (thread.isAlive()) {
                        plugin.logger.warn("Worker thread did not clean up within 1000ms of signalling stop: this may be a memory leak");
                    }

                    thread = null;
                }
            } else if (type == WorkerType.SCHEDULED_TASK) {
                if (future != null) {
                    future.cancel(false);
                    future = null;
                }
            }
        }
    }

    public boolean isRunning() {
        return type == WorkerType.BUKKIT_TASK
                ? (task != null && (!HAS_IS_CANCELLED || !task.isCancelled()))
                : type == WorkerType.THREAD
                ? (thread != null && thread.isAlive() && !thread.isInterrupted())
                : type == WorkerType.SCHEDULED_TASK && (future != null && !future.isDone());
    }

    public void setWorkerType(WorkerType type) {
        if (this.type != null) {
            throw new IllegalStateException("worker type has already been set");
        }

        if (type == null) {
            throw new IllegalArgumentException("worker type cannot be null");
        }

        this.type = type;
    }

    public void setDelay(long delay) {
        checkPreconditions();

        if (type != WorkerType.BUKKIT_TASK && type != WorkerType.SCHEDULED_TASK) {
            throw new IllegalArgumentException("setDelay is not implemented for " + type.name());
        }

        this.delay = delay;
    }

    public void setPeriod(long period) {
        checkPreconditions();

        if (type != WorkerType.BUKKIT_TASK && type != WorkerType.SCHEDULED_TASK) {
            throw new IllegalArgumentException("setPeriod is not implemented for " + type.name());
        }

        this.period = period;
    }

    public void setSync(boolean sync) {
        checkPreconditions();

        if (type != WorkerType.BUKKIT_TASK) {
            throw new IllegalArgumentException("setSync is not implemented for " + type.name());
        }

        this.sync = sync;
    }

    public void setThreadName(String threadName) {
        checkPreconditions();

        if (type != WorkerType.THREAD) {
            throw new IllegalArgumentException("setThreadName is not implemented for " + type.name());
        }

        this.threadName = threadName;
    }

    public void setIsFixed(boolean fixed) {
        checkPreconditions();

        if (type != WorkerType.SCHEDULED_TASK) {
            throw new IllegalArgumentException("setIsFixed is not implemented for " + type.name());
        }

        this.fixed = fixed;
    }

    private void checkPreconditions() {
        if (type == null) {
            throw new IllegalStateException("setWorkerType must be called first");
        }
    }
}
