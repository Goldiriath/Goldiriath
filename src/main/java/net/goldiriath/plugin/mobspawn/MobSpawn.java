package net.goldiriath.plugin.mobspawn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.ConfigSavable;
import net.goldiriath.plugin.util.Validatable;
import net.pravian.bukkitlib.serializable.SerializableBlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MobSpawn implements ConfigLoadable, ConfigSavable, Validatable {

    public static final String METADATA_ID = "mobspawn";
    //
    private final MobSpawnManager msm;
    private final Logger logger;
    private final String id;
    private final Set<LivingEntity> spawns;
    //
    private MobSpawnProfile profile;
    private Location location;
    private long lastSpawn;
    private int maxMobs;

    public MobSpawn(MobSpawnManager msm, String id) {
        this.msm = msm;
        this.logger = msm.getPlugin().getLogger();
        this.id = id;
        this.lastSpawn = 0;
        this.spawns = new HashSet<>();
    }

    public Location getLocation() {
        return location;
    }

    public void setProfile(MobSpawnProfile profile) {
        this.profile = profile;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public MobSpawnProfile getProfile() {
        return profile;
    }

    public String getId() {
        return id;
    }

    public long getLastSpawn() {
        return lastSpawn;
    }

    public boolean hasMaxMobs() {
        return maxMobs >= 1;
    }

    public Set<LivingEntity> getSpawns() {
        return Collections.unmodifiableSet(spawns);
    }

    public int kill() {
        int killed = 0;

        for (LivingEntity entity : spawns) {
            entity.remove();
            killed++;
        }
        spawns.clear();

        return killed;
    }

    public int getMaxMobs() {
        return maxMobs;
    }

    public void setMaxMobs(int maxMobs) {
        this.maxMobs = maxMobs;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        kill();

        final String locationString = config.getString("location", null);
        if (locationString == null) {
            logger.warning("Could not load mobspawn '" + id + "'. Location not defined!");
            return;
        }

        location = new SerializableBlockLocation(locationString).deserialize();
        if (location == null) {
            logger.warning("Could not load mobspawn '" + id + "'. Could not deserialize location!");
            return;
        }

        final String profileString = config.getString("profile", null);
        if (profileString == null) {
            logger.warning("Could not load mobspawn '" + id + "'. Profile not defined!");
            return;
        }

        profile = msm.getProfile(profileString);
        if (profile == null) {
            logger.warning("Ignoring profile '" + profileString + "' for MobSpawn '" + id + "'. Profile could not be determined!");
        }

        maxMobs = config.getInt("max_mobs", -1);
    }

    @Override
    public void saveTo(ConfigurationSection config) {
        if (!isValid()) {
            logger.warning("Not saving mobspawn: '" + id + "' mobspawn is invalid");
            return;
        }

        config.set("location", new SerializableBlockLocation(location).serialize());
        config.set("profile", profile.getId());
        config.set("max_mobs", maxMobs);
    }

    protected boolean tick() { // True if the tick resulted in a mob spawn
        if (!canSpawn()) {
            return false;
        }

        return spawn() != null;
    }

    public boolean canSpawn() {
        if (!isValid()) {
            return false;
        }

        // Check spawn timeout
        if (getCurrentTicks() - lastSpawn < (profile.hasSpawnThreshold() ? profile.getSpawnThreshold() : msm.getSpawnThreshold())) {
            return false;
        }

        // Remove old spawns
        final Iterator<LivingEntity> it = spawns.iterator();
        while (it.hasNext()) {
            if (it.next().isDead()) {
                it.remove();
            }
        }

        // Check max mobs
        if (spawns.size() >= (hasMaxMobs() ? maxMobs : msm.getMaxMobs())) {
            return false;
        }

        // Check player close
        boolean playerClose = false;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getLocation().distanceSquared(location) > msm.getRadiusSquared()) {
                continue;
            }

            playerClose = true;
            break;
        }

        return playerClose;
    }

    public LivingEntity spawn() {
        if (!isValid()) {
            return null;
        }

        lastSpawn = getCurrentTicks();

        // Spawn
        final LivingEntity entity = profile.spawn(location);

        // Set metadata
        entity.setMetadata(METADATA_ID, new FixedMetadataValue(msm.getPlugin(), this));

        spawns.add(entity);
        return entity;
    }

    @Override
    public boolean isValid() {
        return id != null
                && location != null
                && profile != null;
    }

    private long getCurrentTicks() {
        return location.getWorld().getFullTime();
    }

}
