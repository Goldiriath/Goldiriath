package me.dirkjan.goldiriath.mobspawn;

import java.util.logging.Logger;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.ConfigSavable;
import me.dirkjan.goldiriath.util.Validatable;
import net.pravian.bukkitlib.serializable.SerializableBlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MobSpawn implements ConfigLoadable, ConfigSavable, Validatable {

    private final MobSpawnManager msm;
    private final Logger logger;
    private final String id;
    //
    private MobSpawnProfile profile;
    private Location location;
    private long lastSpawn;

    public MobSpawn(MobSpawnManager msm, String id) {
        this.msm = msm;
        this.logger = msm.getPlugin().getLogger();
        this.id = id;
        this.lastSpawn = 0;
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

    @Override
    public void loadFrom(ConfigurationSection config) {
        final String locationString = config.getString("location");
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
        }

        profile = msm.getProfile(profileString);
        if (profile == null) {
            logger.warning("Ignoring profile '" + profileString + "' for MobSpawn '" + id + "'. Profile could not be determined!");
        }
    }

    @Override
    public void saveTo(ConfigurationSection config) {
        if (!isValid()) {
            logger.warning("Not saving mobspawn: '" + id + "' mobspawn is invalid");
            return;
        }

        config.set("location", new SerializableBlockLocation(location).serialize());
        config.set("profile", profile.getId());
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

        if (getCurrentTicks() - lastSpawn < (profile.hasSpawnThreshold() ? profile.getSpawnThreshold() : msm.getSpawnThreshold())) {
            return false;
        }

        boolean playerClose = false;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getLocation().distanceSquared(location) > msm.getRadiusSquared()) {
                continue;
            }

            playerClose = true;
            break;
        }

        if (!playerClose) {
            return false;
        }

        int closemobs = 0;
        for (Entity entity : location.getWorld().getLivingEntities()) {
            if (entity.getType() == profile.getType() && entity.getLocation().distanceSquared(location) < msm.getRadiusSquared()) {
                closemobs++;
            }
        }

        return closemobs <= msm.getMaxMobs();
    }

    public LivingEntity spawn() {
        if (!isValid()) {
            return null;
        }

        lastSpawn = getCurrentTicks();

        return profile.spawn(location);
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
