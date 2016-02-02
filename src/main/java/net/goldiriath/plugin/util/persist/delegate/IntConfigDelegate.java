package net.goldiriath.plugin.util.persist.delegate;

import java.lang.reflect.Field;
import org.bukkit.configuration.ConfigurationSection;

public class IntConfigDelegate extends ConfigDelegate<Integer> {

    public IntConfigDelegate(String key) {
        super(key);
    }

    @Override
    public Integer loadValue(ConfigurationSection config, Field field) {
        return config.getInt(key);
    }

}
