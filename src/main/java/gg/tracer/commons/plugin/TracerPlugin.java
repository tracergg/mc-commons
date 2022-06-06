package gg.tracer.commons.plugin;

import gg.tracer.commons.logging.TracerLog;
import gg.tracer.commons.register.Registrable;
import gg.tracer.commons.resource.ResourceProvider;
import gg.tracer.commons.resource.json.JsonResourceHandler;
import gg.tracer.commons.resource.yml.YamlResourceHandler;
import gg.tracer.commons.util.Reflection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bradley Steele
 */
public abstract class TracerPlugin extends JavaPlugin {

    private final List<Registrable> registrables = new ArrayList<>();

    public final TracerLog logger = new TracerLog(this);
    protected ResourceProvider resourceProvider;

    @Override
    public final void onLoad() {
        resourceProvider = new ResourceProvider(this);
        resourceProvider.addResourceHandler(new YamlResourceHandler());
        resourceProvider.addResourceHandler(new JsonResourceHandler());

        execute(StateType.LOAD, this::load);
    }

    @Override
    public final void onEnable() {
        execute(StateType.ENABLE, this::enable);
    }

    @Override
    public final void onDisable() {
        for (Registrable registrable : registrables) {
            unregister(registrable);
        }

        registrables.clear();

        execute(StateType.DISABLE, this::disable);
    }

    // semi-abstract

    public void load() {}

    public void enable() {}

    public void disable() {}


    public TracerLog getTracerLogger() {
        return logger;
    }

    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    public PluginManager getPluginManager() {
        return getServer().getPluginManager();
    }

    public void register(Registrable registrable) {
        if (registrable == null) {
            logger.error("Attempted to register registrable object: &cnull&r");
            return;
        }

        Reflection.setFieldValue(registrable, "plugin", this);

        try {
            registrable.internalRegister();
        } catch (Exception e) {
            logger.error("Failed to register registrable object &c%s&r:", registrable.getClass().getSimpleName());
            logger.exception(e);
            return;
        }

        // no internal exceptions, successfully registered
        if (!registrables.contains(registrable)) {
            registrables.add(registrable);
        }

        registrable.register();
    }

    public void register(Class<? extends Registrable> clazz) {
        Registrable registrable;

        if (Reflection.isSingleton(clazz)) {
            registrable = Reflection.getSingleton(clazz);

            if (registrable == null) {
                logger.error("Failed to register registrable &c%s&r: singleton must have a \"get\" or \"getInstance\" accessor", clazz.getSimpleName());
                return;
            }
        } else {
            registrable = Reflection.newInstance(clazz);

            if (registrable == null) {
                logger.error("Failed to register registrable &c%s&r: newInstance returned null", clazz.getSimpleName());
                return;
            }
        }

        register(registrable);
    }

    @SuppressWarnings("unchecked")
    public void register(Object object) {
        if (object == null) {
            logger.error("Failed to register registrable: null");
            return;
        }

        if (object instanceof Registrable) {
            register((Registrable) object);
        } else if (object instanceof Class<?> clazz) {
            if (Registrable.class.isAssignableFrom(clazz)) {
                register((Class<? extends Registrable>) clazz);
            } else {
                logger.error("Failed to register registrable &c%s&r: class does not implement Registrable", clazz.getSimpleName());
            }
        } else {
            logger.error("Failed to register registrable &c%s&r: unknown object", object.getClass().getSimpleName());
        }
    }

    public void register(Iterable<Object> objects) {
        for (Object object : objects) {
            register(object);
        }
    }

    public void register(Object... objects) {
        for (Object object : objects) {
            register(object);
        }
    }

    public void unregister(Registrable registrable) {
        registrable.unregister();
        registrable.internalUnregister();
    }

    public void reload() {
        for (Registrable registrable : registrables) {
            unregister(registrable);
        }

        for (Registrable registrable : registrables) {
            register(registrable);
        }
    }

    // state

    private void execute(StateType state, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (isEnabled()) {
                logger.error("Failed to execute plugin in state &c%s&r, exception was thrown:", state.name());
                logger.exception(e);

                getPluginManager().disablePlugin(this);
            }
        }
    }
}
