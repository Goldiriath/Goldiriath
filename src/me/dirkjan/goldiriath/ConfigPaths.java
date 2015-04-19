package me.dirkjan.goldiriath;

import net.pravian.bukkitlib.config.PathContainer;

public enum ConfigPaths implements PathContainer {

    MOBSPAWNER_ENABLED("mobspawner.enabled"),
    MOBSPAWNER_DEV_MODE("mobspawner.dev_mode"),
    MOBSPAWNER_RADIUS("mobspawner.radius"),
    MOBSPAWNER_MAX_MOBS("mobspawner.max_mobs"),
    MOBSPAWNER_SPAWN_THRESHOLD("mobspawner.spawn_threshold"),

    INFINITE_DISPENSER_ENABLED("infinite_dispenser.enabled"),
    ;

    private final String path;

    private ConfigPaths(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
