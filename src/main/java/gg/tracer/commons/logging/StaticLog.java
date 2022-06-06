package gg.tracer.commons.logging;

/**
 * @author Bradley Steele
 */
public final class StaticLog {

    private static final TracerLog logger = new TracerLog();

    static {
        logger.setLoggingLevel(TracerLogLevel.TRACE);
    }

    private StaticLog() {}

    public static void trace(String message, Object... args) {
        logger.trace(message, args);
    }

    public static void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    public static void info(String message, Object... args) {
        logger.info(message, args);
    }

    public static void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    public static void error(String message, Object... args) {
        logger.error(message, args);
    }

    public static void exception(Throwable throwable) {
        logger.exception(throwable);
    }

    public static void critical(String message, Object... args) {
        logger.critical(message, args);
    }
}
