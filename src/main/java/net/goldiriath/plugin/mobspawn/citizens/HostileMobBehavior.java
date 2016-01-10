package net.goldiriath.plugin.mobspawn.citizens;

import java.util.Random;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.ai.tree.Behavior;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.astar.pathfinder.MinecraftBlockExaminer;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class HostileMobBehavior implements Behavior {

    private static final long WANDER_DELAY_AVERAGE = 30L * 20L;
    private static final int WANDER_DELAY_MAX_OFFSET = 10 * 20;
    //
    private final Random random = new Random();
    private final Goldiriath plugin;
    private final NPC npc;
    private final Location base;
    private final int radius;
    private final int radiusSquared;
    //
    private long lastWalkTick = 0;
    private MobTarget target = null;

    public HostileMobBehavior(Goldiriath plugin, NPC npc, Location base, int radius) {
        this.plugin = plugin;
        this.npc = npc;
        this.base = base;
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    @Override
    public void reset() {
        target = null;
    }

    @Override
    public BehaviorStatus run() {
        if (target == null) {
            return BehaviorStatus.FAILURE;
        }

        if (target.getType() == TargetType.ENTITY) {
            final Player player = target.getPlayer();
            return player.isOnline() && !player.isDead() ? BehaviorStatus.RUNNING : BehaviorStatus.SUCCESS;
        }

        if (target.getType() == TargetType.LOCATION) {
            if (!npc.getNavigator().isNavigating()) {
                npc.faceLocation(npc.getEntity().getLocation().add((random.nextDouble() * 2) - 1, 0, (random.nextDouble() * 2) - 1));
                return BehaviorStatus.SUCCESS;
            }

            // We're walking to a location, try to find a new player target
            Player player = tryFindPlayer();
            if (player != null) { // Got one!
                target = new MobTarget(player);
                target.applyTo(npc);
            }

            return BehaviorStatus.RUNNING;
        }

        return BehaviorStatus.SUCCESS;
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.isSpawned() || npc.getNavigator().isNavigating()) {
            return false;
        }

        // Find a player to attack
        final Player player = tryFindPlayer();
        if (player != null) {
            target = new MobTarget(player);
            target.applyTo(npc);
            return true;
        }

        // Don't walk around all the time
        if (lastWalkTick != 0 && Util.getServerTick() - lastWalkTick < WANDER_DELAY_AVERAGE) {
            return false;
        }

        // Randomly walk around
        final Location loc = tryFindLocation();
        if (loc != null) {
            target = new MobTarget(loc);
            target.applyTo(npc);
            lastWalkTick = Util.getServerTick();

            // Apply a random offset so that walking does occur synchronised
            lastWalkTick += -WANDER_DELAY_MAX_OFFSET + (long) (random.nextDouble() * 2 * WANDER_DELAY_MAX_OFFSET);
            return true;
        }

        return false;
    }

    private Player tryFindPlayer() {
        for (Player player : base.getWorld().getPlayers()) {
            // Don't attack Creative / Adventure
            if (player.getGameMode() != GameMode.SURVIVAL) {
                continue;
            }

            // Don't attack NPCs
            if (plugin.msm.getBridge().isNPC(player)) {
                continue;
            }

            // TODO: Aggro here
            if (player.getLocation().distanceSquared(npc.getEntity().getLocation()) < radiusSquared) {
                return player;
            }
        }

        return null;
    }

    private Location tryFindLocation() {
        for (int tryNum = 0; tryNum < 10; tryNum++) {

            int x = base.getBlockX() + random.nextInt(2 * radius) - radius;
            int z = base.getBlockZ() + random.nextInt(2 * radius) - radius;

            // From 3 to -3 in Y change
            for (int i = 3; i > -3; i--) {
                int y = base.getBlockY() + i;

                Block block = base.getWorld().getBlockAt(x, y - 1, z);

                if (MinecraftBlockExaminer.canStandOn(block)) {
                    return block.getLocation().add(0, 1, 0);
                }
            }
        }

        return null;
    }
}
