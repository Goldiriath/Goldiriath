package net.goldiriath.plugin;

import net.goldiriath.plugin.wand.WandBasicAttack;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import net.goldiriath.plugin.command.Command_goldiriath;
import net.goldiriath.plugin.game.AutoClose;
import net.goldiriath.plugin.game.DevMode;
import net.goldiriath.plugin.game.EffectsTicker;
import net.goldiriath.plugin.game.InfiDispenser;
import net.goldiriath.plugin.game.MetaCycler;
import net.goldiriath.plugin.game.PressurePlateFixer;
import net.goldiriath.plugin.game.XPManager;
import net.goldiriath.plugin.game.damage.ArrowHitTracker;
import net.goldiriath.plugin.game.damage.AttackManager;
import net.goldiriath.plugin.game.damage.DeathManager;
import net.goldiriath.plugin.game.damage.HealthManager;
import net.goldiriath.plugin.game.inventory.InventoryManager;
import net.goldiriath.plugin.game.item.ItemManager;
import net.goldiriath.plugin.game.loot.LootManager;
import net.goldiriath.plugin.game.mobspawn.MobSpawnManager;
import net.goldiriath.plugin.game.questing.dialog.DialogManager;
import net.goldiriath.plugin.game.questing.quest.QuestManager;
import net.goldiriath.plugin.game.shop.ShopManager;
import net.goldiriath.plugin.game.skill.SkillManager;
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
    public ItemManager im;
    public PlayerManager pm;
    public XPManager xm;
    public DevMode dev;
    public ShopManager sh;
    public QuestManager qm;
    public DialogManager dlm;
    public LootManager lm;
    public MobSpawnManager msm;
    public HealthManager hm;
    public AttackManager bm;
    public DeathManager dm;
    public SidebarManager sb;
    public SkillManager sm;
    public EffectsTicker et;
    public EffectLibBridge elb;
    public WandBasicAttack wba;
    public ArrowHitTracker at;
    public InventoryManager iv;
    public MetaCycler ms;
    public AutoClose ac;
    public InfiDispenser id;
    public PressurePlateFixer pf;
    //
    public AeroCommandHandler<Goldiriath> ch;

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
        im = services.registerService(ItemManager.class);
        pm = services.registerService(PlayerManager.class);
        xm = services.registerService(XPManager.class);
        dev = services.registerService(DevMode.class);
        sh = services.registerService(ShopManager.class);
        qm = services.registerService(QuestManager.class);
        dlm = services.registerService(DialogManager.class);
        lm = services.registerService(LootManager.class);
        msm = services.registerService(MobSpawnManager.class);
        hm = services.registerService(HealthManager.class);
        bm = services.registerService(AttackManager.class);
        dm = services.registerService(DeathManager.class);
        sb = services.registerService(SidebarManager.class);
        sm = services.registerService(SkillManager.class);
        et = services.registerService(EffectsTicker.class);
        elb = services.registerService(EffectLibBridge.class);
        wba = services.registerService(WandBasicAttack.class);
        at = services.registerService(ArrowHitTracker.class);
        iv = services.registerService(InventoryManager.class);
        ms = services.registerService(MetaCycler.class);
        ac = services.registerService(AutoClose.class);
        id = services.registerService(InfiDispenser.class);
        pf = services.registerService(PressurePlateFixer.class);

        // Commands
        ch = new SimpleCommandHandler<>(plugin);
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
        ch.setCommandClassPrefix("Command_");
        ch.loadFrom(Command_goldiriath.class.getPackage());
        ch.registerAll("goldiriath", true);

        logger.info(getName() + " v" + getDescription().getVersion() + "-" + buildVersion + " by Prozza and derpfacedirk is enabled");
    }

    @Override
    public void disable() {

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
