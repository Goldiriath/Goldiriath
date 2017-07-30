package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.damage.modifier.Modifier;
import net.goldiriath.plugin.game.damage.modifier.ModifierType;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.util.Callback;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BowQuickShot extends ActiveSkill {

    public BowQuickShot(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true); // Skill arrows are critical

        plugin.at.track(arrow, new Callback<Entity>() {

            @Override
            public void call(Entity hit) {
                // TODO: Fix player switching items
                plugin.bm.attack(player, player.getItemInHand(), hit,
                        new Modifier(ModifierType.DAMAGE_MULTIPLIER, 1.2));
            }
        });
    }

}
