package net.goldiriath.plugin;

import com.google.common.collect.Lists;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.goldiriath.plugin.util.service.AbstractService;
import net.goldiriath.plugin.util.service.Service;
import org.bukkit.plugin.Plugin;

public class ServiceManager extends AbstractService {

    private final List<Service> services = Lists.newArrayList();

    public ServiceManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        for (Service service : services) {
            service.start();
        }
    }

    @Override
    protected void onStop() {
        for (Service service : Lists.reverse(services)) {
            service.stop();
        }
    }

    public boolean registerService(Service service) {
        return services.add(service);
    }

    public <T extends Service> T registerService(Class<T> serviceClass) {

        T service = null;
        try {
            for (Constructor<?> cons : serviceClass.getConstructors()) {
                Class<?>[] args = cons.getParameterTypes();

                if (args.length == 2
                        && Plugin.class.isAssignableFrom(args[0])
                        && String.class.equals(args[1])) {
                    service = serviceClass.cast(cons.newInstance(plugin, null));
                    break;
                }

                if (args.length == 1
                        && Plugin.class.isAssignableFrom(args[0])) {
                    service = serviceClass.cast(cons.newInstance(plugin));
                    break;
                }
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.severe("Could not register service class: " + serviceClass.getSimpleName());
            logger.severe(ex);
        }

        if (service == null) {
            logger.severe("Could not register service class: " + serviceClass.getSimpleName() + ". No matching constructor found!");
            return null;
        }

        services.add(service);
        return service;
    }

    public <T> T getService(Class<T> wantedClass) {
        for (Service service : services) {
            if (wantedClass.isAssignableFrom(service.getClass())) {
                return wantedClass.cast(service);
            }
        }
        return null;
    }

}
