package thirdparty.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
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

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < options.length; i++) {
            if (options[i] != null) {
                inventory.setItem(i, options[i].getStack());
            }
        }
        player.openInventory(inventory);
    }

    public void destroy() {
        HandlerList.unregisterAll(this);

        for (int i = 0; i < options.length; i++) {
            options[i] = null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(name)) {
            return;
        }

        event.setCancelled(true);
        int slot = event.getRawSlot();

        if (slot < 0 || slot >= options.length) {
            return;
        }

        Option option = options[slot];
        if (option == null) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();

        OptionClickEvent optEvent = new OptionClickEvent(event, player, slot, option);
        handler.onOptionClick(optEvent);

        if (optEvent.isClose()) {

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



}
