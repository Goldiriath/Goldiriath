package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.player.info.modifier.ModifiedDamageEffect;
import org.bukkit.entity.Player;

/**
 * Created by koen on 31/07/2017.
 */
public class SwordProtectiveFormation extends ActiveSkill {
    public SwordProtectiveFormation(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        Goldiriath.instance().pm.getData(player).getModifiers().addModifier(
                new ModifiedDamageEffect(200, 0.3));
    }
}
