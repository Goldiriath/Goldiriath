package net.goldiriath.plugin.game.loot;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.DevMode;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.mobspawn.MobSpawnProfile;
import net.goldiriath.plugin.game.mobspawn.citizens.HostileMobTrait;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Locations;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LootManager extends AbstractService {

    private final YamlConfig config;
    private final YamlConfig spawnConfig;
    private BukkitTask spawnTask;
    //
    private final Set<ChestSpawn> spawns;
    @Getter
    private final Map<String, Group> groupMap = Maps.newHashMap();
    @Getter
    private final Map<String, LootProfile> profileMap = Maps.newHashMap();

    public LootManager(Goldiriath plugin) {
        super(plugin);
        this.config = new YamlConfig(plugin, "loot.yml", true);
        this.spawns = new HashSet<>();
        this.spawnConfig = new YamlConfig(plugin, "data/chestspawns.yml", false);
    }

    @Override
    protected void onStart() {
        config.load();

        ConfigurationSection groupSection = config.getConfigurationSection("groups");
        groupMap.clear();
        if (groupSection != null) {
            for (String id : groupSection.getKeys(false)) {

                Group group = new Group(id.toLowerCase());
                group.loadFrom(groupSection);
                groupMap.put(id.toLowerCase(), group);

            }
        }
        ConfigurationSection profileSection = config.getConfigurationSection("profiles");
        profileMap.clear();
        if (profileSection != null) {
            for (String id : profileSection.getKeys(false)) {

                LootProfile profile = new LootProfile(plugin, id.toLowerCase());
                profile.loadFrom(profileSection.getConfigurationSection(id));
                profileMap.put(id.toLowerCase(), profile);

            }
        }
        loadConfig();
        //start spawning chests
        spawnTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (plugin.dev.isDevMode()) {
                    return;
                }

                for (ChestSpawn spawn : spawns) {
                    spawn.tick();
                }
            }
        }.runTaskTimer(plugin, 2, 2);

    }

    @Override
    protected void onStop() {
        groupMap.clear();
        profileMap.clear();
        saveConfig();
        Util.cancel(spawnTask);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerKillMob(EntityDeathEvent event) {
        LivingEntity mob = event.getEntity();

        NPC npc = plugin.msm.getBridge().getNPC(mob);
        if (npc == null) {
            return;
        }

        HostileMobTrait trait = npc.getTrait(HostileMobTrait.class);
        if (trait == null) {
            logger.warning("An NPC died that did not have a mobspawn profile!");
            return;
        }

        MobSpawnProfile profile = trait.getProfile();
        LootProfile loot = profile.getLootProfile();
        if (loot == null) {
            return;
        }

        // TODO: Beta - roll loot according to https://github.com/Goldiriath/Goldiriath/issues/71
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            int damage = trait.getInflictedDamage(player);
            if (damage < 1) {
                continue;
            }

            List<ItemStack> stacks = loot.drop(profile.getLootTier());
            for (ItemStack stack : stacks) {
                InventoryUtil.storeItem(player.getInventory(), stack, false);
            }
        }
    }

    // Chestspawns
    public void addSpawn(ChestSpawn spawn) {
        if (!spawn.isValid()) {
            plugin.logger.warning("Could not add chestSpawn: " + spawn.getId() + ". chestSpawn not valid");
            return;
        }

        spawns.add(spawn);
        saveConfig();
    }

    public void removeSpawn(ChestSpawn spawn) {
        if (!spawns.remove(spawn)) {
            plugin.logger.warning("Could not remove chestspawn: " + spawn.getId() + ". chestspawn not present!");
            return;
        }

        if (spawn.getId() != null) {
            spawnConfig.set(spawn.getId(), null);
        }
        saveConfig();
    }

    // Listeners
    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        final Player player = event.getPlayer();

        if (!event.getLine(0).equalsIgnoreCase("[chest]")) {
            return;
        }

        // Ensure player has the required permission
        if (!event.getPlayer().hasPermission("goldiriath.chestspawn")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not have permission to create MobSpawn signs!");
            return;
        }

        // Validate name
        final String id = event.getLine(1).toLowerCase();
        if (id.length() < 2) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "ChestSpawn id is too short!");
            return;
        }
        for (ChestSpawn spawn : spawns) {
            if (spawn.getId().equalsIgnoreCase(id)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "ChestSpawn id already in use!");
                return;
            }
        }

        // Validate loot
        final LootProfile profile = getProfile(event.getLine(2));
        if (profile == null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Unknown Loot profile: " + event.getLine(2));
            return;
        }

        // Validate delay
        int updatedelay = -1;
        if (event.getLine(3) != null && !event.getLine(3).isEmpty()) {
            try {
                updatedelay = Integer.parseInt(event.getLine(3));
            } catch (NumberFormatException ex) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Invalid number: " + event.getLine(3));
                return;
            }
        }

        final ChestSpawn spawn = new ChestSpawn(this, id);
        spawn.setProfile(profile);
        spawn.setLocation(event.getBlock().getLocation());
        spawn.setTickDelay(updatedelay);

        addSpawn(spawn);

        // Update sign next tick
        new BukkitRunnable() {
            @Override
            public void run() {
                updateSign(spawn);
            }
        }.runTask(plugin);

        player.sendMessage(ChatColor.GREEN + "Created chestspawn sign: " + id + "!");
    }

    @EventHandler
    public void onSignDelete(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SIGN_POST) {
            return;
        }

        final Sign sign = (Sign) event.getBlock().getState();

        if (!ChatColor.stripColor(sign.getLine(0)).toLowerCase().equals("[chest]")) {
            return;
        }

        final Player player = event.getPlayer();
        if (!player.hasPermission("goldiriath.chestspawn")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to delete ChestSpawn signs!");
            event.setCancelled(true);
            return;
        }

        ChestSpawn spawn = null;
        for (ChestSpawn loopSpawn : spawns) {
            if (!loopSpawn.isValid()) {
                continue;
            }

            if (!loopSpawn.getLocation().equals(event.getBlock().getLocation())) {
                continue;
            }

            spawn = loopSpawn;
            break;
        }

        if (spawn == null) {
            plugin.logger.warning("Could not delete ChestSpawn at location " + Locations.format(event.getBlock().getLocation()) + ". Could not find assocated spawner!");
            player.sendMessage(ChatColor.RED + "Warning: Could not find associated Chestspawn!");
            return;
        }

        removeSpawn(spawn);
        player.sendMessage(ChatColor.GREEN + "Deleted Chestspawn: " + spawn.getId());
    }

    // Private methods
    private void updateSign(ChestSpawn spawn) {
        if (!spawn.isValid()) {
            return;
        }

        if (!plugin.dev.isDevMode()) {
            spawn.getLocation().getBlock().setType(Material.AIR);
            return;
        }

        final Block spawner = spawn.getLocation().getBlock();
        spawner.setType(Material.SIGN_POST);

        if (!Sign.class.isAssignableFrom(spawner.getState().getClass())) {
            plugin.logger.warning("Could set sign for chestspawn! Invalid sign state!");
            return;
        }

        final Sign sign = (Sign) spawner.getState();
        sign.setLine(0, "[" + ChatColor.DARK_PURPLE + "Chest" + ChatColor.RESET + "]");
        sign.setLine(1, spawn.getId().toLowerCase());
        sign.setLine(2, spawn.getProfile().getId().toLowerCase());
        sign.setLine(3, String.valueOf(spawn.getTickDelay()));
        sign.update();
    }

    private void loadConfig() {
        spawns.clear();

        // Load spawns
        if (spawnConfig.exists()) {
            spawnConfig.load();
        }
        // [id]:
        //   location: [location]
        //   loot: [profilename]
        //   delay: [delay]
        for (String id : spawnConfig.getKeys(false)) {
            if (!spawnConfig.isConfigurationSection(id)) {
                plugin.logger.warning("Could not load chestspawn: '" + id + "'. Invalid format!");
                continue;
            }

            id = id.toLowerCase();

            final ChestSpawn spawn = new ChestSpawn(this, id);
            spawn.loadFrom(spawnConfig.getConfigurationSection(id));
            spawns.add(spawn);
        }
        saveConfig();
    }

    private void saveConfig() {
        for (ChestSpawn spawn : spawns) {
            spawn.saveTo(spawnConfig.createSection(spawn.getId()));
        }

        spawnConfig.save();
    }

    // Update sign visibility
    @EventHandler
    public void onDevModeChange(DevMode.DevModeChangeEvent event) {
        for (ChestSpawn spawn : spawns) {
            updateSign(spawn);
        }
    }

    public LootProfile getProfile(String id) {
        if (id == null) {
            return null;
        }

        id = id.toLowerCase();

        for (LootProfile profile : profileMap.values()) {
            if (profile.getId().equals(id)) {
                return profile;
            }
        }

        return null;
    }

    @EventHandler
    public void onChestclose(InventoryCloseEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.CHEST)) {
            return;
        }

        if (!(event.getInventory().getHolder() instanceof Chest)) {
            return;
        }

        Chest chest = (Chest) event.getInventory().getHolder();
        for (ChestSpawn spawn : spawns) {
            if (spawn.getLocation().equals(chest.getLocation())) {
                spawn.despawn();
                break;
            }
        }

    }

}
