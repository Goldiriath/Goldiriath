package net.goldiriath.plugin.health;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class HealthManager extends AbstractService {

    public HealthManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    // TODO: Health implementation.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        final EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

        if (!(entityEvent.getDamager() instanceof Player)) {
            return;
        }

        plugin.pm.getData((Player) entityEvent.getDamager()).recordKill(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void calculateDamage(EntityDamageEvent event) {

        final Entity hit = event.getEntity();
        if (!(hit instanceof Player)) {
            return;
        }

        //Player player = (Player) hit;
        double damage = event.getDamage();

        // TODO: calculate armor and weapon damage modifiers, etc
        event.setDamage(damage);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void applyDamage(EntityDamageEvent event) {

        final Entity hit = event.getEntity();
        if (!(hit instanceof Player)) {
            return;
        }

        Player player = (Player) hit;
        PlayerData data = plugin.pm.getData(player);

        double health = data.getHealth() - event.getDamage();
        if (health <= 0) {
            health = 0;
        }

        // TODO: use player.setHealthScale(), player.setMaxHealth()
        player.setHealth((health / data.getMaxHealth()) * 10);

        plugin.pm.getData(player).setHealth((int) health);
    }

    public void playerDeath(PlayerDeathEvent event) {
        event.setKeepLevel(true);
        event.setDroppedExp(0);

        // TODO: Subtact money, drop inventory if the player is out

        // TODO: 1.7.10 doesn't have this...
        // https://github.com/Bukkit/Bukkit/commit/e0dc9470efa3487c4d00a67a4d62de6d05d4985a
        //event.setKeepInventory(true);

        // Reset player's health
        final PlayerData data = plugin.pm.getData(event.getEntity());
        data.setHealth(data.getMaxHealth());
    }

}
