package gg.tracer.commons.logging;

import gg.tracer.commons.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Bradley Steele
 */
public class TracerLog {

    public static final String PLACEHOLDER_LOG_LEVEL   = "{tracer:log_level}";
    public static final String PLACEHOLDER_LOG_MESSAGE = "{tracer:log_message}";

    private String loggingFormat;
    private TracerLogLevel loggingLevel;

    public TracerLog(Plugin plugin) {
        String name = null;

        if (plugin != null) {
            PluginDescriptionFile desc = plugin.getDescription();
            name = desc.getPrefix();

            if (name == null) {
                name = desc.getName();
            }
        }

        if (name == null) {
            name = "TracerCommons";
        }

        loggingFormat = String.format("[&2%s&r] [%s]: %s", name, PLACEHOLDER_LOG_LEVEL, PLACEHOLDER_LOG_MESSAGE);
        loggingLevel = TracerLogLevel.INFO;
    }

    public TracerLog() {
        this(null);
    }

    // output

    public void rawLog(String message) {
        Bukkit.getConsoleSender().sendMessage(Messages.color(message));
    }

    public void log(String message, Object... args) {
        rawLog(String.format(message, args));
    }

    public void log(Iterable<? extends String> messages) {
        for (String m : messages) {
            log(m);
        }
    }

    public void log(TracerLogLevel level, String message, Object... args) {
        if (loggingLevel.level > level.level) {
            return;
        }

        String msg = loggingFormat
                .replace(PLACEHOLDER_LOG_LEVEL, level.color + level.tag + ChatColor.RESET)
                .replace(PLACEHOLDER_LOG_MESSAGE, message);

        if (level == TracerLogLevel.EXCEPTION) {
            rawLog(msg); // java.util.MissingFormatArgumentException: Format specifier '%s'
        } else {
            log(msg, args);
        }
    }

    // levels

    public void trace(String message, Object... args) {
        log(TracerLogLevel.TRACE, message, args);
    }

    public void debug(String message, Object... args) {
        log(TracerLogLevel.DEBUG, message, args);
    }

    public void info(String message, Object... args) {
        log(TracerLogLevel.INFO, message, args);
    }

    public void warn(String message, Object... args) {
        log(TracerLogLevel.WARN, message, args);
    }

    public void error(String message, Object... args) {
        log(TracerLogLevel.ERROR, message, args);
    }

    public void exception(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));

        for (String s : writer.toString().split(System.lineSeparator())) {
            log(TracerLogLevel.EXCEPTION, s);
        }
    }

    public void critical(String message, Object... args) {
        log(TracerLogLevel.CRITICAL, message, args);
    }


    /**
     * @return the logging message format.
     */
    public String getLoggingFormat() {
        return loggingFormat;
    }

    /**
     * Returns the {@link TracerLogLevel} at which this logger
     * will output at, any received logs that are under the
     * active logging level's threshold will be ignored.
     *
     * @return the active logging level for this logger
     * @see TracerLogLevel#level
     */
    public TracerLogLevel getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingFormat(String loggingFormat) {
        this.loggingFormat = loggingFormat;
    }

    /**
     * Sets the active logging level.
     *
     * @param loggingLevel the level to set.
     * @see #getLoggingLevel()
     */
    public void setLoggingLevel(TracerLogLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }
}
