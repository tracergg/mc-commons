package gg.tracer.commons.register.command;

import com.google.common.collect.Lists;
import gg.tracer.commons.plugin.TracerPlugin;
import gg.tracer.commons.register.Registrable;
import gg.tracer.commons.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

/**
 * @author Bradley Steele
 */
public abstract class TracerCommand implements Registrable, TracerCommandExecutor, TracerCommandTabCompleter {

    public static final List<String> EMPTY_TAB_COMPLETE = new ArrayList<>();

    private TracerCommand parent;
    private TracerCommandExecutor executor = this;
    private TracerCommandBukkit cmd;

    private String name;
    private String description;
    private String usage;
    private String permission;
    private List<String> permissionDenyMsg;
    private boolean opOnly = false;
    private final List<String> aliases = new ArrayList<>();
    private final List<TracerCommand> children = new ArrayList<>();
    private boolean allowPlayer = true;
    private boolean allowConsole = false;
    private boolean sync = true;
    private boolean useDefaultTabCompleter = true;

    protected TracerPlugin plugin;

    @Override
    public final void internalRegister() {
        for (TracerCommand child : children) {
            child.plugin = plugin;
            child.parent = this;

            child.internalRegister();
            child.register();
        }

        if (isRoot()) {
            cmd = new TracerCommandBukkit(this);

            if (TracerCommandBukkit.getCommandMap().register(plugin.getName(), cmd)) {
                List<String> aliases = new ArrayList<>(this.aliases);
                aliases.remove(getName());

                plugin.logger.info(
                        "Registered command: &a%s&r%s with &2%s&r %s",
                        getName(),
                        !aliases.isEmpty() ? String.format(" (&a%s&r)", String.join("&r, &a", aliases)) : "",
                        children.size(),
                        children.size() == 1 ? "child" : "children"
                );
            } else {
                plugin.logger.error("Failed to register command: &c%s&r", getName());
            }
        }
    }

    @Override
    public final void internalUnregister() {
        var map = TracerCommandBukkit.getCommandMap();
        var known = TracerCommandBukkit.getKnownCommands();

        Set<String> remove = new HashSet<>();

        for (var entry : TracerCommandBukkit.getKnownCommands().entrySet()) {
            if (entry.getValue().equals(cmd)) {
                cmd.unregister(map);
                remove.add(entry.getKey());
            }
        }

        for (String alias : remove) {
            known.remove(alias);
        }

        cmd = null;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (useDefaultTabCompleter && args.length == 1) {
            List<String> in = new ArrayList<>();
            List<String> out = new ArrayList<>();

            for (TracerCommand child : children) {
                if (child.hasPermission(sender)) {
                    in.add(child.getName());
                }
            }

            StringUtil.copyPartialMatches(args[args.length - 1], in, out);
            return out;
        }

        return null;
    }

    public List<String> withMatches(String query, List<String> options) {
        return StringUtil.copyPartialMatches(query, options, new ArrayList<>());
    }

    protected final void internalExecute(CommandSender sender, String[] args) {
        if (!children.isEmpty() && args.length > 0) {
            for (TracerCommand child : children) {
                if (child.aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(args[0]))) {
                    List<String> childArgs = Lists.newArrayList(args);
                    childArgs.remove(0);

                    child.internalExecute(sender, childArgs.toArray(new String[0]));
                    return;
                }
            }
        }

        if (!allowPlayer && sender instanceof Player) {
            sendPermissionDenyMessage(sender);
            return;
        }

        if (!allowConsole && sender instanceof ConsoleCommandSender) {
            plugin.logger.error("&c%s&r does not have console support", getName());
            return;
        }

        if (!hasPermission(sender)) {
            sendPermissionDenyMessage(sender);
            return;
        }

        if (sync) {
            internalExecute0(sender, args);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> internalExecute0(sender, args));
        }
    }

    private void internalExecute0(CommandSender sender, String[] args) {
        try {
            executor.execute(sender, args);
        } catch (Throwable e) {
            List<String> cmd = Lists.newArrayList(args);
            cmd.add(0, getName());

            plugin.logger.error("A Throwable was caught while executing command &c%s&r:", String.join(" ", cmd));
            plugin.logger.exception(e);
        }
    }

    protected final List<String> internalTab(CommandSender sender, String alias, String[] args) {
        if (!hasPermission(sender)) {
            return null;
        }

        if (!children.isEmpty() && args.length > 0) {
            for (TracerCommand child : children) {
                if (child.aliases.stream().anyMatch(a -> a.equalsIgnoreCase(args[0]))) {
                    List<String> childArgs = Lists.newArrayList(args);
                    childArgs.remove(0);

                    return child.internalTab(sender, alias, childArgs.toArray(new String[0]));
                }
            }
        }

        return tabComplete(sender, alias, args);
    }

    public void sendPermissionDenyMessage(CommandSender sender) {
        if (permissionDenyMsg == null || permissionDenyMsg.isEmpty()) {
            return;
        }

        Players.sendMessage(sender, permissionDenyMsg);
    }

    public boolean hasPermission(CommandSender sender) {
        if (opOnly && !sender.isOp()) {
            return false;
        }

        if (permission == null || permission.isBlank()) {
            return true;
        }

        return sender.isOp() || sender.hasPermission(permission);
    }

    public String getName() {
        return name != null ? name : !aliases.isEmpty() ? aliases.get(0) : null;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public String getUsage() {
        return usage != null ? usage : "/" + getName();
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getPermissionDenyMessage() {
        return permissionDenyMsg;
    }

    public boolean isOpOnly() {
        return opOnly;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<TracerCommand> getChildren() {
        return children;
    }

    public boolean isAllowPlayer() {
        return allowPlayer;
    }

    public boolean isAllowConsole() {
        return allowConsole;
    }

    public boolean isSync() {
        return sync;
    }

    public boolean isUseDefaultTabCompleter() {
        return useDefaultTabCompleter;
    }

    public TracerCommand getRoot() {
        TracerCommand root = this;

        while (root.parent != null) {
            root = root.parent;
        }

        return root;
    }

    public boolean isRoot() {
        return parent == null;
    }

    // setters

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setPermissionDenyMsg(List<String> permissionDenyMsg) {
        this.permissionDenyMsg = permissionDenyMsg;
    }

    public void setPermissionDenyMsg(String permissionDenyMsg) {
        setPermissionDenyMsg(List.of(permissionDenyMsg));
    }

    public void setOpOnly(boolean opOnly) {
        this.opOnly = opOnly;
    }

    public void setAliases(Collection<? extends String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
    }

    public void setAliases(String... aliases) {
        setAliases(Arrays.asList(aliases));
    }

    public void addChild(TracerCommand child) {
        if (children.contains(child)) {
            return;
        }

        child.plugin = plugin;
        child.parent = parent;

        children.add(child);
    }

    public void setChildren(Collection<? extends TracerCommand> children) {
        for (TracerCommand child : children) {
            addChild(child);
        }
    }

    public void setChildren(TracerCommand... children) {
        setChildren(Arrays.asList(children));
    }

    public void setAllowPlayer(boolean allowPlayer) {
        this.allowPlayer = allowPlayer;
    }

    public void setAllowConsole(boolean allowConsole) {
        this.allowConsole = allowConsole;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public void setUseDefaultTabCompleter(boolean useDefaultTabCompleter) {
        this.useDefaultTabCompleter = useDefaultTabCompleter;
    }
}
