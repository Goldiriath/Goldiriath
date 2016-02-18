package thirdparty.com.virtivia.minecraft.bukkitplugins.dropprotect;

import com.google.common.collect.HashMultiset;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * https://github.com/pavben/Bukkit-DropProtect/blob/master/src/com/virtivia/minecraft/bukkitplugins/dropprotect/ProtectedItemsSnapshot.java
 *
 * @author pavben
 */
public class InventorySnapshot {

    private final ItemStack[] savedArmorContents;
    private final ItemStack[] savedInventoryContents;

    public InventorySnapshot(Player player, List<ItemStack> drops) {
        // Convert drops into a MultiSet
        HashMultiset<ItemStack> dropsMultiSet = HashMultiset.create(drops);

        // Save armor contents while removing them from drops
        savedArmorContents = filterItemsArrayBySet(player.getInventory().getArmorContents(), dropsMultiSet);

        // Same for inventory
        savedInventoryContents = filterItemsArrayBySet(player.getInventory().getContents(), dropsMultiSet);

        // At this point, saved* Contents will contain only the items that were in dropsMultiSet
        // For each item in saved* Contents, a corresponding item in dropsMultiSet was removed
        // Now we filter the drops, removing the exact items that were saved above
        // This will reorder the drops, but that's okay
        // NOTE: drops.retainAll(dropsMultiSet) would not handle duplicates correctly, so we do this as follows.
        drops.clear();
        drops.addAll(dropsMultiSet);
    }

    private ItemStack[] filterItemsArrayBySet(ItemStack[] items, AbstractCollection<ItemStack> drops) {
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];

            // If the slot has an item
            if (isNonEmptyItem(item)) {
                // If the item was not in the drops
                if (!drops.remove(item)) {
                    // Then we don't save it
                    items[i] = null;
                }
            }
        }

        return items;
    }

    private boolean isNonEmptyItem(ItemStack item) {
        return (item != null && item.getType() != Material.AIR);
    }

    public boolean hasNonEmptyItems() {
        for (ItemStack item : savedArmorContents) {
            if (isNonEmptyItem(item)) {
                return true;
            }
        }

        for (ItemStack item : savedInventoryContents) {
            if (isNonEmptyItem(item)) {
                return true;
            }
        }

        return false;
    }

    public void mergeIntoPlayerInventory(Player player, Location overflowDropLocation) {
        PlayerInventory inventory = player.getInventory();

        LinkedList<ItemStack> overflows = new LinkedList<ItemStack>();

        inventory.setArmorContents(itemsToSet(inventory.getArmorContents(), savedArmorContents, overflows));
        inventory.setContents(itemsToSet(inventory.getContents(), savedInventoryContents, overflows));

        // Now add any overflows
        HashMap<Integer, ItemStack> failedToAdd = inventory.addItem(overflows.toArray(new ItemStack[overflows.size()]));

        // If we failed to add any items (due to a full inventory), drop them near the player
        // This can only occur if another plugin has filled up the player's inventory before us
        for (ItemStack item : failedToAdd.values()) {
            player.sendMessage("Dropping " + item.getAmount() + " " + item.getType() + " due to full inventory!");
            player.getWorld().dropItem(overflowDropLocation, item);
        }
    }

    private ItemStack[] itemsToSet(ItemStack[] currentItems, ItemStack[] savedItems, List<ItemStack> overflows) {
        ItemStack[] itemsToSet = Arrays.copyOf(currentItems, currentItems.length);

        for (int i = 0; i < savedItems.length; i++) {
            // If we have a saved item for this slot
            if (isNonEmptyItem(savedItems[i])) {
                // If there is already an item in a slot we have saved
                if (isNonEmptyItem(itemsToSet[i])) {
                    // Add that item to overflows as we're about to overwrite it
                    overflows.add(itemsToSet[i]);
                }

                itemsToSet[i] = savedItems[i];
            }
        }

        return itemsToSet;
    }
}
