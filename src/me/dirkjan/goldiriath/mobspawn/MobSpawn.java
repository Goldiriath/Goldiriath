package me.dirkjan.goldiriath.mobspawn;

import me.dirkjan.goldiriath.util.Validatable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class MobSpawn implements Validatable {

    private final MobSpawnManager msm;
    private final String name;
    private final EntityType type;
    private final Location location;
    private final MobSpawnProfile profile;
    private long lastSpawn;

    public MobSpawn(MobSpawnManager msm, String name, EntityType type, Location location, MobSpawnProfile profile) {
        this.msm = msm;
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

    public MobSpawnProfile getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public long getLastSpawn() {
        return lastSpawn;
    }

    @Override
    public boolean isValid() {
        return type != null && location != null && name != null;
    }

    protected boolean tick() { // True if the tick resulted in a mob spawn
        if (!isValid()) {
            return false;
        }

        int closemobs = 0;
        for (Entity entity : location.getWorld().getLivingEntities()) {
            if (entity instanceof LivingEntity && entity.getLocation().distanceSquared(location) < msm.getRadiusSquared()) {
                closemobs++;
            }
        }

        if (closemobs > msm.getMaxMobs()) {
            return false;
        }

        if (getCurrentTicks() - lastSpawn < msm.getSpawnThreshold()) {
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
