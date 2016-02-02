package net.goldiriath.plugin.player;

import net.goldiriath.plugin.player.data.DataQuests;
import lombok.Getter;
import net.goldiriath.plugin.ConfigPaths;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.persist.Persist;
import net.goldiriath.plugin.util.persist.PersistentStorage;
import net.goldiriath.plugin.player.data.DataFlags;
import net.goldiriath.plugin.player.data.DataSkills;
import org.bukkit.configuration.ConfigurationSection;

@SuppressWarnings("PackageVisibleField")
class PersistentData extends PersistentStorage {

    @Getter
    private final PlayerData data;
    //
    final DataQuests quest;
    final DataFlags flags;
    final DataSkills skills;

    @Persist
    int money = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MONEY);

    @Persist
    int health = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH);

    @Persist
    int maxHealth = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH);

    @Persist
    int mana = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MANA);

    @Persist
    int maxMana = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MANA);

    @Persist
    int xp = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_XP);

    @Persist
    int skillPoints = 0;

    PersistentData(PlayerData data) {
        this.data = data;

        this.quest = new DataQuests(data);
        this.flags = new DataFlags(data);
        this.skills = new DataSkills(data);
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        super.loadFrom(config);

        quest.loadFrom(config);
        flags.loadFrom(config);
        skills.loadFrom(config);

    }

    @Override
    public void saveTo(ConfigurationSection config) {
        super.saveTo(config);

        quest.saveTo(config);
        flags.saveTo(config);
        skills.saveTo(config);
    }

}
