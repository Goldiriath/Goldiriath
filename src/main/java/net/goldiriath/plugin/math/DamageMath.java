package net.goldiriath.plugin.math;

import com.google.common.annotations.VisibleForTesting;
import net.goldiriath.plugin.game.damage.modifier.Modifier;
import net.goldiriath.plugin.game.damage.modifier.ModifierType;
import net.goldiriath.plugin.game.item.meta.GItemMeta;
import net.goldiriath.plugin.game.item.meta.ItemTier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DamageMath {

    private DamageMath() {
    }

    public static double baseDamage(GItemMeta meta) {
        return b(materialMod(meta.getStack().getType()), tierMod(meta.getTier()), meta.getLevel());
    }

    /**
     * Calculates an item's base damage.
     *
     * @param m The weapon's material modifier.
     * @param t The weapon's tier modifier.
     * @param l The level of the item.
     *
     * @return The base damage.
     */
    @VisibleForTesting
    static double b(double m, double t, int l) {
        return 10 + m * t * Math.pow(1.1, l);
    }

    public static double materialMod(Material mat) {
        switch (mat) {
            case BOW:
                return 1.1;
            case SHEARS:
                return 0.8;
            case STICK:
                return 1.025;
            default:
                return 1.0;
        }
    }

    public static double tierMod(ItemTier tier) {
        switch (tier) {
            case BATTERED:
                return 0.8;
            case NORMAL:
                return 1;
            case CRAFTED:
                return 1.1;
            case RARE:
                return 1.21;
            case LEGENDARY:
                return 1.33;
            default:
                throw new IllegalArgumentException("Unknown tier: " + tier);
        }
    }

    public static double attackDamage(double baseDamage, Entity entity, Modifier[] modifiers) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            return a(baseDamage, skillMod(modifiers), inventoryMod(player.getInventory()), environmentMod(player));
        } else {
            // TODO: Mobs with skill/inventory modifiers?
            return a(baseDamage, 1, 1, environmentMod(entity));
        }
    }

    /**
     * Calculates attack damage.
     *
     * @param b The base damage of the item
     * @param s The skill modifier
     * @param i The inventory modifier
     * @param e The environmental modifier
     *
     * @return The attack damage.
     */
    @VisibleForTesting
    static double a(double b, double s, double i, double e) {
        return b * s * i * i;
    }

    public static double skillMod(Modifier[] modifiers) {
        double mod = 1.0;

        for (Modifier m : modifiers) {
            if (m.getType() == ModifierType.DAMAGE_MULTIPLIER) {
                mod *= m.getValue();
            }
        }

        return mod;
    }

    public static double inventoryMod(Inventory inventory) {
        // TODO: Weight system
        return 1.0;
    }

    public static double environmentMod(Entity entity) {
        Location loc = entity.getLocation();

        // TODO: Check if Y=90 appropriate
        if (loc.getBlockY() > 90) {
            return 0.6;
        }

        Biome biome = loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ());

        switch (biome) {
            case COLD_BEACH:
            case ICE_MOUNTAINS:
            case ICE_FLATS:
            case FROZEN_RIVER:
            case FROZEN_OCEAN:
                return 0.7;
        }

        long time = loc.getWorld().getTime();

        // Nighttime
        // http://minecraft.gamepedia.com/Day-night_cycle
        if (time > 12500 && time < 22500) {
            return 0.9;
        }

        // TODO: daytime in friendly grounds (see wiki)
        return 1.0;
    }

    /**
     * Calculates an attack's effective damage.
     *
     * @param a The attack damage
     * @param d The defense's armor modifier
     * @param i The defense's inventory modifier
     *
     * @return The effective damage.
     */
    @VisibleForTesting
    static double e(double a, double d, double i) {
        return Math.max(a * d * i, 1);
    }

    public static double effectiveDamage(ItemStack weapon, ItemStack helmet, ItemStack chestplate, ItemStack pants, ItemStack boots) {
        double a = 1; // TODO
        double d = ArmorMath.armorModifier(helmet, chestplate, pants, boots);
        double i = 1; // TODO

        return e(a, d, i);
    }

}
