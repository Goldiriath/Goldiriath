package net.goldiriath.plugin.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.aero.config.YamlConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager extends AbstractService {

    private final Map<UUID, PlayerData> players;

    public PlayerManager(Goldiriath plugin) {
        super(plugin);
        this.players = new HashMap<>();
    }

    @Override
    public void onStart() {
        players.clear();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            getData(player, true); // Preload player
        }
        if (players.size() > 0) {
            logger.info("Precached playerdata for " + players.size() + " players");
        }
    }

    @Override
    public void onStop() {
        saveAll();
        players.clear();
    }

    public PlayerData getData(PlayerEvent event) {
        return getData(event.getPlayer());
    }

    public PlayerData getData(Player player) {
        return getData(player, true);
    }

    public PlayerData getData(Player player, boolean shouldLoad) {

        // If the playerdata map has the player stored already, use that
        PlayerData data = players.get(player.getUniqueId());
        if (data != null || !shouldLoad) {
            return data;
        }

        // Create player and config
        data = new PlayerData(this, player);
        final YamlConfig config = createPlayerConfig(player);

        if (config.exists()) {
            // Existing player
            logger.info("Loading player data: " + player.getName());
            config.load();
            data.loadFrom(config);
        } else {
            // New player
            logger.info("Creating player data: " + player.getName());
            data.saveTo(config);
            config.save();
        }

        // Store player
        players.put(player.getUniqueId(), data);

        return data;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        getData(event.getPlayer(), true); // Preload
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        savePlayer(event.getPlayer());

        if (players.remove(event.getPlayer().getUniqueId()) == null) {
            logger.warning("Could not remove playerdata for player " + event.getPlayer().getName() + ". No playerdata present!");
        }
    }

    public void saveAll() {
        for (PlayerData data : players.values()) {
            savePlayer(data.getPlayer());
        }
    }

    private void savePlayer(Player player) {
        final PlayerData data = getData(player, false);

        if (data == null) {
            logger.warning("Not saving playerdata for player " + player.getName() + ". No playerdata present!");
            return;
        }

        // Save the config
        final YamlConfig config = createPlayerConfig(player);
        data.saveTo(config);
        config.save();
    }

    private YamlConfig createPlayerConfig(Player player) {
        return new YamlConfig(plugin, "data/players/" + player.getUniqueId() + ".yml", false);
    }

}
