package net.goldiriath.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import net.goldiriath.plugin.command.Command_goldiriath;
import net.goldiriath.plugin.game.AutoClose;
import net.goldiriath.plugin.game.BlockCycler;
import net.goldiriath.plugin.game.DevMode;
import net.goldiriath.plugin.game.EffectLibBridge;
import net.goldiriath.plugin.game.EffectsTicker;
import net.goldiriath.plugin.game.InfiDispenser;
import net.goldiriath.plugin.game.SidebarManager;
import net.goldiriath.plugin.game.XPManager;
import net.goldiriath.plugin.game.citizens.CitizensBridge;
import net.goldiriath.plugin.game.damage.ArrowHitTracker;
import net.goldiriath.plugin.game.damage.DamageManager;
import net.goldiriath.plugin.game.damage.DeathManager;
import net.goldiriath.plugin.game.inventory.InventoryManager;
import net.goldiriath.plugin.game.item.ItemManager;
import net.goldiriath.plugin.game.loot.LootManager;
import net.goldiriath.plugin.game.mobspawn.MobSpawnManager;
import net.goldiriath.plugin.game.questing.dialog.DialogManager;
import net.goldiriath.plugin.game.questing.quest.QuestManager;
import net.goldiriath.plugin.game.shop.ShopManager;
import net.goldiriath.plugin.game.skill.SkillManager;
import net.goldiriath.plugin.game.wand.WandBasicAttack;
import net.goldiriath.plugin.player.PlayerManager;
import net.goldiriath.plugin.util.PlayerList;
import net.goldiriath.plugin.util.logging.GLogger;
import net.goldiriath.plugin.util.logging.PlayerListLogSink;
import net.pravian.aero.command.handler.AeroCommandHandler;
import net.pravian.aero.command.handler.SimpleCommandHandler;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.plugin.AeroPlugin;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class Goldiriath extends AeroPlugin<Goldiriath> {

    public static String name = "";
    public static String buildVersion = "";
    public static String buildDate = "";
    //
    public GLogger logger;
    public final PlayerList loggerPlayers = new PlayerList();
    public YamlConfig config;
    public File dataLoadFolder;
    //
    protected ServiceManager services;
    public ItemManager itm;
    public PlayerManager pym;
    public XPManager xpm;
    public DevMode dev;
    public ShopManager shm;
    public QuestManager qst;
    public DialogManager dlm;
    public LootManager ltm;
    public CitizensBridge czb;
    public MobSpawnManager msm;
    public DamageManager dam;
    public DeathManager dth;
    public SidebarManager sbm;
    public SkillManager skl;
    public EffectsTicker eft;
    public EffectLibBridge elb;
    public WandBasicAttack wba;
    public ArrowHitTracker aht;
    public InventoryManager ivt;
    public BlockCycler bcl;
    public AutoClose atc;
    public InfiDispenser ifd;
    //
    public AeroCommandHandler<Goldiriath> commands;

    @Override
    public void load() {

        // Setup logger
        logger = new GLogger(getPluginLogger());
        logger.addSink(new PlayerListLogSink(loggerPlayers));

        loadBuildProperties();

        config = new YamlConfig(plugin, "config.yml");
        dataLoadFolder = new File(plugin.getDataFolder(), "data");

        // Services
        services = new ServiceManager(plugin);
        itm = services.registerService(ItemManager.class);
        pym = services.registerService(PlayerManager.class);
        xpm = services.registerService(XPManager.class);
        dev = services.registerService(DevMode.class);
        shm = services.registerService(ShopManager.class);
        qst = services.registerService(QuestManager.class);
        dlm = services.registerService(DialogManager.class);
        ltm = services.registerService(LootManager.class);
        czb = services.registerService(CitizensBridge.class);
        msm = services.registerService(MobSpawnManager.class);
        dam = services.registerService(DamageManager.class);
        dth = services.registerService(DeathManager.class);
        sbm = services.registerService(SidebarManager.class);
        skl = services.registerService(SkillManager.class);
        eft = services.registerService(EffectsTicker.class);
        elb = services.registerService(EffectLibBridge.class);
        wba = services.registerService(WandBasicAttack.class);
        aht = services.registerService(ArrowHitTracker.class);
        ivt = services.registerService(InventoryManager.class);
        bcl = services.registerService(BlockCycler.class);
        atc = services.registerService(AutoClose.class);
        ifd = services.registerService(InfiDispenser.class);

        // Commands
        commands = new SimpleCommandHandler<>(plugin);
    }

    @Override
    public void enable() {
        // Load config
        config.load();

        // Create data folder
        dataLoadFolder.mkdirs();

        // Start services
        services.start();

        // Setup command handler
        commands.setCommandClassPrefix("Command_");
        commands.loadFrom(Command_goldiriath.class.getPackage());
        commands.registerAll("goldiriath", true);

        logger.info(getName() + " v" + getDescription().getVersion() + "-" + buildVersion + " by Prozza and derpfacedirk is enabled");
    }

    @Override
    public void disable() {
        // Unregister commands
        commands.clearCommands();

        // Stop services
        services.stop();

        // Unregister events
        HandlerList.unregisterAll((Plugin) plugin);

        // Cancel running tasks
        plugin.getServer().getScheduler().cancelTasks(plugin);
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

    public static Goldiriath instance() {
        return (Goldiriath) Bukkit.getServer().getPluginManager().getPlugin("Goldiriath");
    }

}
