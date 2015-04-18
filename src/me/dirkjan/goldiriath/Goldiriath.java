package me.dirkjan.goldiriath;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.commands.Command_resetquest;
import net.pravian.bukkitlib.BukkitLib;
import net.pravian.bukkitlib.command.BukkitCommandHandler;
import net.pravian.bukkitlib.config.YamlConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Goldiriath extends JavaPlugin {

    public static Goldiriath plugin;
    public Logger logger;
    public YamlConfig config;
    public YamlConfig questConfig;
    public Map<UUID, Stage> questmap;
    public BukkitCommandHandler<Goldiriath> handler;
    public PlayerManager pm;
    public MobSpawner ms;

    @Override
    public void onLoad() {
        plugin = this;
        logger = plugin.getLogger();

        config = new YamlConfig(plugin, "config.yml");

        // TODO: Get rid of this
        questConfig = new YamlConfig(plugin, "quests.yml");
        questmap = new HashMap<>();

        pm = new PlayerManager(plugin);
        ms = new MobSpawner(plugin);

        handler = new BukkitCommandHandler<>(plugin);
    }

    @Override
    public void onEnable() {
        BukkitLib.init(plugin);

        // Register events
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(plugin), plugin);

        // Load configs
        config.load();
        questConfigLoad();

        // Start services
        ms.start();

        // Setup command handler
        handler.setCommandLocation(Command_resetquest.class.getPackage());
    }

    @Override
    public void onDisable() {

        // Save configs
        questSave();
        pm.saveAll();

        // Stop services
        ms.stop();

        // Cancel running tasks
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return handler.handleCommand(sender, cmd, commandLabel, args);
    }

    private void questConfigLoad() {
        questmap.clear();
        questConfig.load();
        //quests:
        // quest1:
        //  [uuidhere]: stage-a
        //  [uuidhere2]: stage-b
        if (!questConfig.isConfigurationSection("quests.quest1")) {
            return;
        }
        ConfigurationSection quests = questConfig.getConfigurationSection("quests.quest1");
        for (String UUIDString : quests.getKeys(false)) {

            UUID uuid;
            try {
                uuid = UUID.fromString(UUIDString);
            } catch (IllegalArgumentException exception) {
                continue;
            }

            Stage stage = Stage.fromString(quests.getString(UUIDString));
            if (stage == null) {
                continue;
            }

            questmap.put(uuid, stage);

        }
    }

    private void questSave() {
        questConfig.clear();
        ConfigurationSection quests = questConfig.createSection("quests.quest1");
        for (UUID key : questmap.keySet()) {

            Stage stage = questmap.get(key);
            quests.set(key.toString(), stage.toString());

        }
        questConfig.save();
    }

}
