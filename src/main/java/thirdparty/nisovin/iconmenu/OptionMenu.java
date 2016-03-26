package thirdparty.nisovin.iconmenu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

// Modified from:
// http://bukkit.org/threads/icon-menu.108342/
public class OptionMenu implements Listener {

    private final String name;
    private final int size;
    private final OptionClickEventHandler handler;
    private final Plugin plugin;
    private final Option[] options;
    //
    private final Map<UUID, InventoryView> inventories = new HashMap<>();

    public OptionMenu(Plugin plugin, String name, int size, OptionClickEventHandler handler) {
        this.plugin = plugin;
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.options = new Option[size];

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public OptionMenu option(int position, ItemStack stack, String name) {
        options[position] = new Option(stack, name);
        return this;
    }

    public void closeAll() {
        for (InventoryView view : inventories.values()) {
            view.close();
        }
        inventories.clear();
    }

    public InventoryView open(Player player) {
        return open(player, true, null);
    }

    public InventoryView open(Player player, boolean top, Inventory other) {
        Inventory shopInventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < options.length; i++) {
            if (options[i] != null) {
                shopInventory.setItem(i, options[i].getStack().clone());
            }
        }

        InventoryView view;
        if (top && other == null) {
            view = player.openInventory(shopInventory);
        } else {
            Inventory topInv = top ? shopInventory : other;
            Inventory bottomInv = top ? other : shopInventory;

            view = new SimpleInventoryView(player, topInv, bottomInv);
            player.openInventory(view);
        }

        inventories.put(player.getUniqueId(), view);
        return view;
    }

    public void destroy() {
        closeAll();

        HandlerList.unregisterAll(this);

        for (int i = 0; i < options.length; i++) {
            options[i] = null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        inventories.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        inventories.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView stored = inventories.get(event.getWhoClicked().getUniqueId());
        if (stored == null || !stored.equals(event.getView())) {
            return;
        }

        event.setCancelled(true);
        int rawSlot = event.getRawSlot();

        final Option option = rawSlot < 0 || rawSlot >= options.length ? null : options[rawSlot];
        final Player player = (Player) event.getWhoClicked();

        final OptionClickEvent optEvent = new OptionClickEvent(event, player, rawSlot, this, option);
        handler.onOptionClick(optEvent);

        // Handle post-event
        if (optEvent.isClose() || optEvent.isDestroy()) {

            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();
                }
            });
        }

        if (optEvent.isDestroy()) {
            destroy();
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + this.size;
        hash = 89 * hash + Arrays.deepHashCode(this.options);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OptionMenu other = (OptionMenu) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        return Arrays.deepEquals(this.options, other.options);
    }

}
