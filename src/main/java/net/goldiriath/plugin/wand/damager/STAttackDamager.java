package net.goldiriath.plugin.wand.damager;

import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.wand.effect.RayEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;



/**
 * Created by koen on 04/08/2017.
 * Single target attack damager.
 */
public abstract class STAttackDamager implements RayEffect.LocationCallback {

    @Setter @Getter
    double hitRadius;
    final Player player;
    final ItemStack wand;

    public STAttackDamager(Player player, ItemStack wand) {
        this.player = player;
        this.wand = wand;
        this.hitRadius = 0.4;
    }
    public STAttackDamager(Player player, ItemStack wand, double hitRadius) {
        this.player = player;
        this.wand = wand;
        this.hitRadius = hitRadius;
    }

    @Override
    public abstract boolean call(Location location);

}
