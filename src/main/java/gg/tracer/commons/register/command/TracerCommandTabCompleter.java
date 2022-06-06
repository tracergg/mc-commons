package gg.tracer.commons.register.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author Bradley Steele
 */
public interface TracerCommandTabCompleter {

    List<String> tabComplete(CommandSender sender, String alias, String[] args);

}
