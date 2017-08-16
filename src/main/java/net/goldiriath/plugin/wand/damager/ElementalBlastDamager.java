package net.goldiriath.plugin.wand.damager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by koen on 04/08/2017.
 */
public class ElementalBlastDamager extends STAttackDamager {
    public ElementalBlastDamager(Player player, ItemStack wand) {
        super(player, wand, 0.4);
    }

    @Override
    public boolean call(Location location) {
        return false;
    }
}
