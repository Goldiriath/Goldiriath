package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.damage.modifier.Modifier;
import net.goldiriath.plugin.game.damage.modifier.ModifierType;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.util.Callback;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BowSpreadShot extends ActiveSkill {

    public static double ARROW_ANGLE = Math.PI / 12;

    public BowSpreadShot(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        World w = player.getWorld();

        for (int i = -3; i <= 3; i++) {
            double modYaw = i * ARROW_ANGLE;
            double sinyaw = Math.sin(modYaw);
            double cosyaw = Math.cos(modYaw);

            Vector vec = player.getLocation().getDirection();

            // TODO: AGGHHH - Is this right?
            vec.setX((vec.getX() * sinyaw) - (vec.getY() * cosyaw) - (vec.getZ() * cosyaw));
            vec.setZ(-(vec.getX() * cosyaw) - (vec.getY() * sinyaw) - (vec.getZ() * sinyaw));

            Arrow arrow = w.spawnArrow(player.getLocation(), vec, 0.6f, 12f);
            arrow.setCritical(true); // Skill arrows are critical

            plugin.at.track(arrow, new Callback<Entity>() {

                @Override
                public void call(Entity hit) {
                    // TODO: Fix player switching items
                    plugin.bm.attack(player, player.getItemInHand(), hit,
                            new Modifier(ModifierType.DAMAGE_MULTIPLIER, 0.7),
                            new Modifier(ModifierType.SLOWNESS, 0, 1 * 20));
                }
            });
        }
    }

}
