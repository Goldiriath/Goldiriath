package net.goldiriath.plugin.game;

import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InfiDispenser extends AbstractService {

    public InfiDispenser(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler
    public void onDispenserFire(BlockDispenseEvent event) {
        if (!plugin.config.getBoolean(ConfigPaths.INFINITE_DISPENSER_ENABLED)) {
            return;
        }

        if (!(event.getBlock().getState() instanceof Dispenser)) {
            return;
        }

        final Dispenser disp = (Dispenser) event.getBlock().getState();
        final ItemStack item = event.getItem().clone();
        item.setAmount(item.getMaxStackSize());
        for (int i = 0; i < disp.getInventory().getSize(); i++) {
            disp.getInventory().setItem(i, item);
        }
        disp.update();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDispenserOpen(InventoryOpenEvent event) {
        HumanEntity entity = event.getPlayer();
        if (entity.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        switch (event.getInventory().getType()) {
            case DISPENSER:
            case CHEST:
            case FURNACE:
            case ENCHANTING:
            case ENDER_CHEST:
            case ANVIL:
            case MERCHANT:
            case BEACON:
            case HOPPER:
            case DROPPER:
            case BREWING:
                player.sendMessage(ChatColor.RED + "You cannot open this.");
                break;
        }
    }

}
