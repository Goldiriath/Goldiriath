package net.goldiriath.plugin.game.inventory;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.CraftingInventory;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        validateInventory(event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        validateInventory(event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        // No off hand stuff
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof CraftingInventory)) {
            return;
        }

        if (event.getView().getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        // Debug
        switch (event.getAction()) {

            case UNKNOWN: {
                event.setResult(Event.Result.DENY);
                break;
            }

            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
            case MOVE_TO_OTHER_INVENTORY: {
                // Don't pickup a spellbook, or spells
                ItemStack slot = event.getCurrentItem();
                if (InventoryUtil.isSkillBook(slot)
                        || InventoryUtil.isSkill(slot)) {
                    event.setResult(Event.Result.DENY);
                }
                break;
            }

            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
            case SWAP_WITH_CURSOR: {
                final int index = event.getSlot();
                final SlotType slot = SlotType.ofIndex(index);
                final ItemStack cursor = event.getCursor();
                //String curI = event.getCurrentItem() == null ? "none" : event.getCurrentItem().getType().toString();
                //String curC = event.getCursor() == null ? "none" : event.getCursor().getType().toString();
                //logger.info(event.getClick() + " - " + event.getAction() + ": " + curI + " @ " + curC + " (" + event.getSlot() + ":" + event.getRawSlot() + ")");

                if (!slot.validate(cursor)) {
                    event.setResult(Event.Result.DENY);
                }
                break;
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        final int storeSlot = InventoryUtil.getStoreIndex(event.getPlayer().getInventory(), event.getItem().getItemStack());

        if (storeSlot < 0) {
            // Can't store this item
            event.setCancelled(true);
            return;
        }

        // Put it in the correct place next tick
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                validateInventory(event.getPlayer().getInventory());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDrop(final PlayerDropItemEvent event) {
        ItemStack stack = event.getItemDrop().getItemStack();
        // Don't drop skills or skillbooks
        if (InventoryUtil.isSkill(stack)
                || InventoryUtil.isSkillBook(stack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.PHYSICAL) {
            return;
        }

        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        final ItemStack stack = event.getItem();
        final SlotType slot = SlotType.ofIndex(event.getPlayer().getInventory().getHeldItemSlot());

        // Item is already in a non-any slot
        if (slot == SlotType.ANY) {
            for (SlotType loopSlot : SlotType.values()) {
                if (loopSlot == SlotType.ANY) {
                    continue;
                }

                // Is this slot a non-any slot that can hold the item?
                if (loopSlot.validate(stack)) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You must use this item in the designated slot.");
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
        } else {
            // Does the item belong in this slot?
            if (!slot.validate(stack)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You must use this item in the designated slot.");
                event.setUseItemInHand(Event.Result.DENY);
                event.setCancelled(true);
            }
        }

    }

    private void validateInventory(PlayerInventory inventory) {
        if (inventory.getHolder().getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {

            if (InventoryUtil.isEmpty(contents[i])
                    || SlotType.ofIndex(i).validate(contents[i])) {
                continue;
            }

            InventoryUtil.storeInInventory(inventory, contents[i]);
            inventory.setItem(i, null);
        }

        // Ensure a skillbook is present
        inventory.setItem(SlotType.SKILL_BOOK.getIndices()[0], StaticItem.SKILL_BOOK.getStack());
    }

}
