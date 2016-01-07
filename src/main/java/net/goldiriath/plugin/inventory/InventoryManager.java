package net.goldiriath.plugin.inventory;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryManager extends AbstractService {

    @SuppressWarnings("deprecation")
    public InventoryManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        prepareInventory(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangeGamemode(PlayerGameModeChangeEvent event) {
        prepareInventory(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof CraftingInventory)) {
            return;
        }

        final PlayerInventory inv = event.getWhoClicked().getInventory();

        if (event.getClick() == ClickType.WINDOW_BORDER_LEFT || event.getClick() == ClickType.WINDOW_BORDER_RIGHT) {
            return;
        }

        // Debug
        //String curI = event.getCurrentItem() == null ? "none" : event.getCurrentItem().getType().toString();
        //String curC = event.getCursor() == null ? "none" : event.getCursor().getType().toString();
        //logger.info(event.getClick() + " - " + event.getAction() + ": " + curI + " @ " + curC + " (" + event.getSlot() + ":" + event.getRawSlot() + ")");
        final int index = event.getSlot(); // https://bugs.mojang.com/secure/attachment/61101/Items_slot_number.jpg
        final SlotType slot = SlotType.ofIndex(index);
        final ItemStack current = event.getCurrentItem();
        final ItemStack cursor = event.getCursor();

        logger.info("Slot: " + slot.name());

        switch (event.getAction()) {

            // Pickup or move item
            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
            case COLLECT_TO_CURSOR:
            case MOVE_TO_OTHER_INVENTORY:
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP: {
                // Don't process ANY-slots
                if (!slot.hasPlaceHolder()) {
                    return;
                }

                // Don't pick up placeholders
                if (SlotType.isPlaceHolder(current)) {
                    event.setResult(Event.Result.DENY);
                    return;
                }

                event.setResult(Event.Result.ALLOW);

                // Set the new placeholder
                delayedFillPlaceholder(inv, slot, index);
                break;
            }

            // Place item
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
            case SWAP_WITH_CURSOR: {
                // Validate item
                if (!slot.validate(cursor)) {
                    event.setResult(Event.Result.DENY);
                    return;
                }

                // Remove placeholder if present
                if (SlotType.isPlaceHolder(current)) {
                    event.setCurrentItem(null);
                    event.setResult(Event.Result.ALLOW);
                } else {
                }
                break;
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false) // false: See bug below
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (!SlotType.isPlaceHolder(event.getItemDrop().getItemStack())) {
            // Fill the placeholder
            Player player = event.getPlayer();
            int slot = player.getInventory().getHeldItemSlot();
            delayedFillPlaceholder(player.getInventory(), SlotType.ofIndex(slot), slot);
        } else {
             // Prevent dropping placeholders

            //event.setCancelled(true);
            // Woah, a bug in Bukkit: https://bukkit.org/threads/cancelling-item-dropping.111676
            // Workaround:
            final Player player = event.getPlayer();
            ItemStack stack = event.getItemDrop().getItemStack().clone();
            stack.setAmount(player.getInventory().getItemInHand().getAmount() + 1);
            event.getItemDrop().remove();
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), stack);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.PHYSICAL) {
            return;
        }

        ItemStack stack = event.getItem();

        // Prevent item use outside of slot
        for (SlotType slot : SlotType.values()) {
            if (slot == SlotType.ANY) {
                continue;
            }

            if (!slot.validate(stack)) {
                continue;
            }

            // This item must be used in a slot
            event.getPlayer().sendMessage(ChatColor.RED + "You must use this item in the designated slot.");
            event.setUseItemInHand(Event.Result.DENY);
            return;
        }
    }

    private void prepareInventory(Player player) {
        PlayerInventory inv = player.getInventory();

        // Loop through the inventory
        for (SlotType slot : SlotType.values()) {
            if (!slot.hasPlaceHolder()) {
                continue;
            }

            for (int i : slot.getIndices()) {
                final ItemStack stack = inv.getItem(i);

                // Place placeholder
                if (InventoryUtil.isEmpty(stack)) {
                    inv.setItem(i, slot.getPlaceHolder());
                    continue;
                }

                if (SlotType.isPlaceHolder(stack)) {
                    // Re-set placeholder in case it's incorrect
                    inv.setItem(i, slot.getPlaceHolder());
                    continue;
                }

                if (!slot.validate(stack)) {
                    // Move the item somewhere where it fits
                    InventoryUtil.storeInInventory(inv, stack);
                    inv.setItem(i, slot.getPlaceHolder());
                }
            }
        }

    }

    private void delayedFillPlaceholder(final PlayerInventory inv, final SlotType slot, final int index) {
        // This must be done in the next tick.
        // See javadoc InventoryClickEvent#setCursor(ItemStack)
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (InventoryUtil.isEmpty(inv.getItem(index))) {
                    inv.setItem(index, slot.getPlaceHolder());
                }
            }
        });
    }

}
