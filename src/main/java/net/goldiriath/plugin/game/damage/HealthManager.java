package net.goldiriath.plugin.game.damage;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

public class HealthManager extends AbstractService {

    private final AutoHealer autoHealer;
    private BukkitTask healTask;

    public HealthManager(Goldiriath plugin) {
        super(plugin);
        this.autoHealer = new AutoHealer();
    }

    @Override
    protected void onStart() {
        healTask = plugin.getServer().getScheduler().runTaskTimer(plugin, autoHealer, 20, 20);
    }

    @Override
    protected void onStop() {
        Util.cancel(healTask);
        healTask = null;
    }

    //
    // TODO: Most of this is wrong
    //
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
                // Don't take damage from starvation
                event.setCancelled(true);
                break;
            }

            case FALL: {
                Player player = (Player) event.getEntity();

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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Reset player's health
        final PlayerData data = plugin.pm.getData(event.getEntity());
        data.setHealth(data.getMaxHealth());
    }

    public void damage(Player player, int amount) {
        PlayerData data = plugin.pm.getData(player);
        int newHealth = data.getHealth() - amount;
        if (newHealth < 0) {
            player.setHealth(0);
        } else {
            player.setHealth(newHealth);
        }
    }

    public void heal(Player player, int amount) {
        PlayerData data = plugin.pm.getData(player);
        int newHealth = data.getHealth() + amount;
        int maxHealth = data.getMaxHealth();
        data.setHealth(newHealth > maxHealth ? maxHealth : newHealth);
    }

    public class AutoHealer implements Runnable {

        @Override
        public void run() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                plugin.pm.getData(player).getBattle().autoHeal();
            }
        }

    }

}
