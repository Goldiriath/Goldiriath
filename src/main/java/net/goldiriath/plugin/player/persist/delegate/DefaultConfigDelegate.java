package net.goldiriath.plugin.player.persist.delegate;

import java.lang.reflect.Field;
import org.bukkit.configuration.ConfigurationSection;

public class DefaultConfigDelegate extends ConfigDelegate<Object> {

    public DefaultConfigDelegate(String key) {
        super(key);
    }

    @Override
    public Object loadValue(ConfigurationSection config, Field field) {
        return config.get(key);
    }

}
