package me.dirkjan.goldiriath.util.service;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.util.RegistrableListener;
import net.pravian.bukkitlib.implementation.BukkitLogger;

public abstract class AbstractService extends RegistrableListener implements Service {

    protected final String id;
    protected final BukkitLogger logger;
    //
    protected boolean started = false;

    public AbstractService(Goldiriath plugin) {
        this(plugin, null);
    }

    public AbstractService(Goldiriath plugin, String id) {
        super(plugin);
        this.id = id == null ? getClass().getSimpleName() : id;
        this.logger = plugin.logger;
    }

    @Override
    public final void start() {
        if (started) {
            logger.warning("Tried to start service '" + id + "' whilst already started!");
            return;
        }
        started = true;

        try {
            onStart();
        } catch (Exception ex) {
            logger.severe("Unhandled exception whilst starting service '" + id + "'!");
            logger.severe(ex);
        }
        register();
    }

    @Override
    public final void stop() {
        if (!started) {
            logger.warning("Tried to stop service '" + id + "' whilst already stopped!");
            return;
        }
        started = false;
        unregister();
        try {
            onStop();
        } catch (Exception ex) {
            logger.severe("Unhandled exception whilst stopping service '" + id + "'!");
            logger.severe(ex);
        }
    }

    @Override
    public final boolean isStarted() {
        return started;
    }

    protected abstract void onStart();

    protected abstract void onStop();
}
