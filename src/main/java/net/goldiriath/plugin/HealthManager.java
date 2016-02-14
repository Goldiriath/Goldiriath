package net.goldiriath.plugin;

import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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

    public void setMaxHealth(Player player, int maxHealth) {
        // Update the health player
        player.setMaxHealth(maxHealth);
        player.setHealthScale(20.0);

        // Set the player's stored max health
        PlayerData data = plugin.pm.getData(player);
        data.setMaxHealth(maxHealth);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.pm.getData(player);

        // Set and scale health
        player.setMaxHealth(data.getMaxHealth());
        player.setHealthScale(20.0);
        player.setHealth(data.getHealth());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        switch (event.getCause()) {

            case STARVATION: {
                // Don't take damage from starvation, mana bar
                event.setCancelled(true);
            }

            case FALL: {
                Player player = (Player) event.getEntity();

                // https://github.com/Goldiriath/Goldiriath/issues
                // De-scale the health
                // Falling damage does the same amount of hearts damage in
                // Goldiriath as in vanilla.
                double descaled = event.getDamage() * (player.getMaxHealth() / 20.0);
                event.setDamage(descaled);
                break;
            }

            // TODO: POISON, DROWNING, what do...
            default: {
                break;
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityHitPlayer(EntityDamageEvent event) {

        final Entity hit = event.getEntity();
        if (!(hit instanceof Player)) {
            return;
        }

        //Player player = (Player) hit;
        double damage = event.getDamage();

        // TODO: calculate armor and weapon damage modifiers, etc
        event.setDamage(damage);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
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
