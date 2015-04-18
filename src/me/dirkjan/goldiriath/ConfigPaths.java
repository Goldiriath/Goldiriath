package me.dirkjan.goldiriath;

import net.pravian.bukkitlib.config.PathContainer;

public enum ConfigPaths implements PathContainer {

    MOBSPAWNER_ENABLED("mobspawner.enabled"),
    MOBSPAWNER_RADIUS("mobspawner.radius"),
    MOBSPAWNER_MAX_MOBS("mobspawner.max_mobs"),
    MOBSPAWNER_SPAWN_THRESHOLD("mobspawner.spawn_threshold"),;
    private final String path;

    private ConfigPaths(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
