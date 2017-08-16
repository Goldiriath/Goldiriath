package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.wand.damager.ElementalBlastDamager;
import net.goldiriath.plugin.wand.effect.RayEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import thirdparty.de.slikey.effectlib.util.DynamicLocation;
import thirdparty.de.slikey.effectlib.util.ParticleEffect;

/**
 * Created by koen on 04/08/2017.
 */
public class WandElementalBlast extends ActiveSkill {
    public WandElementalBlast(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        ItemStack firstItem = player.getInventory().getItem(0);
        if (!(firstItem.getType() == Material.EMERALD)) {
            return;
        }
        World world = player.getWorld();

        Vector eyeLoc = player.getEyeLocation().toVector();
        Vector direction = player.getLocation().getDirection();

        Location origin = eyeLoc.clone().add(direction).toLocation(world);
        origin.setDirection(direction);
        origin.add(0, -0.3, 0);

        // Play the visual
        RayEffect effect = new RayEffect(plugin.elb.getManager(), new ElementalBlastDamager(player, firstItem));
        effect.setDynamicOrigin(new DynamicLocation(origin));
        effect.setPARTICLE(ParticleEffect.SMOKE_NORMAL);
        effect.start();

        // Play sound
        player.getWorld().playSound(origin, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.4f);
    }
}
