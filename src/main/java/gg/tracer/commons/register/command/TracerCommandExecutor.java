package gg.tracer.commons.register.command;

import org.bukkit.command.CommandSender;

/**
 * @author Bradley Steele
 */
public interface TracerCommandExecutor {

    void execute(CommandSender sender, String[] args);

}
