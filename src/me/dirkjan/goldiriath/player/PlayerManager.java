package me.dirkjan.goldiriath.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.dirkjan.goldiriath.Goldiriath;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.LoggerUtils;
import org.bukkit.entity.Player;

public class PlayerManager {

    private final Goldiriath plugin;
    private final Map<UUID, GPlayer> players;

    public PlayerManager(Goldiriath plugin) {
        this.plugin = plugin;
        this.players = new HashMap<>();
    }

    public Goldiriath getPlugin() {
        return plugin;
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

        // Load data from config
        final YamlConfig config = createPlayerConfig(player);
        if (config.exists()) {
            config.load();
        }

        // Parse data from config
        gPlayer = new GPlayer(this, player);
        gPlayer.getData().loadFrom(config); // TODO: improve playerdata loading
        players.put(player.getUniqueId(), gPlayer);
        return gPlayer;
    }

    public void logout(Player player) { // Should be called when the player logs out

        final PlayerData data = PlayerManager.this.getData(player, false); // false: Don't load the config if it isn't present

        if (data == null) {
            LoggerUtils.warning("Not saving playerdata for player " + player.getName() + ". No playerdata present!");
            return;
        }

        // Save the config
        final YamlConfig config = createPlayerConfig(player);
        data.saveTo(config);
        config.save(); // Note: saveTo() does not actually save the config

        if (players.remove(player.getUniqueId()) == null) {
            LoggerUtils.warning("Could not remove playerdata for player " + player.getName() + ". Playerdata not present!");
        }
    }

    public void saveAll() {
        for (GPlayer gPlayer : players.values()) {
            final YamlConfig config = createPlayerConfig(gPlayer.getPlayer());
            gPlayer.getData().saveTo(config);
            config.save(); // Note: saveTo() does not actually save the config
        }
    }

    public void updateAll() {
        for (GPlayer gPlayer : players.values()) {
            gPlayer.update();
        }
    }

    private YamlConfig createPlayerConfig(Player player) {
        return new YamlConfig(plugin, "players/" + player.getUniqueId() + ".yml", false);
    }

}
