package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.damage.modifier.Modifier;
import net.goldiriath.plugin.game.damage.modifier.ModifierType;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.util.Callback;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BowBleedingArrows extends ActiveSkill {

    public BowBleedingArrows(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true); // Skill arrows are critical

        plugin.aht.track(arrow, new Callback<Entity>() {

            @Override
            public void call(Entity hit) {
                plugin.dam.attack(player, InventoryUtil.getWeapon(player), hit,
                        new Modifier(ModifierType.DAMAGE_MULTIPLIER, 1.2),
                        new Modifier(ModifierType.DAMAGE_BLEED_WITH_MULTIPLIER, 0.2, 4 * 20));
            }
        });
    }

}
