package net.goldiriath.plugin.inventory;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class InventoryUtil {

    private InventoryUtil() {
    }

    public static boolean isWeapon(Material mat) {
        return mat == Material.WOOD_SWORD
                || mat == Material.IRON_SWORD
                || mat == Material.GOLD_SWORD
                || mat == Material.IRON_SWORD
                || mat == Material.DIAMOND_SWORD
                || mat == Material.BOW
                || mat == Material.BLAZE_ROD;
    }

    public static boolean isArmor(Material mat) {
        return mat == Material.LEATHER_BOOTS
                || mat == Material.LEATHER_LEGGINGS
                || mat == Material.LEATHER_CHESTPLATE
                || mat == Material.LEATHER_HELMET
                || mat == Material.IRON_BOOTS
                || mat == Material.IRON_LEGGINGS
                || mat == Material.IRON_CHESTPLATE
                || mat == Material.IRON_HELMET
                || mat == Material.GOLD_BOOTS
                || mat == Material.GOLD_LEGGINGS
                || mat == Material.GOLD_CHESTPLATE
                || mat == Material.GOLD_HELMET
                || mat == Material.DIAMOND_BOOTS
                || mat == Material.DIAMOND_LEGGINGS
                || mat == Material.DIAMOND_CHESTPLATE
                || mat == Material.DIAMOND_HELMET
                || mat == Material.CHAINMAIL_BOOTS
                || mat == Material.CHAINMAIL_LEGGINGS
                || mat == Material.CHAINMAIL_CHESTPLATE
                || mat == Material.CHAINMAIL_HELMET;
    }

    public static boolean isSkill(ItemStack stack) {
        // TODO: skill validation
        return false;
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR;
    }

    public static int getStoreIndex(PlayerInventory inv, ItemStack stack) {
        if (isEmpty(stack)) {
            return -1;
        }

        final ItemStack[] contents = inv.getContents();

        // Loop through the items
        for (int i = 0; i < inv.getSize(); i++) {
            final SlotType slot = SlotType.ofIndex(i);

            // Does the item fit here?
            if (!slot.validate(stack)) {
                continue;
            }

            // If the slot's empty, put it there
            if (isEmpty(contents[i])) {
                return i;
            }
        }

        return -1;
    }

    public static boolean storeInInventory(PlayerInventory inv, ItemStack stack) {
        int index = getStoreIndex(inv, stack);

        if (index >= 0) {
            inv.setItem(index, stack);
            return true;
        }

        // Drop on the floor
        final HumanEntity hEntity = inv.getHolder();
        final Location loc = hEntity.getEyeLocation();
        final Vector velocity = hEntity.getLocation().getDirection().normalize().multiply(0.5);

        loc.getWorld().dropItem(loc, stack).setVelocity(velocity);
        return false;

    }

}
