package me.dirkjan.goldiriath;

import net.pravian.bukkitlib.config.YamlConfig;

public interface ConfigSavable {

    public void loadFrom(YamlConfig config);

    public void saveTo(YamlConfig config);

}
