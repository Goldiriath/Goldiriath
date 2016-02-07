package net.goldiriath.plugin.mobspawn;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.mobspawn.citizens.HostileMobBehavior;
import net.goldiriath.plugin.mobspawn.citizens.MobSpawnTrait;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.ConfigSavable;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.Validatable;
import net.pravian.bukkitlib.serializable.SerializableBlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MobSpawn implements ConfigLoadable, ConfigSavable, Validatable {

    private final MobSpawnManager msm;
    private final Logger logger;
    @Getter
    private final String id;
    private final Set<NPC> mobs = Sets.newHashSet();
    //
    @Getter
    @Setter
    private MobSpawnProfile profile;
    @Getter
    @Setter
    private Location location;
    @Getter
    @Setter
    private int maxMobs;
    @Getter
    private long lastSpawn = 0;

    public MobSpawn(MobSpawnManager msm, String id) {
        this.msm = msm;
        this.logger = msm.getPlugin().getLogger();
        this.id = id;
    }

    public boolean hasMaxMobs() {
        return maxMobs >= 1;
    }

    public Set<NPC> getMobs() {
        return Collections.unmodifiableSet(mobs);
    }

    public int killAll() {
        removeDead();
        int count = mobs.size();

        for (NPC mob : mobs) {
            mob.destroy();
        }

        return count;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        killAll();

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

        return spawn();
    }

    public boolean canSpawn() {
        if (!isValid()) {
            return false;
        }

        // Check spawn timeout
        if (Util.getServerTick() - lastSpawn < (profile.hasTimeThreshold() ? profile.getTimeThreshold() : msm.getTimeThreshold())) {
            return false;
        }

        // Remove dead mobs
        removeDead();

        // Check max mobs
        if (mobs.size() >= (hasMaxMobs() ? maxMobs : msm.getMaxMobs())) {
            return false;
        }

        // Check player close
        boolean playerClose = false;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getLocation().distanceSquared(location) > msm.getRadiusSquaredThreshold()) {
                continue;
            }

            playerClose = true;
            break;
        }

        return playerClose;
    }

    public boolean spawn() {
        if (!isValid()) {
            return false;
        }

        lastSpawn = Util.getServerTick();

        final NPC mob = profile.spawn(location);
        mobs.add(mob);

        mob.addTrait(new MobSpawnTrait(this));

        return true;
    }

    @Override
    public boolean isValid() {
        return id != null
                && location != null
                && profile != null;
    }

    private void removeDead() {
        Iterator<NPC> it = mobs.iterator();
        while (it.hasNext()) {
            NPC npc = it.next();
            if (!npc.isSpawned()) {
                npc.destroy();
                it.remove();
            }
        }

    }

}
