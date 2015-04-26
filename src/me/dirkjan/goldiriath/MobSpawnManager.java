package me.dirkjan.goldiriath;

import me.dirkjan.goldiriath.util.Service;
import me.dirkjan.goldiriath.util.Util;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.serializable.SerializableBlockLocation;
import net.pravian.bukkitlib.util.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MobSpawnManager implements Service, Listener {

    private final Goldiriath plugin;
    private final YamlConfig config;
    private BukkitTask spawnTask;
    //
    private final Set<MobSpawn> spawns;
    private final Set<Profile> profiles;
    private boolean enabled;
    private boolean devMode;
    private int radiusSquared;
    private int maxMobs;
    private int spawnThreshold;
    private int playerRadiusThresholdSquared;

    public MobSpawnManager(Goldiriath plugin) {
        this.plugin = plugin;
        this.config = new YamlConfig(plugin, "mobs.yml");
        this.spawns = new HashSet<>();
        this.profiles = new HashSet<>();
        this.devMode = false;
    }

    public Set<MobSpawn> getSpawns() {
        return Collections.unmodifiableSet(spawns);
    }

    public void add(MobSpawn spawn) {
        if (!spawn.isValid()) {
            plugin.logger.warning("Could not add mobspawn! Mobspawn not valid");
            return;
        }

        spawns.add(spawn);

        saveConfig();
    }

    public void remove(MobSpawn spawn) {
        if (!spawns.remove(spawn)) {
            plugin.logger.warning("Could not remove mobspawn! Mobspawn not present!");
            return;
        }

        saveConfig();
    }

    @Override
    public void start() {
        if (spawnTask != null) {
            stop();
        }

        loadConfig();

        // Update spawns
        setDevMode(devMode);

        if (!enabled) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

        spawnTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (devMode) {
                    return;
                }

                spawnLoop:
                for (MobSpawn spawn : spawns) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getLocation().distanceSquared(spawn.getLocation()) > playerRadiusThresholdSquared) {
                            continue;
                        }

                        spawn.tick();
                        break spawnLoop;
                    }
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

        // Unregister events
        SignChangeEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
    }

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
        final String name = event.getLine(1).toLowerCase();
        if (name.length() < 2) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "MobSpawn name it too short!");
            return;
        }
        for (MobSpawn spawn : spawns) {
            if (spawn.getName().equalsIgnoreCase(name)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "MobSpawn name already in use!");
                return;
            }
        }

        // Validate type
        final EntityType type = EntityType.fromName(event.getLine(2));
        if (type == null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Entity type not found!");
            return;
        }

        // Validate level
        Profile profile = null;
        if (!event.getLine(3).isEmpty()) {
            profile = determineProfile(event.getLine(3));

            if (profile == null) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Invalid MobSpawn profile: " + event.getLine(3));
                return;
            }
        }

        final MobSpawn spawn = plugin.msm.new MobSpawn(name, type, event.getBlock().getLocation(), profile);
        plugin.msm.add(spawn);

        // Update sign next tick
        new BukkitRunnable() {
            @Override
            public void run() {
                updateDevModeSign(spawn);
            }
        }.runTask(plugin);

        player.sendMessage(ChatColor.GREEN + "Created mobspawn sign: " + name + "!");
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

        remove(spawn);
        player.sendMessage(ChatColor.GREEN + "Deleted MobSpawn: " + spawn.getName());
    }

    private void loadConfig() {
        spawns.clear();
        config.load();

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
        final ConfigurationSection profileSection = plugin.config.getConfigurationSection(ConfigPaths.MOBSPAWNER_PROFILES.getPath());
        for (String profileName : profileSection.getKeys(false)) {
            final ConfigurationSection currentProfile = profileSection.getConfigurationSection(profileName);
            profileName = profileName.toLowerCase().trim();

            String customName = currentProfile.getString("name", null);
            final ItemStack carryItem = Util.parseItem(currentProfile.getString("item", null));
            final ItemStack helmet = Util.parseItem(currentProfile.getString("helmet", null));
            final ItemStack chestplate = Util.parseItem(currentProfile.getString("chestplate", null));
            final ItemStack leggings = Util.parseItem(currentProfile.getString("leggings", null));
            final ItemStack boots = Util.parseItem(currentProfile.getString("boots", null));

            if (customName != null) {
                customName = ChatColor.translateAlternateColorCodes('&', customName);
            }

            profiles.add(new Profile(profileName, customName, carryItem, helmet, chestplate, leggings, boots));
        }

        // Load spawns
        if (config.isConfigurationSection("spawns")) {
            //spawns:
            //  [name]:
            //    location: [location]
            //    type: [type]
            //    (profile: [profilename])

            ConfigurationSection spawnsConfig = config.getConfigurationSection("spawns");

            for (String name : spawnsConfig.getKeys(false)) {

                final String locationString = spawnsConfig.getString(name + ".location");
                if (locationString == null) {
                    plugin.logger.warning("Could not load mobspawn '" + name + "'. Location not defined!");
                    continue;
                }
                final Location location = new SerializableBlockLocation(locationString).deserialize();
                if (location == null) {
                    plugin.logger.warning("Could not load mobspawn '" + name + "'. Could not deserialize location!");
                    continue;
                }

                final String typeString = spawnsConfig.getString(name + ".type");
                if (typeString == null) {
                    plugin.logger.warning("Could not load mobspawn '" + name + "'. Type not defined!");
                    continue;
                }

                // TODO fix deprecation
                final EntityType type = EntityType.fromName(typeString);
                if (type == null) {
                    plugin.logger.warning("Could not load mobspawn '" + name + "'. Could not determine type!");
                    continue;
                }

                final String profileString = spawnsConfig.getString(name + ".profile", null);
                final Profile profile = determineProfile(profileString);
                if (profileString != null && profile == null) {
                    plugin.logger.warning("Ignoring profile '" + profileString + "' for mobspawn '" + name + "'. Profile could not be determined!");
                }

                // Setup and add mobspawn
                final MobSpawn spawn = new MobSpawn(name, type, location, profile);
                spawns.add(spawn);
            }

            // Remove invalid mobspawns
            final Iterator<MobSpawn> it = spawns.iterator();
            while (it.hasNext()) {
                final MobSpawn spawn = it.next();

                if (spawn.isValid()) {
                    continue;
                }

                plugin.logger.warning("Discarding mobspawn '" + spawn.getName() + "'. Mobspawn is not valid!");

                if (spawn.getLocation() != null) {
                    spawn.getLocation().getBlock().setType(Material.AIR);
                }

                it.remove();
            }
            saveConfig();
        }
    }

    private void saveConfig() {

        // Save spawns
        config.clear();

        for (MobSpawn spawn : spawns) {
            if (!spawn.isValid()) {
                plugin.logger.info("Not saving mobspawn: " + spawn.getName() + ". Mobspawn is invalid!");
                continue;
            }

            final String name = spawn.getName().toLowerCase();
            config.set("spawns." + name + ".location", new SerializableBlockLocation(spawn.getLocation()).serialize());
            config.set("spawns." + name + ".type", spawn.getEntityType().toString());
            if (spawn.getProfile() != null) {
                config.set("spawns." + name + ".profile", spawn.getProfile().getName().toLowerCase());
            }
        }

        config.save();
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
        for (MobSpawn spawn : spawns) {
            updateDevModeSign(spawn);
        }
    }

    public boolean isDevMode() {
        return devMode;
    }

    private void updateDevModeSign(MobSpawn spawn) {
        if (!devMode) {
            spawn.getLocation().getBlock().setType(Material.AIR);
            return;
        }

        final Block spawner = spawn.getLocation().getBlock();
        spawner.setType(Material.SIGN_POST);

        if (!Sign.class.isAssignableFrom(spawner.getState().getClass())) {
            plugin.logger.warning("Could not set dev mode for mob spawner! Invalid sign state!");
            return;
        }

        final Sign sign = (Sign) spawner.getState();
        sign.setLine(0, "[" + ChatColor.DARK_PURPLE + "MobSpawn" + ChatColor.RESET + "]");
        sign.setLine(1, spawn.getName());
        sign.setLine(2, spawn.getEntityType().toString());
        sign.setLine(3, (spawn.getProfile() == null ? "" : spawn.getProfile().getName()));
        sign.update();
    }

    private Profile determineProfile(String profileName) {
        if (profileName == null) {
            return null;
        }

        for (Profile loopProfile : profiles) {
            if (loopProfile.getName().equalsIgnoreCase(profileName)) {
                return loopProfile;
            }
        }

        return null;
    }

    public class Profile {

        private final String name;
        private final String customName;
        private final ItemStack carryItem;
        private final ItemStack helmet;
        private final ItemStack chestplate;
        private final ItemStack leggings;
        private final ItemStack boots;

        public Profile(String name, String customName, ItemStack carryItem, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
            this.name = name;
            this.customName = customName;
            this.carryItem = carryItem;
            this.helmet = helmet;
            this.chestplate = chestplate;
            this.leggings = leggings;
            this.boots = boots;
        }

        public String getName() {
            return name;
        }

        public String getCustomName() {
            return customName;
        }

        public ItemStack getCarryItem() {
            return carryItem;
        }

        public ItemStack getHelmet() {
            return helmet;
        }

        public ItemStack getChestplate() {
            return chestplate;
        }

        public ItemStack getLeggings() {
            return leggings;
        }

        public ItemStack getBoots() {
            return boots;
        }

        public void setup(LivingEntity entity) {
            final EntityEquipment equipment = entity.getEquipment();

            entity.setCanPickupItems(false);
            equipment.setItemInHandDropChance(0);
            equipment.setHelmetDropChance(0);
            equipment.setChestplateDropChance(0);
            equipment.setLeggingsDropChance(0);
            equipment.setBootsDropChance(0);

            if (customName != null) {
                entity.setCustomName(customName);
            }

            if (carryItem != null) {
                equipment.setItemInHand(carryItem);
            }

            if (helmet != null) {
                equipment.setHelmet(helmet);
            }

            if (chestplate != null) {
                equipment.setChestplate(chestplate);
            }

            if (leggings != null) {
                equipment.setLeggings(leggings);
            }

            if (boots != null) {
                equipment.setBoots(boots);
            }
        }

    }

    // Instance per mob spawn
    public class MobSpawn {

        private final String name;
        private final EntityType type;
        private final Location location;
        private final @Nullable
        Profile profile;
        private long lastSpawn;

        public MobSpawn(String name, EntityType type, Location location, Profile profile) {
            this.name = name;
            this.type = type;
            this.location = location;
            this.profile = profile;
            this.lastSpawn = 0;
        }

        public EntityType getEntityType() {
            return type;
        }

        public Location getLocation() {
            return location;
        }

        public Profile getProfile() {
            return profile;
        }

        public String getName() {
            return name;
        }

        public long getLastSpawn() {
            return lastSpawn;
        }

        public boolean isValid() {
            return type != null && location != null && name != null;
        }

        protected boolean tick() { // True if the tick resulted in a mob spawn
            if (!isValid()) {
                return false;
            }

            int closemobs = 0;
            for (Entity entity : location.getWorld().getLivingEntities()) {
                if (entity instanceof LivingEntity && entity.getLocation().distanceSquared(location) < radiusSquared) {
                    closemobs++;
                }
            }

            if (closemobs > maxMobs) {
                return false;
            }

            if (getCurrentTicks() - lastSpawn < spawnThreshold) {
                return false;
            }

            spawn();
            return true;
        }

        private long getCurrentTicks() {
            return location.getWorld().getFullTime();
        }

        public LivingEntity spawn() {
            if (!isValid()) {
                return null;
            }

            lastSpawn = getCurrentTicks();

            final LivingEntity le = (LivingEntity) location.getWorld().spawnEntity(location, type);

            if (profile != null) {
                profile.setup(le);
            }

            return le;
        }
    }

}
