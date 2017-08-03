package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.damage.modifier.Modifier;
import net.goldiriath.plugin.game.damage.modifier.ModifierType;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.util.Callback;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BowPunchShot extends ActiveSkill {

    public BowPunchShot(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true); // Skill arrows are critical
        arrow.setKnockbackStrength(2);

        plugin.at.track(arrow, new Callback<Entity>() {

            @Override
            public void call(Entity hit) {
                plugin.bm.attack(player, InventoryUtil.getWeapon(player), hit,
                        new Modifier(ModifierType.SLOWNESS, 0, 5 * 20));
            }
        });
    }

}
