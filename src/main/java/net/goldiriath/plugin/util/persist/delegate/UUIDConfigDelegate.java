package net.goldiriath.plugin.util.persist.delegate;

import java.lang.reflect.Field;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;

public class UUIDConfigDelegate extends ConfigDelegate<UUID> {

    public UUIDConfigDelegate(String key) {
        super(key);
    }

    @Override
    public UUID loadValue(ConfigurationSection config, Field field) {
        try {
            return UUID.fromString(config.getString(key));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void saveValue(ConfigurationSection config, Object object) {
        config.set(key, ((UUID) object).toString());
    }

}
