package net.goldiriath.plugin.mobspawn;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Getter;
import net.goldiriath.plugin.ConfigPaths;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.mobspawn.citizens.CitizensBridge;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.LocationUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dispenser;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MobSpawnManager extends AbstractService {

    private final YamlConfig profileConfig;
    private final YamlConfig spawnConfig;
    @Getter
    private final CitizensBridge bridge;
    @Getter
    private final Set<MobSpawnProfile> profiles = Sets.newHashSet();
    @Getter
    private final Set<MobSpawn> spawns = Sets.newHashSet();
    //
    private boolean enabled = false;
    @Getter
    private boolean devMode = false;
    @Getter
    private int maxMobs;
    @Getter
    private int timeThreshold;
    @Getter
    private int radiusSquaredThreshold;
    private BukkitTask spawnTask;

    public MobSpawnManager(Goldiriath plugin) {
        super(plugin);
        this.profileConfig = new YamlConfig(plugin, "profiles.yml", true);
        this.spawnConfig = new YamlConfig(plugin, "mobspawns.yml", false);
        this.bridge = new CitizensBridge(plugin);
        this.devMode = false;
    }

    public int killAll() {
        int killed = 0;

        for (MobSpawn spawn : spawns) {
            killed += spawn.killAll();
        }

        return killed;
    }

    public void addSpawn(MobSpawn spawn) {
        if (!spawn.isValid()) {
            plugin.logger.warning("Could not add mobspawn: " + spawn.getId() + ". Mobspawn not valid");
            return;
        }

        spawns.add(spawn);
        saveConfig();
    }

    public void removeSpawn(MobSpawn spawn) {
        if (!spawns.remove(spawn)) {
            plugin.logger.warning("Could not remove mobspawn: " + spawn.getId() + ". Mobspawn not present!");
            return;
        }

        if (spawn.getId() != null) {
            spawnConfig.set(spawn.getId(), null);
        }
        saveConfig();
    }

    @Override
    public void onStart() {
        if (spawnTask != null) {
            onStop();
        }

        loadConfig();

        // Update sign visibility
        setDevMode(devMode);

        if (!enabled) {
            return;
        }

        bridge.start();

        // Start ticking
        spawnTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (devMode) {
                    return;
                }

                for (MobSpawn spawn : spawns) {
                    spawn.tick();
                }
            }
        }.runTaskTimer(plugin, 2, 2); // Run every other tick

        logger.info("Loaded " + profiles.size() + " mobspawn profiles for + " + spawns.size() + " mobspawns");
    }

    @Override
    public void onStop() {
        saveConfig();

        bridge.stop();

        try {
            spawnTask.cancel();
        } catch (Exception ignored) {
        }

        // Kill spawned entities
        killAll();
    }

    // Listeners
    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        final Player player = event.getPlayer();

        if (!event.getLine(0).equalsIgnoreCase("[mobspawn]")) {
            return;
        }

        // Ensure player has the required permission
        if (!event.getPlayer().hasPermission("goldiriath.mobspawn")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not have permission to create MobSpawn signs!");
            return;
        }

        // Validate name
        final String id = event.getLine(1).toLowerCase();
        if (id.length() < 2) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "MobSpawn id it too short!");
            return;
        }
        for (MobSpawn spawn : spawns) {
            if (spawn.getId().equalsIgnoreCase(id)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "MobSpawn id already in use!");
                return;
            }
        }

        // Validate profile
        final MobSpawnProfile profile = getProfile(event.getLine(2));
        if (profile == null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Unknown MobSpawn profile: " + event.getLine(2));
            return;
        }

        // Validate max mobs
        int spawnMaxMobs = -1;
        if (event.getLine(3) != null && !event.getLine(3).isEmpty()) {
            try {
                spawnMaxMobs = Integer.parseInt(event.getLine(3));
            } catch (NumberFormatException ex) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Invalid number: " + event.getLine(3));
                return;
            }
        }

        final MobSpawn spawn = new MobSpawn(this, id);
        spawn.setProfile(profile);
        spawn.setLocation(event.getBlock().getLocation());
        spawn.setMaxMobs(spawnMaxMobs);

        plugin.msm.addSpawn(spawn);

        // Update sign next tick
        new BukkitRunnable() {
            @Override
            public void run() {
                updateSign(spawn);
            }
        }.runTask(plugin);

        player.sendMessage(ChatColor.GREEN + "Created mobspawn sign: " + id + "!");
    }

    @EventHandler
    public void onSignDelete(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SIGN_POST) {
            return;
        }

        final Sign sign = (Sign) event.getBlock().getState();

        if (!ChatColor.stripColor(sign.getLine(0)).toLowerCase().equals("[mobspawn]")) {
            return;
        }

        final Player player = event.getPlayer();
        if (!player.hasPermission("goldiriath.mobspawn")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to delete MobSpawn signs!");
            event.setCancelled(true);
            return;
        }

        MobSpawn spawn = null;
        for (MobSpawn loopSpawn : spawns) {
            if (!loopSpawn.isValid()) {
                continue;
            }

            if (!loopSpawn.getLocation().equals(event.getBlock().getLocation())) {
                plugin.logger.info(LocationUtils.format(loopSpawn.getLocation()) + " != " + LocationUtils.format(event.getBlock().getLocation()));
                continue;
            }

            spawn = loopSpawn;
            break;
        }

        if (spawn == null) {
            plugin.logger.warning("Could not delete mob spawn at location " + LocationUtils.format(event.getBlock().getLocation()) + ". Could not find assocated spawner!");
            player.sendMessage(ChatColor.RED + "Warning: Could not find associated mob spawn!");
            return;
        }

        removeSpawn(spawn);
        player.sendMessage(ChatColor.GREEN + "Deleted MobSpawn: " + spawn.getId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDispense(BlockDispenseEvent event) {
        ItemStack stack = event.getItem();
        if (stack.getType() != Material.MONSTER_EGG
                && stack.getType() != Material.MONSTER_EGGS) {
            logger.info("Not monster egg!");
            return;
        }

        ItemMeta meta = stack.getItemMeta();
        if (!meta.hasDisplayName()) {
            return;
        }

        String name = meta.getDisplayName();
        if (name == null) {
            return;
        }

        MobSpawnProfile profile = plugin.msm.getProfile(name);
        if (profile == null) {
            return;
        }

        MaterialData data = event.getBlock().getState().getData();
        if (!(data instanceof Dispenser)) {
            return;
        }

        Dispenser dis = (Dispenser) data;
        BlockFace face = dis.getFacing();

        Location location = event.getBlock().getRelative(face, 1).getLocation();

        if (face == BlockFace.DOWN) {
            location = location.add(0, -1, 0);
        }

        location = location.add(0.5, 0.5, 0.5);

        event.setCancelled(true);
        profile.spawn(location);
    }

    // Private methods
    private void updateSign(MobSpawn spawn) {
        if (!spawn.isValid()) {
            return;
        }

        if (!devMode) {
            spawn.getLocation().getBlock().setType(Material.AIR);
            return;
        }

        final Block spawner = spawn.getLocation().getBlock();
        spawner.setType(Material.SIGN_POST);

        if (!Sign.class.isAssignableFrom(spawner.getState().getClass())) {
            plugin.logger.warning("Could set sign for mobspawner! Invalid sign state!");
            return;
        }

        final Sign sign = (Sign) spawner.getState();
        sign.setLine(0, "[" + ChatColor.DARK_PURPLE + "MobSpawn" + ChatColor.RESET + "]");
        sign.setLine(1, spawn.getId().toLowerCase());
        sign.setLine(2, spawn.getProfile().getId().toLowerCase());
        sign.setLine(3, spawn.hasMaxMobs() ? "" + spawn.getMaxMobs() : "");
        sign.update();
    }

    private void loadConfig() {
        spawns.clear();

        // Load vars
        enabled = plugin.config.getBoolean(ConfigPaths.MOBSPAWNER_ENABLED);
        devMode = plugin.config.getBoolean(ConfigPaths.MOBSPAWNER_DEV_MODE);
        maxMobs = plugin.config.getInt(ConfigPaths.MOBSPAWNER_MAX_MOBS);
        timeThreshold = plugin.config.getInt(ConfigPaths.MOBSPAWNER_TIME_THRESHOLD);
        radiusSquaredThreshold = plugin.config.getInt(ConfigPaths.MOBSPAWNER_RADIUS_THRESHOLD);
        radiusSquaredThreshold *= radiusSquaredThreshold;

        // Load Profiles
        profileConfig.load();
        for (String id : profileConfig.getKeys(false)) {
            final ConfigurationSection section = profileConfig.getConfigurationSection(id);
            id = id.toLowerCase();

            final MobSpawnProfile profile = new MobSpawnProfile(this, id);

            try {
                profile.loadFrom(section);
            } catch (ParseException ex) {
                plugin.logger.warning("Could not load mobspawn: '" + id + "'. Exception whilst loading!");
                plugin.logger.severe(ExceptionUtils.getFullStackTrace(ex));
                return;
            }

            if (!profile.isValid()) {
                plugin.logger.warning("Could not load mobspawn: '" + id + "'. Invalid MobSpawn! (Are there missing values?)");
                return;
            }

            profiles.add(profile);
        }

        // Load spawns
        if (spawnConfig.exists()) {
            spawnConfig.load();
        }
        // [id]:
        //   location: [location]
        //   profile: [profilename]
        for (String id : spawnConfig.getKeys(false)) {
            if (!spawnConfig.isConfigurationSection(id)) {
                plugin.logger.warning("Could not load mobspawn: '" + id + "'. Invalid format!");
                continue;
            }

            id = id.toLowerCase();

            final MobSpawn spawn = new MobSpawn(this, id);
            spawn.loadFrom(spawnConfig.getConfigurationSection(id));
            spawns.add(spawn);
        }
        saveConfig();
    }

    private void saveConfig() {
        for (MobSpawn spawn : spawns) {
            spawn.saveTo(spawnConfig.createSection(spawn.getId()));
        }

        spawnConfig.save();
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
        if (devMode) {
            killAll();
        }

        for (MobSpawn spawn : spawns) {
            updateSign(spawn);
        }
    }

    public MobSpawnProfile getProfile(String id) {
        if (id == null) {
            return null;
        }

        id = id.toLowerCase();

        for (MobSpawnProfile profile : profiles) {
            if (profile.getId().equals(id)) {
                return profile;
            }
        }

        return null;
    }
}
