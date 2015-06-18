package me.dirkjan.goldiriath;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.command.Command_goldiriath;
import me.dirkjan.goldiriath.dialog.DialogManager;
import me.dirkjan.goldiriath.item.ItemStorage;
import me.dirkjan.goldiriath.listener.BlockListener;
import me.dirkjan.goldiriath.listener.PlayerListener;
import me.dirkjan.goldiriath.mobspawn.MobSpawnManager;
import me.dirkjan.goldiriath.player.PlayerManager;
import me.dirkjan.goldiriath.quest.QuestManager;
import net.pravian.bukkitlib.BukkitLib;
import net.pravian.bukkitlib.command.BukkitCommandHandler;
import net.pravian.bukkitlib.config.YamlConfig;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Goldiriath extends JavaPlugin {

    public static Goldiriath plugin;
    public static String name = "";
    public static String buildVersion = "0.0";
    public static String buildNumber = "0";
    public static String buildDate = "";
    //
    public Logger logger;
    public YamlConfig config;
    //
    public PlayerManager pm;
    public ScoreboardHandler sch;
    // Services
    public MobSpawnManager msm;
    public ItemStorage is;
    public QuestManager qm;
    public DialogManager dm;
    //
    public BukkitCommandHandler<Goldiriath> ch;

    @Override
    public void onLoad() {
        plugin = this;
        logger = plugin.getLogger();

        loadBuildProperties();

        config = new YamlConfig(plugin, "config.yml");

        pm = new PlayerManager(plugin);
        sch = new ScoreboardHandler(plugin);

        // Services
        msm = new MobSpawnManager(plugin);
        is = new ItemStorage(plugin);
        qm = new QuestManager(plugin);
        dm = new DialogManager(plugin);

        // Commands
        ch = new BukkitCommandHandler<>(plugin);
    }

    @Override
    public void onEnable() {
        BukkitLib.init(plugin);

        // Load config
        config.load();

        // Start services
        msm.start();
        is.start();
        qm.start();
        dm.start();

        // Register events
        new PlayerListener(plugin).register();
        new BlockListener(plugin).register();

        // Setup command handler
        ch.setCommandLocation(Command_goldiriath.class.getPackage());
    }

    @Override
    public void onDisable() {

        // Save playerdata
        pm.saveAll();

        // Stop services
        msm.stop();
        is.stop();
        qm.stop();
        dm.stop();

        // Unregister events
        HandlerList.unregisterAll(plugin);

        // Cancel running tasks
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return ch.handleCommand(sender, cmd, commandLabel, args);
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

}
