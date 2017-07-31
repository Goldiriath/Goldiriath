package net.goldiriath.plugin.game.loot;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.ConfigSavable;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.Validatable;
import net.goldiriath.plugin.util.logging.GLogger;
import net.pravian.aero.serializable.SerializableBlockLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ChestSpawn implements ConfigLoadable, ConfigSavable, Validatable {

    private final LootManager lm;
    private final GLogger logger;
    @Getter
    private final String id;
    //
    @Getter
    @Setter
    private Location location;
    @Getter
    @Setter
    private LootProfile profile;
    @Getter
    @Setter
    private int tickDelay = 20;
    private long lastSpawn = 0;

    public ChestSpawn(LootManager lm, String id) {
        this.lm = lm;
        this.id = id;
        this.logger = lm.getPlugin().logger;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        final String locationString = config.getString("location", null);
        if (locationString == null) {
            logger.warning("Could not load chestspawn '" + id + "'. Location not defined!");
            return;
        }

        location = new SerializableBlockLocation(locationString).deserialize();
        if (location == null) {
            logger.warning("Could not load chestspawn '" + id + "'. Could not deserialize location!");
            return;
        }

        final String profileString = config.getString("profile".toLowerCase(), null);
        if (profileString == null) {
            logger.warning("Could not load chestspawn '" + id + "'. Profile not defined!");
            return;
        }

        Map<String, LootProfile> profileMap = lm.getProfileMap();
        if (profileMap == null) {
            logger.warning("ProfileMap null");
        }
        profile = profileMap.get(profileString);
        if (profile == null) {
            logger.warning("Ignoring profile '" + profileString + "' for chestspawn '" + id + "'. Profile could not be determined!");
        }

        final String delayString = config.getString("delay", "20");
        try {
            tickDelay = Integer.valueOf(delayString);
        } catch (Exception e) {
            logger.warning("Cant load delay, setting to default 20");
        }
    }

    @Override
    public void saveTo(ConfigurationSection config) {
        if (!isValid()) {
            logger.warning("Not saving chestspawn: '" + id + "' chestspawn is invalid");
            return;
        }

        config.set("location", new SerializableBlockLocation(location).serialize());
        config.set("profile", profile.getId());
        config.set("delay", tickDelay);
    }

    protected boolean tick() { // True if the tick resulted in a mob spawn
        if (!canSpawn()) {
            return false;
        }

        return spawn();
    }

    public boolean canSpawn() {
        if (!isValid()) {
            return false;
        }

        if (Util.getServerTick() - lastSpawn < tickDelay) {
            return false;
        }
        if (isSpawned()) {
            return false;
        }

        return true;
    }

    public boolean spawn() {
        if (!isValid()) {
            return false;
        }

        // Spawn
        location.getBlock().setType(Material.CHEST);

        // Set inventory
        Chest chest = (Chest) location.getBlock().getState();
        chest.getBlockInventory().clear();
        chest.getBlockInventory().setContents(profile.drop(ChestTier.NORMAL).toArray(new ItemStack[0]));

        return true;
    }

    public boolean isSpawned() {
        return location.getBlock().getType() == Material.CHEST;
    }

    public void despawn() {
        lastSpawn = Util.getServerTick();
        BlockState state = location.getBlock().getState();
        if (state instanceof Chest) {
            Chest chest = (Chest) state;
            chest.getBlockInventory().clear();
        }

        location.getBlock().setType(Material.AIR);
    }

    @Override
    public boolean isValid() {
        return id != null
                && location != null
                && profile != null;
    }

}
