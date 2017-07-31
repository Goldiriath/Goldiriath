package net.goldiriath.plugin.game.damage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.goldiriath.plugin.ConfigPaths;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathManager extends AbstractService {

    private int deathCost;
    private double multiplier;

    public DeathManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        this.deathCost = plugin.config.getInt(ConfigPaths.DEATH_MONEY_COST);
        this.multiplier = plugin.config.getDouble(ConfigPaths.DEATH_MONEY_MULTIPLIER);
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        final Player player = event.getEntity();

        event.setKeepLevel(true);
        event.setDroppedExp(0);

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

        PlayerData data = plugin.pm.getData(player);
        int money = data.getMoney();

        money *= multiplier;
        data.setMoney(money);

        if (deathCost > 0) {
            if (money < deathCost) {
                player.sendMessage(ChatColor.RED + "You lost your items because you didn't have enough money.");
                event.setKeepInventory(false);
                return;
            }

            money -= deathCost;
            data.setMoney(money);
        }

        // Keep the player's inventory
        event.setKeepInventory(true);
        event.getDrops().clear();
    }

}
