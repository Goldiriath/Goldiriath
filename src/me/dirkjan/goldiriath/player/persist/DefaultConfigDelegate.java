package me.dirkjan.goldiriath.player.persist;

import org.bukkit.configuration.ConfigurationSection;

public class DefaultConfigDelegate extends ConfigDelegate<Object> {

    public DefaultConfigDelegate(String key) {
        super(key);
    }

    @Override
    public Object loadValue(ConfigurationSection config) {
        return config.get(key);
    }

    @Override
    public void saveValue(ConfigurationSection config, Object object) {
        config.set(key, object);
    }

}
