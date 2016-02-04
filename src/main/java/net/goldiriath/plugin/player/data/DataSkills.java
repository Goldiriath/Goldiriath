package net.goldiriath.plugin.player.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.skill.Skill;
import net.goldiriath.plugin.skill.SkillType;
import net.pravian.bukkitlib.util.LoggerUtils;
import org.bukkit.configuration.ConfigurationSection;

public class DataSkills extends Data {

    final Set<Skill> skills = new HashSet<>();

    public DataSkills(PlayerData data) {
        super(data, "skills");
    }

    public Set<Skill> getSkills() {
        return Collections.unmodifiableSet(skills);
    }

    public void add(Skill skill) {
        skills.add(skill);
    }

    public void remove(Skill skill) {
        skills.remove(skill);
    }

    public boolean has(SkillType type) {
        for (Skill loopSkill : skills) {
            if (loopSkill.getType() == type) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void load(ConfigurationSection config) {
        skills.clear();

        // Loop through loaded skills
        for (String skillName : config.getKeys(false)) {

            // Find the appropriate skill type
            SkillType type;
            try {
                type = SkillType.valueOf(skillName);
            } catch (IllegalArgumentException ex) {
                LoggerUtils.warning("Could not load skill '" + skillName + "' for player " + data.getPlayer().getName() + ". Skill type not found!");
                continue;
            }

            if (type == null) {
                LoggerUtils.warning("Could not load skill '" + skillName + "' for player " + data.getPlayer().getName() + ". Skill type not found!");
                continue;
            }

            int lvl = config.getInt(skillName + ".lvl", -1);

            if (lvl == -1) {
                LoggerUtils.warning("Could not load skill '" + skillName + "' for player " + data.getPlayer().getName() + ". Skill level could not be parsed!");
                continue;
            }

            // Create and the skill
            final Skill skill = type.create(data.getPlayer(), lvl);
            skills.add(skill);
        }

    }

    @Override
    protected void save(ConfigurationSection config) {
        for (Skill skill : skills) {
            String basePath = "skills." + skill.getType().name();
            config.set(basePath + ".lvl", skill.getLvl());
        }
    }

}
