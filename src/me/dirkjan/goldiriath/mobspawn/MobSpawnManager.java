package me.dirkjan.goldiriath.mobspawn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import me.dirkjan.goldiriath.ConfigPaths;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.util.Service;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MobSpawnManager implements Service, Listener {

    private final Goldiriath plugin;
    private final YamlConfig profileConfig;
    private final YamlConfig spawnConfig;
    private BukkitTask spawnTask;
    //
    private final Set<MobSpawnProfile> profiles;
    private final Set<MobSpawn> spawns;
    private boolean enabled;
    private boolean devMode;
    private int radiusSquared;
    private int maxMobs;
    private int spawnThreshold;
    private int playerRadiusThresholdSquared;

    public MobSpawnManager(Goldiriath plugin) {
        this.plugin = plugin;
        this.profileConfig = new YamlConfig(plugin, "profiles.yml", true);
        this.spawnConfig = new YamlConfig(plugin, "mobspawns.yml", false);
        this.spawns = new HashSet<>();
        this.profiles = new HashSet<>();
        this.devMode = false;
    }

    // Public/protected methods
    public Set<MobSpawn> getSpawns() {
        return Collections.unmodifiableSet(spawns);
    }

    public int killAll() {
        int killed = 0;

        for (MobSpawn spawn : spawns) {
            killed += spawn.kill();
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
    public void start() {
        if (spawnTask != null) {
            stop();
        }

        loadConfig();

        // Update sign visibility
        setDevMode(devMode);

        if (!enabled) {
            return;
        }

        // Register events
        Bukkit.getPluginManager().registerEvents(this, plugin);

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
        }.runTaskTimer(Goldiriath.plugin, 2, 2); // Run every other tick
    }

    @Override
    public void stop() {
        saveConfig();

        try {
            spawnTask.cancel();
        } catch (Exception ignored) {
        }

        // Kill spawned entities
        killAll();

        // Unregister events
        HandlerList.unregisterAll(this);
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
        radiusSquared = plugin.config.getInt(ConfigPaths.MOBSPAWNER_RADIUS);
        radiusSquared *= radiusSquared;
        maxMobs = plugin.config.getInt(ConfigPaths.MOBSPAWNER_MAX_MOBS);
        spawnThreshold = plugin.config.getInt(ConfigPaths.MOBSPAWNER_SPAWN_THRESHOLD);
        playerRadiusThresholdSquared = plugin.config.getInt(ConfigPaths.MOBSPAWNER_PLAYER_RADIUS_THRESHOLD);
        playerRadiusThresholdSquared *= playerRadiusThresholdSquared;

        // Load Profiles
        profileConfig.load();
        for (String id : profileConfig.getKeys(false)) {
            final ConfigurationSection section = profileConfig.getConfigurationSection(id);
            id = id.toLowerCase();

            final MobSpawnProfile profile = new MobSpawnProfile(this, id);
            profile.loadFrom(section);

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

    // Getters, Setters
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
        for (MobSpawn spawn : spawns) {
            updateSign(spawn);
        }
    }

    public Goldiriath getPlugin() {
        return plugin;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public int getRadiusSquared() {
        return radiusSquared;
    }

    public int getMaxMobs() {
        return maxMobs;
    }

    public int getSpawnThreshold() {
        return spawnThreshold;
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
