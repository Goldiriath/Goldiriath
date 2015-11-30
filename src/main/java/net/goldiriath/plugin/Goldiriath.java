package net.goldiriath.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import net.goldiriath.plugin.autoclose.AutoClose;
import net.goldiriath.plugin.command.Command_goldiriath;
import net.goldiriath.plugin.dialog.DialogManager;
import net.goldiriath.plugin.infidispenser.InfiDispenser;
import net.goldiriath.plugin.item.ItemManager;
import net.goldiriath.plugin.item.ItemMetaManager;
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
    public File dataLoadFolder;
    //
    protected ServiceManager services;
    public PlayerManager pm;
    public QuestManager qm;
    public DialogManager dm;
    public MobSpawnManager msm;
    public ItemManager im;
    public HeartBeat hb;
    public MetaCycler ms;
    public AutoClose ac;
    public InfiDispenser id;
    //
    public BukkitCommandHandler<Goldiriath> ch;

    @Override
    public void onLoad() {
        plugin = this;
        logger = new BukkitLogger(plugin);

        loadBuildProperties();

        config = new YamlConfig(plugin, "config.yml");
        dataLoadFolder = new File(plugin.getDataFolder(), "data");

        // Services
        services = new ServiceManager(plugin);
        im = services.registerService(ItemManager.class);
        pm = services.registerService(PlayerManager.class);
        qm = services.registerService(QuestManager.class);
        dm = services.registerService(DialogManager.class);
        msm = services.registerService(MobSpawnManager.class);
        hb = services.registerService(HeartBeat.class);
        ms = services.registerService(MetaCycler.class);
        ac = services.registerService(AutoClose.class);
        id = services.registerService(InfiDispenser.class);

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
        services.start();

        // Setup command handler
        ch.setCommandLocation(Command_goldiriath.class.getPackage());

        logger.info(getName() + " v" + getDescription().getVersion() + "-" + buildVersion + " by derfacedirk and Prozza is enabled.");
    }

    @Override
    public void onDisable() {

        // Stop services
        services.stop();

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
