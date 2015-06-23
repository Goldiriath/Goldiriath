package me.dirkjan.goldiriath.player.persist.delegate;

import java.lang.reflect.Field;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

public abstract class ConfigDelegate<T> {

    @Getter
    protected final String key;

    public ConfigDelegate(String key) {
        this.key = key;
    }

    public void saveValue(ConfigurationSection config, Object object) {
        config.set(key, object);
    }

    public abstract T loadValue(ConfigurationSection config, Field field); // TODO: Find a better way?

}
