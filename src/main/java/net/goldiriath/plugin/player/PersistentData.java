package net.goldiriath.plugin.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import net.goldiriath.plugin.ConfigPaths;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.persist.Persist;
import net.goldiriath.plugin.player.persist.PersistentStorage;
import net.goldiriath.plugin.skill.Skill;
import net.goldiriath.plugin.skill.SkillType;
import net.pravian.bukkitlib.util.LoggerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@SuppressWarnings("PackageVisibleField")
class PersistentData extends PersistentStorage {

    @Getter // For QuestData
    private final PlayerData data;
    //
    final Set<Skill> skills = new HashSet<>();
    final Map<String, Integer> flags = new HashMap<>();
    final Map<String, Integer> dialogs = new HashMap<>();
    QuestData questData;

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
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        super.loadFrom(config);

        final Player player = data.getPlayer();

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
                final int amount = config.getInt("flags." + flag, 0);
                if (amount > 0) {
                    flags.put(flag, amount);
                }
            }
        }

        // Dialogs
        dialogs.clear();
        if (config.isConfigurationSection("dialogs")) {
            for (String dialog : config.getConfigurationSection("dialogs").getKeys(false)) {
                final int amount = config.getInt("dialogs." + dialog, 0);
                if (amount > 0) {
                    dialogs.put(dialog, amount);
                }
            }
        }

    }

    @Override
    public void saveTo(ConfigurationSection config) {
        super.saveTo(config);

        // Save skills
        for (Skill skill : skills) {
            String basePath = "skills." + skill.getType().getName().toLowerCase();
            config.set(basePath + "lvl", skill.getLvl());
        }

        // Save quest data, override prev data
        config.set("quests", null);
        if (questData != null) {
            questData.saveTo(config.createSection("quests"));
        }

        // Flags
        final ConfigurationSection flagsSection = config.createSection("flags");
        for (String flag : flags.keySet()) {
            flagsSection.set(flag, flags.get(flag));
        }

        // Dialog
        final ConfigurationSection dialogsSection = config.createSection("dialogs");
        for (String dialog : dialogs.keySet()) {
            dialogsSection.set(dialog, flags.get(dialog));
        }
    }

}
