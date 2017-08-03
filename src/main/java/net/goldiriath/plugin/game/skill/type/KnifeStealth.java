package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by koen on 02/08/2017.
 */
public class KnifeStealth extends ActiveSkill {

    public KnifeStealth(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        if (plugin.pm.getData(player).getFlags().has("stealth")) {
            plugin.pm.getData(player).getFlags().remove("stealth");
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        } else {
            plugin.pm.getData(player).getFlags().put("stealth",1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        }
    }
}
