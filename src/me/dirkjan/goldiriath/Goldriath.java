package me.dirkjan.goldiriath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.commands.Command_resetquest;
import me.dirkjan.goldiriath.skills.SkillManager;
import net.pravian.bukkitlib.command.BukkitCommandHandler;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.serializable.SerializableBlockLocation;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
//work with players id

public class Goldriath extends JavaPlugin {

    public static Goldriath plugin;
    public Logger logger;
    public YamlConfig questConfig;
    public YamlConfig spawnConfig;
    public YamlConfig mobgearConfig;
    public Map<UUID, Stage> questmap;
    public List<MobSpawn> mobSpawns;
    public BukkitCommandHandler handler;
    SkillManager sm = new SkillManager(plugin);
    
    @Override
    public void onLoad() {
        plugin = this;
        logger = plugin.getLogger();
        questConfig = new YamlConfig(plugin, "quests.yml", false);
        spawnConfig = new YamlConfig(plugin, "spawn.yml", false);
        questmap = new HashMap<>();
        mobSpawns = new ArrayList<>();
        handler = new BukkitCommandHandler(plugin);       
    }

    @Override
    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
        questLoad();
        spawnLoad();
        plugin.getLogger().info(mobSpawns.size() + " mobspawns loaded");

        handler.setCommandLocation(Command_resetquest.class.getPackage());
        for (MobSpawn mobspawn : mobSpawns) {
            mobspawn.startspawning();
        }
    }

    @Override
    public void onDisable() {      
        questSave();
        spawnSave();
        sm.save();
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return handler.handleCommand(sender, cmd, commandLabel, args);
    }

    private void questLoad() {
        questmap.clear();
        questConfig.load();
        //quests:
        // quest1:
        //  [uuidhere]: stage-a
        //  [uuidhere2]: stage-b
        if (!questConfig.isConfigurationSection("quests.quest1")) {
            return;
        }
        ConfigurationSection quests = questConfig.getConfigurationSection("quests.quest1");
        for (String UUIDString : quests.getKeys(false)) {

            UUID uuid;
            try {
                uuid = UUID.fromString(UUIDString);
            } catch (IllegalArgumentException exception) {
                continue;
            }

            Stage stage = Stage.fromString(quests.getString(UUIDString));
            if (stage == null) {
                continue;
            }

            questmap.put(uuid, stage);

        }
    }

    private void questSave() {
        questConfig.clear();
        ConfigurationSection quests = questConfig.createSection("quests.quest1");
        for (UUID key : questmap.keySet()) {

            Stage stage = questmap.get(key);
            quests.set(key.toString(), stage.toString());

        }
        questConfig.save();
    }

    private void spawnLoad() {
        spawnConfig.load();
        mobSpawns.clear();
        //spawns:
        //  [name]:
        //    location:[location]
        //    type:[type]
        //    lvl:[lvl]
        if (!spawnConfig.isConfigurationSection("spawns")) {
            return;
        }

        ConfigurationSection spawns = spawnConfig.getConfigurationSection("spawns");
        for (String name : spawns.getKeys(false)) {
            String locationString = spawns.getString(name + ".location");
            if (locationString == null) {
                plugin.getLogger().severe("location error");
                continue;
            }

            SerializableBlockLocation block = new SerializableBlockLocation(locationString);
            Location spawnLocation = block.deserialize();
            if (spawnLocation == null) {
                continue;
            }

            String typeString = spawns.getString(name + ".type");
            if (typeString == null) {
                plugin.getLogger().severe("type error");
                continue;
            }
            EntityType type = EntityType.fromName(typeString);
            plugin.getLogger().info(typeString);
            if (type == null) {
                plugin.getLogger().severe("name error");
                continue;
            }

            int lvl = spawns.getInt(name + ".lvl", 1);

            MobSpawn spawn = new MobSpawn();
            spawn.setLocation(spawnLocation);
            spawn.setEntityType(type);
            spawn.setLvl(lvl);
            spawn.setName(name);

            mobSpawns.add(spawn);
        }

    }

    private void spawnSave() {
        spawnConfig.clear();

        for (MobSpawn spawn : mobSpawns) {
            String stringName = spawn.getName();
            SerializableBlockLocation location = new SerializableBlockLocation(spawn.getLocation());
            String stringLocation = location.serialize();
            String stringType = spawn.getEntityType().toString();
            int lvl = spawn.getLvl();
            spawnConfig.set("spawns." + stringName + ".location", stringLocation);
            spawnConfig.set("spawns." + stringName + ".type", stringType);
            spawnConfig.set("spawns." + stringName + ".lvl", lvl);

        }
        spawnConfig.save();
    }  
}
