package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.player.PlayerData;
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
        PlayerData playerData = plugin.pm.getData(player);
        double healing = 0.05 * playerData.getMaxHealth() + 0.1 * (playerData.getMaxHealth() - playerData.getHealth());

        plugin.dam.heal(player, (int) healing);

        Util.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.5f);
    }

}
