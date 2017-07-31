package net.goldiriath.plugin.player.data;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.game.skill.SkillType;
import net.goldiriath.plugin.game.skill.type.Skill;
import net.goldiriath.plugin.player.PlayerData;
import org.bukkit.configuration.ConfigurationSection;

public class DataSkills extends Data {

    @Getter
    private final Map<SkillType, Skill> skills = Maps.newHashMap();

    public DataSkills(PlayerData data) {
        super(data, "skills");
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
                logger.warning("Could not load skill '" + skillName + "' for player " + data.getPlayer().getName() + ". Skill type not found!");
                continue;
            }

            if (type == null) {
                logger.warning("Could not load skill '" + skillName + "' for player " + data.getPlayer().getName() + ". Skill type not found!");
                continue;
            }

            // Create and the skill
            final SkillMeta meta = new SkillMeta(type);
            meta.loadFrom(config.getConfigurationSection(skillName));
            skills.put(type, type.create(data.getPlayer(), meta));
        }

    }

    @Override
    protected void save(ConfigurationSection config) {
        for (Skill skill : skills.values()) {
            skill.getMeta().saveTo(config.createSection(skill.getType().name()));
        }
    }

}
