package net.goldiriath.plugin.game;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class BowAttack extends AbstractService {

    public BowAttack(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (event.getItem().getType() != Material.BOW) {
            return;
        }

        // Show a bow arrow
        event.setCancelled(true);
        Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);
        arrow.setCritical(false);
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        // Don't pick up arrows
        if (event.getItem().getItemStack().getType() == Material.ARROW) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();

        event.setCancelled(true);

        if (!(arrow.getShooter() instanceof Player)) {
            // TODO
            logger.warning("Damage handling for mobs shooting bows is not implemented!");
            return;
        }

        // Critical arrows are shot by skills, don't do damage
        if (!arrow.isCritical()) {
            return;
        }

        Player player = (Player) arrow.getShooter();

        // TODO: Handle if the player switches the item in hand
        plugin.bm.attack(player, player.getItemInHand(), arrow);
    }
}
