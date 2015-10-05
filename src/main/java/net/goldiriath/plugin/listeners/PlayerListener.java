package net.goldiriath.plugin.listeners;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.RegistrableListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerListener extends RegistrableListener {

    public PlayerListener(Goldiriath plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() == 0) {
            if (event.getCurrentItem().getData().getItemType() == Material.WOOD_SWORD) {
                player.sendMessage("Wood sword");
            } else if (event.getCurrentItem().getData().getItemType() == Material.STONE_SWORD) {
                player.sendMessage("stone sword");
            } else if (event.getCurrentItem().getData().getItemType() == Material.IRON_SWORD) {
                player.sendMessage("Iron sword");
            } else if (event.getCurrentItem().getData().getItemType() == Material.GOLD_SWORD) {
                player.sendMessage("Gold sword");
            } else if (event.getCurrentItem().getData().getItemType() == Material.DIAMOND_SWORD) {
                player.sendMessage("Diamond sword");
            } else if (event.getCurrentItem().getData().getItemType() == Material.WOOD_AXE) {
                player.sendMessage("Wood axe");
            } else if (event.getCurrentItem().getData().getItemType() == Material.STONE_AXE) {
                player.sendMessage(" Stone axe");
            } else if (event.getCurrentItem().getData().getItemType() == Material.IRON_AXE) {
                player.sendMessage("Iron axe");
            } else if (event.getCurrentItem().getData().getItemType() == Material.GOLD_AXE) {
                player.sendMessage("Gold axe");
            } else if (event.getCurrentItem().getData().getItemType() == Material.DIAMOND_AXE) {
                player.sendMessage("Diamond axe");
            } else if (event.getCurrentItem().getData().getItemType() == Material.BOW) {
                player.sendMessage("Bow");
            } else if (event.getCurrentItem().getData().getItemType() == Material.STICK) {
                player.sendMessage("Stick");
            } else if (event.getCurrentItem().getData().getItemType() == Material.AIR) {
            } else {
                event.getView().close();
                player.sendMessage("Sorry you can not put that item in this slot.");
            }
        }
    }
    
    

}