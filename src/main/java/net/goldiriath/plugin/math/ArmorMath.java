package net.goldiriath.plugin.math;

import com.google.common.annotations.VisibleForTesting;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.GItemMeta;
import org.bukkit.inventory.ItemStack;

public class ArmorMath {

    @VisibleForTesting
    static double b(int l, double t, double h) {
        return 1 + 10 * Math.pow(1.08306, l - 1) * t * h;
    }

    @VisibleForTesting
    static double d(double b) {
        return 1 - (b / (b + 2000));
    }

    public static double baseArmor(ItemStack item) {
        GItemMeta meta = Goldiriath.instance().im.getMeta(item, false);
        int l = 0;
        double t = 1;
        double h = 1;

        if (meta == null) {
            return b(l, t, h);
        }

        //level determination
        l = meta.getLevel();
        
        // tier determination
        t = meta.getTier().getArmorMulti();
        
        // Heavyness determination
        h = meta.getArmorType().getMulti();
        return b(l, t, h);

    }

    public static double armorModifier(ItemStack helmet, ItemStack chestplate, ItemStack pants, ItemStack boots) {
        double b = baseArmor(helmet)
                + baseArmor(chestplate)
                + baseArmor(pants)
                + baseArmor(boots);
        return d(b);
    }
}
