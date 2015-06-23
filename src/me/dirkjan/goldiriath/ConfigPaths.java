package me.dirkjan.goldiriath;

import net.pravian.bukkitlib.config.PathContainer;

public enum ConfigPaths implements PathContainer {

    DEFAULT_MONEY("default.money"),
    DEFAULT_HEALTH("default.health"),
    DEFAULT_MANA("default.mana"),
    DEFAULT_SKILLPOINTS("default.skillpoints"),
    DEFAULT_XP("default.xp"),
    //
    DIALOG_TIMEOUT("dialog.timeout"),
    DIALOG_CLICK_THRESHOLD("dialog.click_threshold"),
    //
    MOBSPAWNER_ENABLED("mobspawner.enabled"),
    MOBSPAWNER_DEV_MODE("mobspawner.dev_mode"),
    MOBSPAWNER_RADIUS("mobspawner.radius"),
    MOBSPAWNER_MAX_MOBS("mobspawner.max_mobs"),
    MOBSPAWNER_SPAWN_THRESHOLD("mobspawner.spawn_threshold"),
    MOBSPAWNER_PLAYER_RADIUS_THRESHOLD("mobspawner.player_radius_threshold"),
    MOBSPAWNER_PROFILES("mobspawner.profiles"),
    //
    INFINITE_DISPENSER_ENABLED("infinite_dispenser.enabled");

    private final String path;

    private ConfigPaths(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
