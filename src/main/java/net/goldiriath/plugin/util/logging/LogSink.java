package net.goldiriath.plugin.util.logging;

import java.util.logging.Level;

public interface LogSink {

    public void log(Level level, String message, Throwable t);

}
