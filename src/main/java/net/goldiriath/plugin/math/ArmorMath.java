package net.goldiriath.plugin.math;

import com.google.common.annotations.VisibleForTesting;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.item.meta.GItemMeta;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class ArmorMath {

    @VisibleForTesting
    static double d(double b) {
        return 1 - (b / (b + 2000));
    }

    @VisibleForTesting
    static double b(int l, double t, double h) {
        return 1 + 10 * Math.pow(1.08306, l - 1) * t * h;
    }

    public static double baseArmor(ItemStack item) {
        if (item == null) {
            return 0;
        }

        GItemMeta meta = Goldiriath.instance().itm.getMeta(item, false);
        if (meta == null) {
            return 0;
        }

        int l = meta.getLevel();
        double t = meta.getTier().getArmorMultiplier();
        double h = meta.getArmorType().getMultiplier();
        return b(l, t, h);

    }

    public static double armorModifier(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        double b = baseArmor(helmet)
                + baseArmor(chestplate)
                + baseArmor(leggings)
                + baseArmor(boots);
        return d(b);
    }

    public static double armorModifier(EntityEquipment equipment) {
        return armorModifier(equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots());
    }
}
