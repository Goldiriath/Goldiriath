package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by koen on 04/08/2017.
 */
public class KnifeBackStab extends ActiveSkill {
    public KnifeBackStab(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        //todo: how to get the entity you are literally looking at...?
    }
}
