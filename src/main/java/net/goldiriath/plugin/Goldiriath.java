package net.goldiriath.plugin;

import java.io.InputStream;
import java.util.Properties;
import net.goldiriath.plugin.autoclose.AutoClose;
import net.goldiriath.plugin.command.Command_goldiriath;
import net.goldiriath.plugin.dialog.DialogManager;
import net.goldiriath.plugin.infidispenser.InfiDispenser;
import net.goldiriath.plugin.item.ItemStorage;
import net.goldiriath.plugin.metacycler.MetaCycler;
import net.goldiriath.plugin.mobspawn.MobSpawnManager;
import net.goldiriath.plugin.player.PlayerManager;
import net.goldiriath.plugin.quest.QuestManager;
import net.pravian.bukkitlib.BukkitLib;
import net.pravian.bukkitlib.command.BukkitCommandHandler;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.implementation.BukkitLogger;
import net.pravian.bukkitlib.implementation.BukkitPlugin;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class Goldiriath extends BukkitPlugin {

    public static Goldiriath plugin;
    public static String name = "";
    public static String buildVersion = "";
    public static String buildDate = "";
    //
    public BukkitLogger logger;
    public YamlConfig config;
    //
    public PlayerManager pm;

    // Services
    public InfiDispenser id;
    public MobSpawnManager msm;
    public ItemStorage is;
    public QuestManager qm;
    public DialogManager dm;
    public HeartBeat hb;
    public MetaCycler ms;
    public AutoClose ac;
    //
    public BukkitCommandHandler<Goldiriath> ch;

    @Override
    public void onLoad() {
        plugin = this;
        logger = new BukkitLogger(plugin);

        loadBuildProperties();

        config = new YamlConfig(plugin, "config.yml");

        // Services
        id = new InfiDispenser(plugin);
        pm = new PlayerManager(plugin);
        msm = new MobSpawnManager(plugin);
        is = new ItemStorage(plugin);
        qm = new QuestManager(plugin);
        dm = new DialogManager(plugin);
        hb = new HeartBeat(plugin);
        ms = new MetaCycler(plugin);
        ac = new AutoClose(plugin);

        // Commands
        ch = new BukkitCommandHandler<>(plugin);
    }

    @Override
    public void onEnable() {
        plugin = this;
        BukkitLib.init(plugin);

        // Load config
        config.load();

        // Start services
        id.start();
        pm.start();
        msm.start();
        is.start();
        qm.start();
        dm.start();
        hb.start();
        ms.start();
        ac.start();

        // Setup command handler
        ch.setCommandLocation(Command_goldiriath.class.getPackage());

        logger.info(getName() + " v" + getDescription().getVersion() + "-" + buildVersion + " by derfacedirk and Prozza is enabled.");
    }

    @Override
    public void onDisable() {

        // Stop services
        id.stop();
        pm.stop();
        msm.stop();
        is.stop();
        qm.stop();
        dm.stop();
        hb.stop();
        ms.stop();
        ac.stop();

        // Unregister events
        HandlerList.unregisterAll(plugin);

        // Cancel running tasks
        plugin.getServer().getScheduler().cancelTasks(plugin);

        // Free plugin
        plugin = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return ch.handleCommand(sender, cmd, commandLabel, args);
    }

    private void loadBuildProperties() {
        try {
            name = plugin.getName();

            final Properties props = new Properties();
            try (InputStream in = plugin.getResource("appinfo.properties")) {
                props.load(in);
            }

            buildVersion = props.getProperty("program.buildversion");
            buildDate = props.getProperty("program.builddate");
        } catch (Exception ex) {
            logger.warning("Could not load build  properties!");
            logger.warning(ExceptionUtils.getFullStackTrace(ex));
        }
    }

}
