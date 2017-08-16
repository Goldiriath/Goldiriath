package net.goldiriath.plugin.wand.damager;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.wand.effect.SplashEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thirdparty.de.slikey.effectlib.util.DynamicLocation;

import java.util.Collection;

/**
 * Created by koen on 04/08/2017.
 */
public class BasicAttackDamager extends STAttackDamager {
    public BasicAttackDamager(Player player, ItemStack wand, double hitRadius) {
        super(player, wand, hitRadius);
    }

    @Override
    public boolean call(Location location) {
        World w = location.getWorld();

        Collection<Entity> nearby = w.getNearbyEntities(location, hitRadius, hitRadius, hitRadius);
        if (nearby.isEmpty()) {
            return false;
        }

        // Target, do damage
        Entity target = nearby.iterator().next();
        Goldiriath.instance().bm.attack(player, wand, target);

        // Show effect
        SplashEffect effect = new SplashEffect(Goldiriath.instance().elb.getManager());
        effect.setDynamicOrigin(new DynamicLocation(location));
        effect.start();

        // Play sound
        player.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1.8f);

        // TODO: Hit ground effect
        return true;
    }
}
