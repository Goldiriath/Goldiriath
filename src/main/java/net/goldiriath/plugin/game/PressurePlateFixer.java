package net.goldiriath.plugin.game;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PressurePlateFixer extends AbstractService {

    public PressurePlateFixer(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerStepPlate(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) {
            return;
        }

        Block block = event.getClickedBlock();
        Material type = block.getType();
        if (type != Material.STONE_PLATE && type != Material.WOOD_PLATE) {
            return;
        }

        byte data = block.getData();
        if (data != 0x00 && data != 0x01) {
            event.setCancelled(true);
        }
    }

}
