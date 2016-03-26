package net.goldiriath.plugin;

import net.goldiriath.plugin.ConfigPath;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
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
        if (!plugin.config.getBoolean(ConfigPath.INFINITE_DISPENSER_ENABLED)) {
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
