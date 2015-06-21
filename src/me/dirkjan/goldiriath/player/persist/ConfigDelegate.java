package me.dirkjan.goldiriath.player.persist;

import org.bukkit.configuration.ConfigurationSection;

public abstract class ConfigDelegate<T> {

    protected final String key;

    public ConfigDelegate(String key) {
        this.key = key;
    }

    public abstract T loadValue(ConfigurationSection config);

    public abstract void saveValue(ConfigurationSection config, T object);

}
