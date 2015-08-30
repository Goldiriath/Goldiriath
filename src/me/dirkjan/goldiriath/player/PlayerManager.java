package me.dirkjan.goldiriath.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.LoggerUtils;
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
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            getData(player, true); // Preload player
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
            config.load();
            data.getPersistent().loadFrom(config);
        } else {
            // New player
            data.getPersistent().saveTo(config);
            config.save();
        }

        // Store player
        players.put(player.getUniqueId(), data);

        return data;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        getData(event.getPlayer(), true); // Preload
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        savePlayer(event.getPlayer());

        if (players.remove(event.getPlayer().getUniqueId()) == null) {
            LoggerUtils.warning("Could not remove playerdata for player " + event.getPlayer().getName() + ". No playerdata present!");
        }
    }

    public void saveAll() {
        for (PlayerData data : players.values()) {
            savePlayer(data.getPlayer());
        }
    }

    public void updateAll() {
        for (PlayerData data : players.values()) {
            data.update();
        }
    }

    private void savePlayer(Player player) {
        final PersistentData data = getData(player, false).getPersistent();

        if (data == null) {
            LoggerUtils.warning("Not saving playerdata for player " + player.getName() + ". No playerdata present!");
            return;
        }

        // Save the config
        final YamlConfig config = createPlayerConfig(player);
        data.saveTo(config);
        config.save();
    }

    private YamlConfig createPlayerConfig(Player player) {
        return new YamlConfig(plugin, "players/" + player.getUniqueId() + ".yml", false);
    }

}
