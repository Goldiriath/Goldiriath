package me.dirkjan.goldiriath.player.persist.delegate;

import java.lang.reflect.Field;
import org.bukkit.configuration.ConfigurationSection;

public class StringConfigDelegate extends ConfigDelegate<String> {

    public StringConfigDelegate(String key) {
        super(key);
    }

    @Override
    public String loadValue(ConfigurationSection config, Field field) {
        return config.getString(key);
    }

}
