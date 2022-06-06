package gg.tracer.commons.register.command;

import gg.tracer.commons.util.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Bradley Steele
 */
public class TracerCommandBukkit extends Command {

    private static final Field COMMAND_MAP = Reflection.getField(Bukkit.getServer().getClass(), "commandMap");
    private static final Field KNOWN_COMMANDS = Reflection.getField(SimpleCommandMap.class, "knownCommands");

    public static SimpleCommandMap getCommandMap() {
        return Reflection.getFieldValue(COMMAND_MAP, Bukkit.getServer());
    }

    public static Map<String, Command> getKnownCommands() {
        return Reflection.getFieldValue(KNOWN_COMMANDS, getCommandMap());
    }

    protected final TracerCommand command;

    protected TracerCommandBukkit(TracerCommand command) {
        super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
        this.command = command;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        command.internalExecute(sender, args);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> result = command.internalTab(sender, alias, args);

        if (result == null) {
            result = super.tabComplete(sender, alias, args);
        }

        return result;
    }

    public TracerCommand getCommand() {
        return command;
    }
}
