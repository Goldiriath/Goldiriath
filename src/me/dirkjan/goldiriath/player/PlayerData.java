package me.dirkjan.goldiriath.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import me.dirkjan.goldiriath.ConfigPaths;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.player.persist.Persist;
import me.dirkjan.goldiriath.player.persist.PersistentStorage;
import me.dirkjan.goldiriath.skill.Skill;
import me.dirkjan.goldiriath.skill.SkillType;
import net.pravian.bukkitlib.util.LoggerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PlayerData extends PersistentStorage {

    @Getter private final PlayerManager manager;
    @Getter private final GPlayer gPlayer;
    @Getter private final Player player;
    //
    private final Set<Skill> skills;
    private final Map<String, Integer> flags;
    private final Map<String, Integer> dialogs;

    @Getter private QuestData questData;

    @Persist
    @Getter @Setter private int money = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MONEY);

    @Persist
    @Getter @Setter private int health = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH);

    @Persist
    @Getter @Setter private int maxHealth = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_HEALTH);

    @Persist
    @Getter @Setter private int mana = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MANA);

    @Persist
    @Getter @Setter private int maxMana = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_MANA);

    @Persist
    @Getter @Setter private int xp = Goldiriath.plugin.config.getInt(ConfigPaths.DEFAULT_XP);

    @Persist
    @Getter @Setter private int skillPoints;

    protected PlayerData(GPlayer gPlayer) {
        this.manager = gPlayer.getManager();
        this.gPlayer = gPlayer;
        this.player = gPlayer.getPlayer();
        this.skills = new HashSet<>();
        this.flags = new HashMap<>();
        this.dialogs = new HashMap<>();
    }

    public Set<Skill> getSkills() {
        return Collections.unmodifiableSet(skills);
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
        return Collections.unmodifiableMap(flags);
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

    public boolean hasHadDialog(String id) {
        return dialogs.containsKey(id) && dialogs.get(id) > 0;
    }

    public int getDialogCount(String id) {
        return dialogs.get(id);
    }

    public void recordDialog(String id) {
        if (dialogs.containsKey(id)) {
            dialogs.put(id, dialogs.get(id) + 1);
        } else {
            dialogs.put(id, 1);
        }
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

    public boolean hasHealth(int health) {
        return this.health >= health;
    }

    public boolean hasMana(int mana) {
        return this.mana >= mana;
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

    @Override
    public void loadFrom(ConfigurationSection config) {
        super.loadFrom(config);

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
