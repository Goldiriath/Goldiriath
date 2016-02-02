package net.goldiriath.plugin.util.logging;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

public class GLogger implements LogSink {

    @Getter
    private final GLogger parent;
    @Getter
    private final Logger internal;
    @Getter
    private final String name;
    private final Set<GLogger> childeren = Sets.newHashSet();
    private final List<LogSink> sinks = Lists.newArrayList();
    //
    @Getter
    @Setter
    private Level level = Level.FINE;

    public GLogger(GLogger parent, String name) {
        this(parent, name, null);
    }

    public GLogger(Logger internal) {
        this(null, null, internal);
    }

    private GLogger(GLogger parent, String sub, Logger internal) {
        if (internal == null && parent == null) {
            throw new IllegalArgumentException("No delegate logger passed");
        }

        this.internal = internal;
        this.parent = parent;
        this.name = sub;

        if (internal != null) {
            sinks.add(new LoggerLogSink(internal));
        }

        if (parent != null) {
            parent.register(this);
        }
    }

    protected final void register(GLogger child) {
        if (child == null) {
            throw new IllegalArgumentException("Child may not be null.");
        }

        if (child.equals(this)) {
            throw new IllegalArgumentException("Cannot register self as child.");
        }

        childeren.add(child);
    }

    protected void subLog(GLogger child, Level level, String message, Throwable t) {
        if (!childeren.contains(child)) {
            throw new IllegalArgumentException("Cannot process child logger message from non-child");
        }

        // Received log message from child, process it
        log(level, message, t);
    }

    // Sink handling
    public void addSink(LogSink sink) {
        if (sinks.contains(sink)) {
            return;
        }
        sinks.add(sink);
    }

    public void clearSinks() {
        sinks.clear();
    }

    public List<LogSink> getSinks() {
        return Collections.unmodifiableList(sinks);
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        if (level.intValue() < this.level.intValue()) {
            return;
        }

        // Give prefix
        if (name != null) {
            message = "[" + name + "] " + message;
        }

        // Hand the message to the parent
        if (parent != null) {
            parent.subLog(this, level, message, t);
            return;
        }

        // Or handle it
        for (LogSink sink : sinks) {
            try {
                sink.log(level, message, t);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public GLogger asSubLogger(String name) {
        return new GLogger(this, name);
    }

    // Logging methods
    public void debug(String message) {
        log(Level.FINE, message, null);
    }

    public void debug(String message, Throwable t) {
        log(Level.FINE, message, t);
    }

    public void info(String message) {
        log(Level.INFO, message, null);
    }

    public void info(String message, Throwable t) {
        log(Level.INFO, message, t);
    }

    public void warning(String message) {
        log(Level.WARNING, message, null);
    }

    public void warning(String message, Throwable t) {
        log(Level.WARNING, message, t);
    }

    public void severe(String message) {
        log(Level.SEVERE, message, null);
    }

    public void severe(String message, Throwable t) {
        log(Level.SEVERE, message, t);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.parent);
        hash = 37 * hash + Objects.hashCode(this.internal);
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GLogger other = (GLogger) obj;
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.internal, other.internal)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

}
