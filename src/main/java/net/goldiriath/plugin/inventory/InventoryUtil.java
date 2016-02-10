package net.goldiriath.plugin.inventory;

import net.goldiriath.plugin.Goldiriath;
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

    public static boolean isSkill(ItemStack stack) {
        // TODO: skill validation
        return false;
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR;
    }

    public static boolean canStore(PlayerInventory inv, ItemStack stack) {
        return getStoreIndex(inv, stack) == -1;
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

    public static boolean storeItem(PlayerInventory inv, ItemStack stack, boolean drop) {
        int index = getStoreIndex(inv, stack);

        if (index >= 0) {
            inv.setItem(index, stack);
            return true;
        }

        // Drop on the floor
        if (drop) {
            final HumanEntity hEntity = inv.getHolder();
            final Location loc = hEntity.getEyeLocation();
            final Vector velocity = hEntity.getLocation().getDirection().normalize().multiply(0.5);

            loc.getWorld().dropItem(loc, stack).setVelocity(velocity);
        }

        return false;
    }

    public static boolean removeItem(PlayerInventory inv, ItemStack remove) {
        int amount = remove.getAmount();

        if (!inv.containsAtLeast(remove, amount)) {
            return false;
        }

        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (isEmpty(stack) || !stack.isSimilar(remove)) {
                continue;
            }

            if (stack.getAmount() > amount) { // Enough items in the stack
                stack.setAmount(stack.getAmount() - amount);
                inv.setItem(i, stack);
                return true;
            } else { // Too few items
                amount -= stack.getAmount();
                inv.setItem(i, null);
            }


        }

        Goldiriath.instance().logger.warning("Could not remove enough " + remove.toString() + " from inventory!");
        storeItem(inv, remove, true);
        return false;
    }

}
