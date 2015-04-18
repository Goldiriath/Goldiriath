package me.dirkjan.goldiriath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.serializable.SerializableBlockLocation;
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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MobSpawner implements Service, Listener {

    private final Goldiriath plugin;
    private final YamlConfig config;
    private BukkitTask spawnTask;
    //
    private final List<MobSpawn> spawns;
    private boolean enabled;
    private boolean devMode;
    private int radiusSquared;
    private int maxMobs;
    private int spawnThreshold;

    public MobSpawner(Goldiriath plugin) {
        this.plugin = plugin;
        this.config = new YamlConfig(plugin, "mobs.yml");
        this.spawns = new ArrayList<>();
        this.devMode = false;
    }

    public List<MobSpawn> getSpawns() {
        return Collections.unmodifiableList(spawns);
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
        }
    }

    @Override
    public void start() {
        loadConfig();

        // Update spawns
        setDevMode(devMode);

        if (!enabled) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

        try {
            spawnTask.cancel();
        } catch (Exception ignored) {
        }

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
    }

    private void clearSign(SignChangeEvent event) {
        event.setLine(0, "");
        event.setLine(1, "");
        event.setLine(2, "");
        event.setLine(3, "");
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        final Player player = event.getPlayer();

        if (!event.getLine(0).equalsIgnoreCase("[mobspawn]")) {
            return;
        }

        // Ensure player has the required permission
        if (!event.getPlayer().hasPermission("goldiriath.mobspawn")) {
            clearSign(event);
            player.sendMessage(ChatColor.RED + "You do not have permission to create MobSpawn signs!");
            return;
        }

        // Validate name
        final String name = event.getLine(1).toLowerCase();
        if (name.length() < 2) {
            clearSign(event);
            player.sendMessage(ChatColor.RED + "MobSpawn name it too short!");
            return;
        }
        for (MobSpawn spawn : spawns) {
            if (spawn.getName().equalsIgnoreCase(name)) {
                clearSign(event);
                player.sendMessage(ChatColor.RED + "MobSpawn name already in use!");
                return;
            }
        }

        // Validate type
        final EntityType type = EntityType.fromName(event.getLine(2));
        if (type == null) {
            clearSign(event);
            player.sendMessage(ChatColor.RED + "Entity type not found!");
            return;
        }

        // Validate level
        int lvl = 0;
        try {
            lvl = Integer.parseInt(event.getLine(3));
        } catch (NumberFormatException ex) {
            clearSign(event);
            player.sendMessage(ChatColor.RED + "Could not parse level!");
            return;
        }

        final MobSpawn spawn = plugin.ms.new MobSpawn(name, type, event.getBlock().getLocation(), lvl);
        plugin.ms.add(spawn);

        // Update sign next tick
        new BukkitRunnable() {
            @Override
            public void run() {
                updateDevModeSign(spawn);
            }
        }.runTask(plugin);

        player.sendMessage(ChatColor.GREEN + "Created mobspawn sign: " + name + "!");
    }

    private void loadConfig() {
        spawns.clear();
        config.load();

        // Load vars
        enabled = plugin.config.getBoolean(ConfigPaths.MOBSPAWNER_ENABLED);
        devMode = plugin.config.getBoolean(ConfigPaths.MOBSPAWNER_DEV_MODE);
        radiusSquared = plugin.config.getInt(ConfigPaths.MOBSPAWNER_RADIUS);
        radiusSquared = radiusSquared * radiusSquared;
        maxMobs = plugin.config.getInt(ConfigPaths.MOBSPAWNER_MAX_MOBS);
        spawnThreshold = plugin.config.getInt(ConfigPaths.MOBSPAWNER_SPAWN_THRESHOLD);

        // Load spawns
        if (config.isConfigurationSection("spawns")) {
            //spawns:
            //  [name]:
            //    location:[location]
            //    type:[type]
            //    lvl:[lvl]

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
                EntityType type = EntityType.fromName(typeString);
                plugin.getLogger().info(typeString);
                if (type == null) {
                    plugin.logger.warning("Could not load mobspawn '" + name + "'. Could not determine type!");
                    continue;
                }

                int lvl = spawnsConfig.getInt(name + ".lvl", 1); // 1 = default lvl

                // Setup and add mobspawn
                final MobSpawn spawn = new MobSpawn(name, type, location, lvl);
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
        config.set("spawns", null);
        for (MobSpawn spawn : spawns) {
            final String name = spawn.getName().toLowerCase();
            config.set("spawns." + name + ".location", new SerializableBlockLocation(spawn.getLocation()).serialize());
            config.set("spawns." + name + ".type", spawn.getEntityType().toString());
            config.set("spawns." + name + ".lvl", spawn.getLvl());
        }

        config.save();
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
        for (MobSpawn spawn : spawns) {
            updateDevModeSign(spawn);
        }
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
        sign.setLine(3, spawn.getLvl() + "");
        sign.update();
    }

    public boolean isDevMode() {
        return devMode;
    }

    // Instance per mob spawn
    public class MobSpawn {

        private final String name;
        private final EntityType type;
        private final Location location;
        private final int lvl;
        private long lastSpawn;

        public MobSpawn(String name, EntityType type, Location location, int lvl) {
            this.name = name;
            this.type = type;
            this.location = location;
            this.lvl = lvl;
            this.lastSpawn = 0;
        }

        public EntityType getEntityType() {
            return type;
        }

        public Location getLocation() {
            return location;
        }

        public int getLvl() {
            return lvl;
        }

        public String getName() {
            return name;
        }

        public long getLastSpawn() {
            return lastSpawn;
        }

        public boolean isValid() {
            return type != null && location != null && lvl > 0 && name != null;
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

            // TODO: Properly implement
            /*
             le.setCustomName(name);
             if (le.getCustomName().equals("zombie(lvl5)")) {
             if (lvl == 5) {
             le.setCanPickupItems(false);
             le.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
             le.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
             le.getEquipment().setLeggings(null);
             le.getEquipment().setHelmet(null);
             le.getEquipment().setItemInHand(Items.WOODEN_SWORD.getItem());
             le.getEquipment().setBootsDropChance(0);
             le.getEquipment().setChestplateDropChance(0);
             le.getEquipment().setHelmetDropChance(0);
             le.getEquipment().setItemInHandDropChance(1);
             le.getEquipment().setLeggingsDropChance(0);

             }
             }*/
            return le;
        }
    }
}
