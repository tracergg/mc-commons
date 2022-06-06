package gg.tracer.commons.logging;

import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * @author Bradley Steele
 */
public enum TracerLogLevel {

    TRACE(0, "Trace", ChatColor.RESET),

    DEBUG(10, "Debug", ChatColor.BLUE),

    INFO(25, "Info", ChatColor.WHITE),

    WARN(50, "Warning", ChatColor.YELLOW),

    ERROR(70, "Error", ChatColor.RED),

    EXCEPTION(75, "Exception", ChatColor.DARK_RED),

    CRITICAL(100, "Critical", ChatColor.DARK_RED)

    ;

    public static TracerLogLevel from(Level level) {
        return (level == Level.OFF || level == Level.ALL || level == Level.FINEST || level == Level.FINER || level == Level.FINE)
                ? TRACE
                : (level == Level.CONFIG)
                ? DEBUG
                : (level == Level.WARNING)
                ? WARN
                : (level == Level.SEVERE)
                ? CRITICAL
                : INFO;
    }

    public final int level;
    public final String tag;
    public final ChatColor color;

    TracerLogLevel(int level, String tag, ChatColor color) {
        this.level = level;
        this.tag = tag;
        this.color = color;
    }
}
