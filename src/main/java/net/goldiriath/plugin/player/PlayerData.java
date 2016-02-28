package net.goldiriath.plugin.player;

import net.goldiriath.plugin.player.info.InfoSidebar;
import net.goldiriath.plugin.player.data.DataQuests;
import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.ConfigPaths;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.data.DataFlags;
import net.goldiriath.plugin.player.data.DataSkills;
import net.goldiriath.plugin.player.info.InfoBattle;
import net.goldiriath.plugin.player.info.InfoDialogs;
import net.goldiriath.plugin.util.persist.Persist;
import net.goldiriath.plugin.util.persist.PersistentStorage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PlayerData extends PersistentStorage {

    @Getter
    private final Goldiriath plugin;
    @Getter
    private final PlayerManager manager;
    @Getter
    private final Player player;
    //
    @Getter
    private final InfoBattle battle;
    @Getter
    private final InfoDialogs dialogs;
    @Getter
    private final InfoSidebar sidebar;
    //
    @Getter
    private final DataQuests quests;
    @Getter
    private final DataFlags flags;
    @Getter
    private final DataSkills skills;

    @Persist
    @Getter
    @Setter
    private int money = plugin().config.getInt(ConfigPaths.DEFAULT_MONEY);

    @Persist
    @Getter
    @Setter
    private int health = plugin().config.getInt(ConfigPaths.DEFAULT_HEALTH);

    @Persist
    @Getter
    @Setter
    private int maxHealth = plugin().config.getInt(ConfigPaths.DEFAULT_HEALTH);

    @Persist
    @Getter
    @Setter
    private int mana = plugin().config.getInt(ConfigPaths.DEFAULT_MANA);

    @Persist
    @Getter
    @Setter
    private int maxMana = plugin().config.getInt(ConfigPaths.DEFAULT_MANA);

    @Persist
    @Getter
    @Setter
    private int xp = plugin().config.getInt(ConfigPaths.DEFAULT_XP);

    @Persist
    @Getter
    @Setter
    private int skillPoints = 0;

    public PlayerData(PlayerManager manager, Player player) {
        this.plugin = manager.getPlugin();
        this.manager = manager;
        this.player = player;
        //
        this.battle = new InfoBattle(this);
        this.sidebar = new InfoSidebar(this);
        this.dialogs = new InfoDialogs(this);
        //
        this.quests = new DataQuests(this);
        this.flags = new DataFlags(this);
        this.skills = new DataSkills(this);

    }

    private Goldiriath plugin() {
        return Goldiriath.instance();
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        super.loadFrom(config);

        quests.loadFrom(config);
        flags.loadFrom(config);
        skills.loadFrom(config);

    }

    @Override
    public void saveTo(ConfigurationSection config) {
        super.saveTo(config);

        quests.saveTo(config);
        flags.saveTo(config);
        skills.saveTo(config);
    }

}
