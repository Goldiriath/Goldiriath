package net.goldiriath.plugin.util;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

// http://bukkit.org/threads/icon-menu.108342/
public class IconMenu implements Listener {

    private final String title;
    private final int size;
    private OptionClickEventHandler handler;
    private Plugin plugin;

    private String[] optionNames;
    private ItemStack[] optionIcons;

    public IconMenu(String title, int size, OptionClickEventHandler handler, Plugin plugin) {
        this.title = title;
        this.size = size;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public IconMenu setOption(int position, ItemStack icon, String name, String... info) {
        optionNames[position] = name;
        optionIcons[position] = icon;
        return this;
    }

    public ItemStack[] getContents() {
        return optionIcons;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, size, title);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.closeInventory();
        player.openInventory(inventory);
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        // TODO: Fix this for multiplayer compatibility
        if (event.getInventory().getTitle().equals(title)) {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            String name = slot < 0 || slot >= optionNames.length || optionNames[slot] == null ? "" : optionNames[slot];

            OptionClickEvent e = new OptionClickEvent(
                    this,
                    (Player) event.getWhoClicked(),
                    name,
                    event);
            handler.onOptionClick(e);

            if (e.willClose()) {
                final Player p = (Player) event.getWhoClicked();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        p.closeInventory();
                    }
                }, 1);
            }

            if (e.willDestroy()) {
                destroy();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryDrag(InventoryDragEvent event) {
        handler.onOptionDrag(event);
    }

    public interface OptionClickEventHandler {

        public void onOptionClick(OptionClickEvent event);

        public void onOptionDrag(InventoryDragEvent event);

    }

    public class OptionClickEvent {

        private final IconMenu menu;
        private final Player player;
        private final String name;
        private final InventoryClickEvent event;
        //
        private boolean close;
        private boolean destroy;

        public OptionClickEvent(IconMenu menu, Player player, String name, InventoryClickEvent event) {
            this.menu = menu;
            this.player = player;
            this.name = name;
            this.event = event;

            this.close = false;
            this.destroy = false;
        }

        public IconMenu getMenu() {
            return menu;
        }

        public Player getPlayer() {
            return player;
        }

        public String getName() {
            return name;
        }

        public boolean willClose() {
            return close;
        }

        public boolean willDestroy() {
            return destroy;
        }

        public InventoryClickEvent getEvent() {
            return event;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }

    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

}
