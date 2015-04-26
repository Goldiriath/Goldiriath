package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.util.Util;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SwordBlessing extends ActiveSkill {

    public SwordBlessing(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        // Heal player
        plugin.hm.heal(player, 5);
        Util.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.5f);
    }

}
