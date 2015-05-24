package me.dirkjan.goldiriath.listener;

import me.dirkjan.goldiriath.ConfigPaths;
import me.dirkjan.goldiriath.Goldiriath;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener extends RegistrableListener {

    public BlockListener(Goldiriath plugin) {
        super(plugin);
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

}
