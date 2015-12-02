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

        long longestTime = 0;
        Service longestService = null;

        long startTime = System.currentTimeMillis();
        for (Service service : services) {
            long serviceStart = System.currentTimeMillis();

            service.start();

            long serviceTime = System.currentTimeMillis() - serviceStart;
            if (serviceTime > longestTime) {
                longestTime = serviceTime;
                longestService = service;
            }
        }
        long stopTime = System.currentTimeMillis();
        logger.info("Started " + services.size() + " services in " + (stopTime - startTime) + "ms");

        if (longestTime > 20 && longestService != null) {
            logger.info(longestService.getServiceId() + " took longest to load: " + longestTime + "ms");
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
            logger.severe("Could not register service class: " + serviceClass.getSimpleName(), ex);
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
