package me.dirkjan.goldiriath.util;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigLoadable {

    public void loadFrom(ConfigurationSection config);
}
