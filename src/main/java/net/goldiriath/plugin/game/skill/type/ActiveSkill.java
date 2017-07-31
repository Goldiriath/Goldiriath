package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import org.bukkit.entity.Player;

public abstract class ActiveSkill extends Skill {

    public ActiveSkill(SkillMeta meta, Player player) {
        super(meta, player);
    }

    public abstract void use();

}
