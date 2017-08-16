package net.goldiriath.plugin.wand.damager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by koen on 04/08/2017.
 */
public class ElementalWaveNoneDamager extends STAttackDamager {
    public ElementalWaveNoneDamager(Player player, ItemStack wand) {
        super(player, wand);
    }

    @Override
    public boolean call(Location location) {
        return false;
    }
}
