package me.dirkjan.goldiriath;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.commands.Command_resetquest;
import me.dirkjan.goldiriath.listener.BlockListener;
import me.dirkjan.goldiriath.listener.PlayerListener;
import net.pravian.bukkitlib.BukkitLib;
import net.pravian.bukkitlib.command.BukkitCommandHandler;
import net.pravian.bukkitlib.config.YamlConfig;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Goldiriath extends JavaPlugin {

    public static Goldiriath plugin;
    public static String name = "";
    public static String buildVersion = "0.0";
    public static String buildNumber = "0";
    public static String buildDate = "";
    public Logger logger;
    public YamlConfig config;
    public YamlConfig questConfig;
    public Map<UUID, Stage> questmap;
    public BukkitCommandHandler<Goldiriath> handler;
    public PlayerManager pm;
    public MobSpawnManager msm;
    public ScoreboardHandler sch;
    
    @Override
    public void onLoad() {
        plugin = this;
        logger = plugin.getLogger();

        loadBuildProperties();

        config = new YamlConfig(plugin, "config.yml");

        // TODO: Get rid of this
        questConfig = new YamlConfig(plugin, "quests.yml");
        questmap = new HashMap<>();

        pm = new PlayerManager(plugin);
        msm = new MobSpawnManager(plugin);
        sch = new ScoreboardHandler(plugin);

        handler = new BukkitCommandHandler<>(plugin);
    }

    @Override
    public void onEnable() {
        BukkitLib.init(plugin);

        // Register events
        new PlayerListener(plugin).register();
        new BlockListener(plugin).register();

        // Load configs
        config.load();
        questConfigLoad();

        // Start services
        msm.start();
        
        // Setup command handler
        handler.setCommandLocation(Command_resetquest.class.getPackage());
    }

    @Override
    public void onDisable() {

        // Save configs
        questConfigSave();
        pm.saveAll();

        // Stop services
        msm.stop();
        // Cancel running tasks
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return handler.handleCommand(sender, cmd, commandLabel, args);
    }

    private void loadBuildProperties() {
        try {
            name = plugin.getName();
            buildVersion = plugin.getDescription().getVersion();

            final Properties props = new Properties();
            try (InputStream in = plugin.getResource("appinfo.properties")) {
                props.load(in);
            }

            buildNumber = props.getProperty("program.buildnumber");
            buildDate = props.getProperty("program.builddate");
        } catch (Exception ex) {
            logger.warning("Could not load build  properties!");
            logger.warning(ExceptionUtils.getFullStackTrace(ex));
        }
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

    private void questConfigSave() {
        questConfig.clear();
        ConfigurationSection quests = questConfig.createSection("quests.quest1");
        for (UUID key : questmap.keySet()) {

            Stage stage = questmap.get(key);
            quests.set(key.toString(), stage.toString());

        }
        questConfig.save();
    }

}
