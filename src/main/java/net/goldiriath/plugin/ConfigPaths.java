package net.goldiriath.plugin;

import net.pravian.bukkitlib.config.PathContainer;

public enum ConfigPaths implements PathContainer {

    DEATH_MONEY_COST("death.money_cost"),
    DEATH_MONEY_MULTIPLIER("death.money_multiplier"),
    //
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
    MOBSPAWNER_MAX_MOBS("mobspawner.max_mobs"),
    MOBSPAWNER_TIME_THRESHOLD("mobspawner.time_threshold"),
    MOBSPAWNER_RADIUS_THRESHOLD("mobspawner.radius_threshold"),
    MOBSPAWNER_PROFILES("mobspawner.profiles"),
    //
    METACYCLER_ENABLED("metacycler.enabled"),
    METACYCLER_META_TOOL("metacycler.meta_tool"),
    METACYCLER_BIOME_TOOL("metacycler.biome_tool"),
    //
    INFINITE_DISPENSER_ENABLED("infinite_dispenser.enabled"),
    //
    CHESTSPAWNER_ENABLED("chestspawner.enabled");

    private final String path;

    private ConfigPaths(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
