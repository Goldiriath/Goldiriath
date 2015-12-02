package net.goldiriath.plugin.util.service;

import java.util.Objects;
import java.util.logging.Logger;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.logging.GLogger;
import net.goldiriath.plugin.util.RegistrableListener;
import net.pravian.bukkitlib.implementation.BukkitLogger;

public abstract class AbstractService extends RegistrableListener implements Service {

    @Getter
    private final String serviceId;
    //
    protected final GLogger logger;
    protected boolean started = false;

    public AbstractService(Goldiriath plugin) {
        this(plugin, null);
    }

    public AbstractService(Goldiriath plugin, String serviceId) {
        super(plugin);
        this.serviceId = serviceId == null ? getClass().getSimpleName() : serviceId;
        this.logger = new GLogger(plugin.logger, serviceId);
    }

    @Override
    public final void start() {
        if (started) {
            logger.warning("Tried to start service '" + serviceId + "' whilst already started!");
            return;
        }
        started = true;

        try {
            onStart();
        } catch (Exception ex) {
            logger.severe("Unhandled exception whilst starting service '" + serviceId + "'!", ex);
        }
        register();
    }

    @Override
    public final void stop() {
        if (!started) {
            logger.warning("Tried to stop service '" + serviceId + "' whilst already stopped!");
            return;
        }
        started = false;
        unregister();
        try {
            onStop();
        } catch (Exception ex) {
            logger.severe("Unhandled exception whilst stopping service '" + serviceId + "'!", ex);
        }
    }

    @Override
    public final boolean isStarted() {
        return started;
    }

    protected abstract void onStart();

    protected abstract void onStop();

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.serviceId);
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
        final AbstractService other = (AbstractService) obj;
        return Objects.equals(this.serviceId, other.serviceId);
    }

}
