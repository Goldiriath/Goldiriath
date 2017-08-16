package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import org.bukkit.entity.Player;

/**
 * Created by koen on 04/08/2017.
 */
public class WandDrawPower extends ActiveSkill {
    public WandDrawPower(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        //todo, get damage, gain mana and extra damage
    }
}
