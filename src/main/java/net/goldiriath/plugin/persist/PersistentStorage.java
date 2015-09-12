package net.goldiriath.plugin.persist;

import com.google.common.base.CaseFormat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.persist.delegate.ConfigDelegate;
import net.goldiriath.plugin.persist.delegate.DefaultConfigDelegate;
import net.goldiriath.plugin.persist.delegate.IntConfigDelegate;
import net.goldiriath.plugin.persist.delegate.ListConfigDelegate;
import net.goldiriath.plugin.persist.delegate.StringConfigDelegate;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.ConfigSavable;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class PersistentStorage implements ConfigLoadable, ConfigSavable {

    public static final Class<DefaultConfigDelegate> DEFAULT_DELEGATE_CLASS = DefaultConfigDelegate.class;
    public static final Map<Class<?>, Class<? extends ConfigDelegate<?>>> DELEGATES = new HashMap<>();

    static {
        register(int.class, IntConfigDelegate.class);
        register(Integer.class, IntConfigDelegate.class);
        register(String.class, StringConfigDelegate.class);
        register(List.class, ListConfigDelegate.class);
    }

    public static void register(Class<?> typeClass, Class<? extends ConfigDelegate<?>> delegateClass) {
        DELEGATES.put(typeClass, delegateClass);
    }

    protected boolean init = false;
    protected final List<Persistence<?>> fields = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public PersistentStorage() {
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);

            final Persist persistAnn = field.getAnnotation(Persist.class);
            if (persistAnn == null) {
                continue;
            }

            // Get key
            String key = persistAnn.value().isEmpty() ? field.getName() : persistAnn.value();
            key = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);

            // Find delegate class
            final DelegatePersistence delegateAnn = field.getAnnotation(DelegatePersistence.class);
            Class<? extends ConfigDelegate<?>> delegateClass = (delegateAnn == null ? DELEGATES.get(field.getType()) : delegateAnn.value());

            if (delegateClass == null) {
                // Custom delegate, supertype
                final Class<?> fieldClass = field.getType();
                for (Class<?> loopDelegateClass : DELEGATES.keySet()) {
                    if (loopDelegateClass.isAssignableFrom(fieldClass)) {
                        delegateClass = DELEGATES.get(loopDelegateClass);
                        break;
                    }
                }
            }

            // Fallback to default
            if (delegateClass == null) {
                delegateClass = DEFAULT_DELEGATE_CLASS;
            }

            // Instantiate delegate
            ConfigDelegate<?> inst;
            try {
                inst = delegateClass.getConstructor(String.class).newInstance(key);
            } catch (Exception ex) {
                Bukkit.getLogger().severe("Could not setup persistent storage. Could not instance String-arg delegate constructor!");
                Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(ex));
                continue;
            }


            fields.add(new Persistence(field, inst));
        }
    }

    private void initFields() {
        if (init) {
            return;
        }
        init = true;

        for (Persistence<?> persist : fields) {
            Object def;
            try {
                def = persist.getField().get(this);
                persist.setDefaultValue(def);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Bukkit.getLogger().severe("Could not setup persistent storage. Could not obtain field default!");
                Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(ex));
            }
        }
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        initFields();

        for (Persistence<?> persist : fields) {
            try {
                Object value = persist.getDelegate().loadValue(config, persist.getField());

                if (value == null) {
                    value = persist.getDefaultValue();
                }

                persist.getField().set(this, value);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Bukkit.getLogger().severe("Could not load persistent storage value: " + persist.getField().getName());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void saveTo(ConfigurationSection config) {
        initFields();

        for (Persistence<?> persist : fields) {
            try {
                Object value = persist.getField().get(this);
                persist.getDelegate().saveValue(config, value);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Bukkit.getLogger().severe("Could not save persistent storage value: " + persist.getField().getName());
            }
        }
    }

}
