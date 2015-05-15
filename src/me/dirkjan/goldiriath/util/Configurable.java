package me.dirkjan.goldiriath.util;

import org.bukkit.configuration.ConfigurationSection;

public interface Configurable {

    public void loadFrom(ConfigurationSection config);

    public void saveTo(ConfigurationSection config);

}
