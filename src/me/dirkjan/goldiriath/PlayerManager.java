package me.dirkjan.goldiriath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.dirkjan.goldiriath.skills.Skill;
import net.pravian.bukkitlib.config.YamlConfig;
import org.bukkit.entity.Player;

public class PlayerManager {

    private final Goldriath plugin;
    private final Map<UUID, PlayerData> datamap;

    public PlayerManager(Goldriath plugin) {
        this.plugin = plugin;
        this.datamap = new HashMap<>();
    }

    public PlayerData getData(Player player) {
        PlayerData data = datamap.get(player.getUniqueId());
        if (data != null) {
            return data;
        }
        data = new PlayerData(player);
        final YamlConfig config = new YamlConfig(plugin, "players/" + player.getUniqueId() + ".yml", false);
        data.getSkills();
        return data;
    }

    public static class PlayerData implements ConfigSavable {

        private final Player player;
        private final Set<Skill> skills;

        private PlayerData(Player player) {
            this.player = player;
            this.skills = new HashSet<>();
        }

        public Player getPlayer() {
            return player;
        }

        public Set<Skill> getSkills() {
            return skills;
        }

        @Override
        public void loadFrom(YamlConfig config) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void saveTo(YamlConfig config) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
