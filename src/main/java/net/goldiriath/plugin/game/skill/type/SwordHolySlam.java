package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by koen on 30/07/2017.
 */
public class SwordHolySlam extends ActiveSkill {

    public SwordHolySlam(SkillMeta meta, Player player) {
        super(meta, player);
    }


    @Override
    public void use() {
        for (Entity e : player.getNearbyEntities(6,6,6)) {
            final double powerScale = 3;

            //only for living entities
            if (!(e instanceof LivingEntity) || e.equals(player)) { continue; }
            LivingEntity le = (LivingEntity) e;

            //push everyone away
            Vector playerToE = le.getLocation().toVector().subtract(player.getLocation().toVector());
            double length = playerToE.length();
            playerToE = playerToE.normalize();
            le.setVelocity(playerToE.multiply((powerScale/length)));

            //damage everyone
            le.damage(powerScale/length);
        }
    }


}
