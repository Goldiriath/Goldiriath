package net.goldiriath.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import thirdparty.com.virtivia.minecraft.bukkitplugins.dropprotect.InventorySnapshot;

public class DeathManager extends AbstractService {

    private final Map<UUID, InventorySnapshot> deathInventories = new HashMap<>();
    private int cost;
    private double multiplier;

    public DeathManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        this.cost = plugin.config.getInt(ConfigPath.DEATH_MONEY_COST);
        this.multiplier = plugin.config.getDouble(ConfigPath.DEATH_MONEY_MULTIPLIER);
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

        if (cost > 0) {
            PlayerData data = plugin.pm.getData(player);
            int money = data.getMoney();
            if (money < cost) {
                player.sendMessage(ChatColor.RED + "You lost your items because you didn't have enough money.");
                return;
            }

            money -= cost;
            money *= multiplier;

            data.setMoney(money);
        }

        // TODO: 1.7.10 doesn't have the following, use it when we update
        //event.setKeepInventory(true);
        // https://github.com/Bukkit/Bukkit/commit/e0dc9470efa3487c4d00a67a4d62de6d05d4985a
        InventorySnapshot snapshot = new InventorySnapshot(player, event.getDrops());

        // Add the snapshot to load after the player respawns
        deathInventories.put(player.getUniqueId(), snapshot);
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        InventorySnapshot snapshot = deathInventories.remove(player.getUniqueId());
        if (snapshot == null) {
            return;
        }

        // Merge the snapshot into the player's inventory, dropping overflow items at the respawn location
        snapshot.mergeIntoPlayerInventory(player, event.getRespawnLocation());
    }

}
