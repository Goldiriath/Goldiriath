package me.dirkjan.goldiriath.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.listener.RegistrableListener;
import me.dirkjan.goldiriath.util.Service;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.LoggerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager extends RegistrableListener implements Service {

    private final Map<UUID, GPlayer> players;

    public PlayerManager(Goldiriath plugin) {
        super(plugin);
        this.players = new HashMap<>();
    }

    @Override
    public void start() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            getPlayer(player, true); // Preload player
        }

        register();
    }

    @Override
    public void stop() {
        saveAll();
        players.clear();
        unregister();
    }

    public PlayerData getData(Player player) {
        final GPlayer gPlayer = getPlayer(player);
        return gPlayer == null ? null : gPlayer.getData();
    }

    public PlayerData getData(Player player, boolean shouldLoad) {
        final GPlayer gPlayer = getPlayer(player, shouldLoad);
        return gPlayer == null ? null : gPlayer.getData();
    }

    public GPlayer getPlayer(Player player) {
        return getPlayer(player, true);
    }

    public GPlayer getPlayer(Player player, boolean shouldLoad) {

        // If the playerdata map has the player stored already, use that
        GPlayer gPlayer = players.get(player.getUniqueId());
        if (gPlayer != null || !shouldLoad) {
            return gPlayer;
        }

        // Create player and config
        gPlayer = new GPlayer(this, player);
        final YamlConfig config = createPlayerConfig(player);

        if (config.exists()) {
            // Existing player
            config.load();
            gPlayer.getData().loadFrom(config);
        } else {
            // New player
            gPlayer.getData().saveTo(config);
            config.save();
        }

        // Store player
        players.put(player.getUniqueId(), gPlayer);

        return gPlayer;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        getPlayer(event.getPlayer(), true); // Preload
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        logout(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKickEvent(PlayerKickEvent event) {
        logout(event.getPlayer());
    }

    private void logout(Player player) {
        savePlayer(player);

        if (players.remove(player.getUniqueId()) == null) {
            LoggerUtils.warning("Could not remove playerdata for player " + player.getName() + ". No playerdata present!");
        }
    }

    public void saveAll() {
        for (GPlayer gPlayer : players.values()) {
            savePlayer(gPlayer.getPlayer());
        }
    }

    public void updateAll() {
        for (GPlayer gPlayer : players.values()) {
            gPlayer.update();
        }
    }

    private void savePlayer(Player player) {
        final PlayerData data = getData(player, false);

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
