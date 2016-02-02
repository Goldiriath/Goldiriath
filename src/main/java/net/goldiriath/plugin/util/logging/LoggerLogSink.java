package net.goldiriath.plugin.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerLogSink implements LogSink {

    private final Logger logger;

    public LoggerLogSink(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        if (t == null) {
            logger.log(level, message);
        } else {
            logger.log(level, message, t);
        }
    }

}
