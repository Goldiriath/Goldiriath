package me.dirkjan.goldiriath.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.dirkjan.goldiriath.ConfigPaths;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.skill.Skill;
import me.dirkjan.goldiriath.skill.SkillType;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.ConfigSavable;
import net.pravian.bukkitlib.util.LoggerUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class PlayerData implements ConfigLoadable, ConfigSavable {

    private final PlayerManager manager;
    private final Player player;
    private final Objective sidebar;
    private final Set<Skill> skills;
    private final Map<String, Integer> flags;
    private QuestData questData;
    private int money;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int xp;
    private int skillPoints;

    protected PlayerData(PlayerManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.sidebar = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("sidebar", "dummy");
        this.skills = new HashSet<>();
        this.flags = new HashMap<>();
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

    public boolean hasSkill(SkillType type) {
        for (Skill loopSkill : skills) {
            if (loopSkill.getType() == type) {
                return true;
            }
        }

        return false;
    }

    public Map<String, Integer> getFlags() {
        return flags;
    }

    public boolean hasFlag(String flag) {
        return hasFlag(flag, 1);
    }

    public boolean hasFlag(String flag, int amount) {
        return flags.containsKey(flag) && flags.get(flag) >= amount;
    }

    public int getFlag(String flag) {
        return flags.containsKey(flag) ? flags.get(flag) : 0;
    }

    public void setFlag(String flag, int amount) {
        flags.put(flag, amount);
    }

    public void addFlag(String flag) {
        addFlag(flag, 1);
    }

    public void addFlag(String flag, int amount) {
        if (flags.containsKey(flag)) {
            flags.put(flag, flags.get(flag) + amount);
        } else {
            flags.put(flag, amount);
        }
    }

    public void removeFlag(String flag) {
        removeFlag(flag, 1);
    }

    public void removeFlag(String flag, int amount) {
        if (!flags.containsKey(flag)) {
            return;
        }

        int newAmount = flags.get(flag) - amount;

        if (newAmount > 0) {
            flags.put(flag, newAmount);
        } else {
            flags.remove(flag);
        }
    }

    public void deleteFlag(String flag) {
        flags.remove(flag);
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean hasHealth(int health) {
        return this.health >= health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public boolean hasMana(int mana) {
        return this.mana >= mana;
    }

    public QuestData getQuestData() {
        return questData;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void addSkillPoints(int toadd) {
        skillPoints += toadd;
    }

    public void removeSkillPoints(int toremove) {
        skillPoints -= toremove;
    }

    public boolean hasSkillPoints(int has) {
        return skillPoints >= has;
    }

    @Deprecated // Don't use this method
    public void setQuestData(QuestData questData) {
        Validate.notNull(questData);
        this.questData = questData;
    }

    public PlayerManager getManager() {
        return manager;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {

        // Load skills
        skills.clear();
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

        // Load quest data
        questData = new QuestData(this, player);
        if (config.isConfigurationSection("quests")) {
            questData.loadFrom(config.getConfigurationSection("quests"));
        }

        // Flags
        flags.clear();
        if (config.isConfigurationSection("flags")) {
            for (String flag : config.getConfigurationSection("flags").getKeys(false)) {
                int amount = config.getInt("flags." + flag, 0);
                if (amount >= 0) {
                    flags.put(flag, amount);
                }
            }
        }

        // Money
        money = config.getInt("money", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MONEY));

        // Health
        health = config.getInt("health", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH));
        maxHealth = config.getInt("max_health", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH));

        // Mana
        mana = config.getInt("mana", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MANA));
        maxMana = config.getInt("max_mana", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MANA));

        // SkillPoints
        skillPoints = config.getInt("skillpoints", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_SKILLPOINTS));
    }

    @Override
    public void saveTo(ConfigurationSection config) {
        // Save skills
        for (Skill skill : skills) {
            String basePath = "skills." + skill.getType().getName().toLowerCase();
            config.set(basePath + "lvl", skill.getLvl());
        }

        // Save quest data, override prev data
        questData.saveTo(config.createSection("quests"));

        // Flags
        ConfigurationSection flagsSection = config.createSection("flags");
        for (String flag : flags.keySet()) {
            flagsSection.set(flag, flags.get(flag));
        }

        // Money
        config.set("money", money);

        // Health
        config.set("heath", health);
        config.set("max_health", maxHealth);

        // Mana
        config.set("mana", mana);
        config.set("max_mana", maxMana);

        //skillpoints
        config.set("skillpoints", skillPoints);

    }

}
