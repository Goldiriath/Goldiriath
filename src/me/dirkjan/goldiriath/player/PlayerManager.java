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
    private final Map<UUID, PlayerData> datamap;

    public PlayerManager(Goldiriath plugin) {
        this.plugin = plugin;
        this.datamap = new HashMap<>();
    }

    public Goldiriath getPlugin() {
        return plugin;
    }

    public PlayerData getData(Player player) {
        return getData(player, true);
    }

    public PlayerData getData(Player player, boolean shouldLoad) {

        // If the playerdata map has the player stored already, use that
        PlayerData data = datamap.get(player.getUniqueId());
        if (data != null || !shouldLoad) {
            return data;
        }

        // Load data from config
        final YamlConfig config = createPlayerConfig(player);
        if (config.exists()) {
            config.load();
        }

        // Parse data from config
        data = new PlayerData(this, player);
        data.loadFrom(config);
        datamap.put(player.getUniqueId(), data);
        return data;
    }

    public void logout(Player player) { // Should be called when the player logs out

        final PlayerData data = getData(player, false); // false: Don't load the config if it isn't present

        if (data == null) {
            LoggerUtils.warning("Not saving playerdata for player " + player.getName() + ". No playerdata present!");
            return;
        }

        // Save the config
        final YamlConfig config = createPlayerConfig(player);
        data.saveTo(config);
        config.save(); // Note: saveTo() does not actually save the config

        if (datamap.remove(player.getUniqueId()) == null) {
            LoggerUtils.warning("Could not remove playerdata for player " + player.getName() + ". Playerdata not present!");
        }
    }

    public void saveAll() {

        for (PlayerData data : datamap.values()) {
            final YamlConfig config = createPlayerConfig(data.getPlayer());
            data.saveTo(config);
            config.save(); // Note: saveTo() does not actually save the config
        }
    }

    private YamlConfig createPlayerConfig(Player player) {
        return new YamlConfig(plugin, "players/" + player.getUniqueId() + ".yml", false);
    }

}
