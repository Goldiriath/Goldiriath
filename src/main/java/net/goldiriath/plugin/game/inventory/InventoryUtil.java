package net.goldiriath.plugin.game.inventory;

import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.skill.SkillType;
import net.goldiriath.plugin.game.skill.type.WeaponType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class InventoryUtil {

    private InventoryUtil() {
    }

    public static boolean isWeapon(Material mat) {
        switch (mat) {
            case WOOD_SWORD:
            case STONE_SWORD:
            case GOLD_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
            case BOW:
            case EMERALD:
            case SHEARS:
                return true;

            default:
                return false;
        }
    }

    public static boolean isSkill(ItemStack stack) {

        // Loops through all Skills defined in SkillType and checks if stack is that skill.
        for(int i = 0; i < SkillType.values().length; i++) {
            if(SkillType.values()[i].getDisplay().getStack().isSimilar(stack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSkillOnCooldown(ItemStack stack) {

        // Loops through all Skills defined in SkillType and checks if stack is that skill.
        for(int i = 0; i < SkillType.values().length; i++) {
            if(SkillType.values()[i].getDisplay().getStack().equals(stack)) {
                return false;
            }
        }
        return true;
    }

    public static SkillType getSkill(ItemStack stack) {
        if(isSkill(stack)) {
            for(int i = 0; i < SkillType.values().length; i++) {
                if (SkillType.values()[i].getDisplay().getStack().isSimilar(stack)) {
                    return SkillType.values()[i];
                }
            }
        }
        return null;
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR;
    }

    public static boolean isSkillBook(ItemStack stack) {
        return stack.equals(StaticItem.SKILL_BOOK.getStack());
    }

    public static boolean isWand(ItemStack stack) {
        return stack.getType() == Material.EMERALD;
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

    public static WeaponType getWeaponType(ItemStack stack) {
        // Returns the WeaponType that is in the first slot of the players hotbar
        if(stack == null) {
            return null;
        }

        switch (stack.getType()) {
            case WOOD_SWORD:
            case STONE_SWORD:
            case GOLD_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
                return WeaponType.SWORD;
            case BOW:
                return WeaponType.BOW;
            case EMERALD:
                return WeaponType.WAND;
            case SHEARS:
                return WeaponType.KNIFE;
        }
        return null;
    }

    public static ItemStack getWeapon(Player player) {
        // Returns the ItemStack that is in the first slot of the players hotbar
        return player.getInventory().getItem(0);
    }

    /**
     * Returns an integer that is the first position where an item is found
     * that is similar to the search object. The inventory argument is the
     * inventory to search in, and the Itemstack argument is the Itemstack
     * to find.
     *
     * @param inventory the PlayerInventory to search.
     * @param stack the ItemStack to search for.
     * @return the position of the found item or -1 if nothing was found
     */
    public static int firstSimilar(final PlayerInventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                if (item.isSimilar(stack)) {
                    return i;
                }
            }
        }
        // returns -1 if no item is found.
        return -1;
    }

}
