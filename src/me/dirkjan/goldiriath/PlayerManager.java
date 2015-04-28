package me.dirkjan.goldiriath;

import me.dirkjan.goldiriath.util.Configurable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.dirkjan.goldiriath.skills.Skill;
import me.dirkjan.goldiriath.skills.SkillType;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class PlayerManager {

    private final Goldiriath plugin;
    private final Map<UUID, PlayerData> datamap;

    public PlayerManager(Goldiriath plugin) {
        this.plugin = plugin;
        this.datamap = new HashMap<>();
    }

    public PlayerData getData(Player player) {
        return getData(player, true);
    }

    public PlayerData getData(Player player, boolean shouldLoad) {

        // If the playerdata map has the player stored already, use that
        PlayerData data = datamap.get(player.getUniqueId());
        if (data != null || !shouldLoad) {
            return data;
        }

        // Load data from config
        final YamlConfig config = createPlayerConfig(player);
        if (config.exists()) {
            config.load();
        }

        // Parse data from config
        data = new PlayerData(player);
        data.loadFrom(config);
        datamap.put(player.getUniqueId(), data);
        return data;
    }

    public void logout(Player player) { // Should be called when the player logs out

        final PlayerData data = getData(player, false); // false: Don't load the config if it isn't present

        if (data == null) {
            LoggerUtils.warning("Not saving playerdata for player " + player.getName() + ". No playerdata present!");
            return;
        }

        // Save the config
        final YamlConfig config = createPlayerConfig(player);
        data.saveTo(config);
        config.save(); // Note: saveTo() does not actually save the config

        if (datamap.remove(player.getUniqueId()) == null) {
            LoggerUtils.warning("Could not remove playerdata for player " + player.getName() + ". Playerdata not present!");
        }
    }

    public void saveAll() {

        for (PlayerData data : datamap.values()) {
            final YamlConfig config = createPlayerConfig(data.getPlayer());
            data.saveTo(config);
            config.save(); // Note: saveTo() does not actually save the config
        }
    }

    private YamlConfig createPlayerConfig(Player player) {
        return new YamlConfig(plugin, "players/" + player.getUniqueId() + ".yml", false);
    }

    public class PlayerData implements Configurable {

        private final Player player;
        private final Objective sidebar;
        private final Set<Skill> skills;
        private int money;
        private double health;
        private int mana;

        private PlayerData(Player player) {
            this.player = player;
            this.sidebar = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("sidebar", "dummy");
            this.skills = new HashSet<>();
            sidebar.setDisplayName("Statistics");
            sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
            player.setScoreboard(sidebar.getScoreboard());
        }

        public Player getPlayer() {
            return player;
        }

        public Objective getSidebar() {
            return sidebar;
        }

        public Set<Skill> getSkills() {
            return Collections.unmodifiableSet(skills); // Contents of returned set can not be modified!
        }

        public void addSkill(Skill skill) {
            skills.add(skill);
        }

        public void removeSkill(Skill skill) {
            skills.remove(skill);
        }

        public int getMoney() {
            return money;
        }

        public int addMoney(int added) {
            money += added;
            return money;
        }

        public int removeMoney(int remove) {
            money -= remove;
            return money;
        }

        public boolean hasMoney(int has) {
            return money >= has;
        }

        public double getHealth() {
            return health;
        }

        public void setHealth(double health) {
            this.health = health;
        }

        public int getMana() {
            return mana;
        }

        public void setMana(int mana) {
            this.mana = mana;
        }
        
        

        @Override
        public void loadFrom(YamlConfig config) {

            // Load skills
            if (config.isConfigurationSection("skills")) {

                // Temp skills holder
                final Set<Skill> tempSkills = new HashSet<>();

                // Loop through loaded skills
                for (String skillName : config.getConfigurationSection("skills").getKeys(false)) {

                    // Find the appropriate skill type
                    SkillType type = null;
                    for (SkillType loopType : SkillType.values()) {
                        if (!loopType.getName().equalsIgnoreCase(skillName)) {
                            continue;
                        }

                        type = loopType;
                        break;
                    }

                    if (type == null) {
                        LoggerUtils.warning("Could not load skill '" + skillName + "' for player " + player.getName() + ". Skill type not found!");
                        continue; // Next skill
                    }

                    int lvl = config.getInt("skills." + skillName + ".lvl", -1);

                    if (lvl == -1) {
                        LoggerUtils.warning("Could not load skill '" + skillName + "' for player " + player.getName() + ". Skill level could not be parsed!");
                        continue; // Next skill
                    }

                    // Create and the skill
                    final Skill skill = type.create(player, lvl);
                    tempSkills.add(skill);
                }

                // Add the loaded skills
                skills.addAll(tempSkills);
            }

            // Load money
            money = config.getInt("money", plugin.config.getInt(ConfigPaths.DEFAULT_MONEY));
            health = config.getInt("health", plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH));
            mana = config.getInt("mana", plugin.config.getInt(ConfigPaths.DEFAULT_MANA));
        }

        @Override
        public void saveTo(YamlConfig config) {
            // Save skill
            for (Skill skill : skills) {
                String basePath = "skills." + skill.getType().getName().toLowerCase();
                config.set(basePath + "lvl", skill.getLvl());
            }
            config.set("money", money);
            config.set("heath", health);
            config.set("mana", mana);
        }

    }

}
