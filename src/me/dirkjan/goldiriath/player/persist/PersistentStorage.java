package me.dirkjan.goldiriath.player.persist;

import com.google.common.base.CaseFormat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.ConfigSavable;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class PersistentStorage implements ConfigLoadable, ConfigSavable {

    protected boolean loaded = false;
    protected final List<Persistence> fields = new ArrayList<>();

    public PersistentStorage() {
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);

            final Persist persistAnn = field.getAnnotation(Persist.class);
            if (persistAnn == null) {
                continue;
            }

            final boolean required = persistAnn.required();

            String key = persistAnn.value().isEmpty() ? field.getName() : persistAnn.value();
            key = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);

            final DelegatePersistence delegateAnn = field.getAnnotation(DelegatePersistence.class);

            ConfigDelegate<?> inst;
            if (delegateAnn == null) {
                inst = new DefaultConfigDelegate(key);
            } else {
                try {
                    inst = delegateAnn.value().getConstructor(String.class).newInstance(key);
                } catch (Exception ex) {
                    Bukkit.getLogger().severe("Could not setup persistent storage. Could not instance String-arg delegate constructor!");
                    Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(ex));
                    continue;
                }
            }

            Object def;
            try {
                def = field.get(this);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Bukkit.getLogger().severe("Could not setup persistent storage. Could not obtain default");
                Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(ex));
                continue;
            }

            fields.add(new Persistence(field, inst, def, required));
        }
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        if (!loaded) {
            for (Persistence persist : fields) {
                Object def;
                try {
                    def = persist.getField().get(this);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Bukkit.getLogger().severe("Could not setup persistent storage. Could not obtain default");
                    Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(ex));
                    continue;
                }
                persist.setDefault(def);
            }
            loaded = true;
        }

        for (Persistence persist : fields) {
            try {
                Object value = persist.getDelegate().loadValue(config);

                if (value == null) {
                    value = persist.getDefault();
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
        for (Persistence persist : fields) {
            try {
                Object value = persist.getField().get(this);

                if (!persist.isRequired() && value != null && value.equals(persist.getDefault())) {
                    value = null;
                }

                persist.getDelegate().saveValue(config, value);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Bukkit.getLogger().severe("Could not save persistent storage value: " + persist.getField().getName());
            }
        }
    }

}
