package me.dirkjan.goldiriath.health;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.player.PlayerData;
import me.dirkjan.goldiriath.util.service.AbstractService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

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

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

        Entity hit = event.getEntity();
        if (!(hit instanceof Player)) {
            return;
        }

        Player player = (Player) hit;
        PlayerData data = plugin.pm.getData(player);
        double health = data.getHealth();

        health -= event.getDamage(); // TODO: Armor, weapon boosts, etc

        if (health <= 0) {
            health = data.getMaxHealth(); // Reset health
        }

        // TODO: use player.setHealthScale(), player.setMaxHealth()
        player.setHealth((health / data.getMaxHealth()) * 10);

        plugin.pm.getData(player).setHealth((int) health);
    }

}
