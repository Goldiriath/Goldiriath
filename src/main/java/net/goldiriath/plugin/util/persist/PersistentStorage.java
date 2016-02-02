package net.goldiriath.plugin.util.persist;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.goldiriath.plugin.util.persist.delegate.ConfigDelegate;
import net.goldiriath.plugin.util.persist.delegate.DefaultConfigDelegate;
import net.goldiriath.plugin.util.persist.delegate.IntConfigDelegate;
import net.goldiriath.plugin.util.persist.delegate.ListConfigDelegate;
import net.goldiriath.plugin.util.persist.delegate.StringConfigDelegate;
import net.goldiriath.plugin.util.persist.delegate.UUIDConfigDelegate;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.ConfigSavable;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class PersistentStorage implements ConfigLoadable, ConfigSavable {

    public static final Class<DefaultConfigDelegate> DEFAULT_DELEGATE_CLASS = DefaultConfigDelegate.class;
    private static final Map<Class<?>, Class<? extends ConfigDelegate<?>>> DELEGATES = new HashMap<>();

    static {
        register(int.class, IntConfigDelegate.class);
        register(Integer.class, IntConfigDelegate.class);
        register(String.class, StringConfigDelegate.class);
        register(List.class, ListConfigDelegate.class);
        register(UUID.class, UUIDConfigDelegate.class);
    }

    public static <T> void register(Class<T> typeClass, Class<? extends ConfigDelegate<? extends T>> delegateClass) {
        DELEGATES.put(typeClass, delegateClass);
    }

    protected boolean init = false;
    protected final List<Persistence<?>> fields = Lists.newArrayList();

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

            // Custom persistence delegate
            final DelegatePersistence delegateAnn = field.getAnnotation(DelegatePersistence.class);
            Class<? extends ConfigDelegate<?>> delegateClass = (delegateAnn == null ? DELEGATES.get(field.getType()) : delegateAnn.value());

            // Find a matching delegate
            if (delegateClass == null) {
                final Class<?> fieldClass = field.getType();
                for (Class<?> loopDelegateClass : DELEGATES.keySet()) {
                    if (loopDelegateClass.isAssignableFrom(fieldClass)) {
                        delegateClass = DELEGATES.get(loopDelegateClass);
                        break;
                    }
                }
            }

            // Fallback to default delegate
            if (delegateClass == null) {
                delegateClass = DEFAULT_DELEGATE_CLASS;
            }

            // Instantiate delegate
            ConfigDelegate<?> inst;
            try {
                inst = delegateClass.getConstructor(String.class).newInstance(key);
            } catch (Exception ex) {
                Bukkit.getLogger().severe("Could not setup persistent storage for field. Could not instance String-arg delegate constructor!");
                Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(ex));
                continue;
            }

            Persistence<?> pers = new Persistence(field, inst);
            fields.add(pers);
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
    @SuppressWarnings("unchecked")
    public void loadFrom(ConfigurationSection config) {
        initFields();

        for (Persistence<?> persist : fields) {
            try {
                Object newValue = persist.getDelegate().loadValue(config, persist.getField());

                if (newValue == null) {
                    newValue = persist.getDefaultValue();
                }

                Field field = persist.getField();
                if (!Collection.class.isAssignableFrom(field.getType())
                        || field.get(this) == null) {
                    field.set(this, newValue);
                    continue;
                }

                // Collection handling
                Collection<?> col = Collection.class.cast(field.get(this));
                col.clear();
                col.addAll(Collection.class.cast(newValue));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Bukkit.getLogger().severe("Could not load persistent storage value: " + persist.getField().getName());
                Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(ex));
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
                if (value != null) {
                    persist.getDelegate().saveValue(config, value);
                } else {
                    config.set(persist.getDelegate().getKey(), null);
                }

            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Bukkit.getLogger().severe("Could not save persistent storage value: " + persist.getField().getName());
                Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(ex));
            }
        }
    }

}
