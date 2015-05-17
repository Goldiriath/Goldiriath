package me.dirkjan.goldiriath.player;

import java.util.Collections;
import java.util.HashSet;
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
    private QuestData questData;
    private int money;
    private int health;
    private int maxHealth;
    private int mana;

    protected PlayerData(PlayerManager manager, Player player) {
        this.manager = manager;
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

    public QuestData getQuestData() {
        return questData;
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

        // Load stats
        money = config.getInt("money", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MONEY));
        health = config.getInt("health", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH));
        maxHealth = config.getInt("max_healt", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH));
        mana = config.getInt("mana", Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MANA));
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

        // Save stats
        config.set("money", money);
        config.set("heath", health);
        config.set("max_health", maxHealth);
        config.set("mana", mana);

    }

}
