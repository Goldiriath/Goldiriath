package net.goldiriath.plugin.mobspawn.citizens;

import java.util.Random;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.ai.tree.Behavior;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.astar.pathfinder.MinecraftBlockExaminer;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.math.AggroMath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class HostileMobBehavior implements Behavior {

    private static final long WANDER_DELAY_AVERAGE = 30L * 20L;
    private static final int WANDER_DELAY_MAX_OFFSET = 10 * 20;
    private static final int AGGRO_CHECK_RANGE_SQUARED = 35 * 35;
    private static final long AGGRO_CHECK_DELAY = 5 * 20L;
    //
    private final Random random = new Random();
    private final Goldiriath plugin;
    private final NPC npc;
    private final Location base;
    private final int wanderRadius;
    //
    private long lastWalkTick = 0;
    private MobTarget currentTarget = null;
    private long lastAggroCheckTick = 0;

    public HostileMobBehavior(Goldiriath plugin, NPC npc, Location base, int radius) {
        this.plugin = plugin;
        this.npc = npc;
        this.base = base;
        this.wanderRadius = radius;
    }

    @Override
    public void reset() {
        target(null);
    }

    @Override
    public BehaviorStatus run() {
        if (currentTarget == null) {
            return BehaviorStatus.FAILURE;
        }

        if (currentTarget.getType() == TargetType.ENTITY) {
            final Player player = currentTarget.getPlayer();

            // Player's dead/offline: We're done
            if (!player.isOnline() && player.isDead()) {
                return BehaviorStatus.SUCCESS;
            }

            // If we've been tracking this player for a while,
            // see if we can attack another player
            if (Util.getServerTick() < lastAggroCheckTick + AGGRO_CHECK_DELAY) {

                Player newPlayer = findAggroPlayer();
                if (newPlayer != null && !newPlayer.equals(currentTarget.getPlayer())) {
                    // Found a new player
                    target(new MobTarget(player));
                }

            }

            return BehaviorStatus.RUNNING;
        }

        if (currentTarget.getType() == TargetType.LOCATION) {
            if (!npc.getNavigator().isNavigating()) {
                // Face a random location
                npc.faceLocation(npc.getEntity().getLocation().add((random.nextDouble() * 2) - 1, 0, (random.nextDouble() * 2) - 1));
                return BehaviorStatus.SUCCESS;
            }

            // We're walking to a location, try to find a new player target
            Player player = findAggroPlayer();
            if (player != null) { // Got one!
                target(new MobTarget(player));
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
        final Player player = findAggroPlayer();
        if (player != null) {
            target(new MobTarget(player));
            return true;
        }

        // Don't walk around all the time
        if (lastWalkTick != 0 && Util.getServerTick() - lastWalkTick < WANDER_DELAY_AVERAGE) {
            return false;
        }

        // Randomly walk around
        final Location loc = findLocation();
        if (loc != null) {
            target(new MobTarget(loc));
            lastWalkTick = Util.getServerTick();

            // Apply a random offset so that walking is not synchronised
            lastWalkTick += -WANDER_DELAY_MAX_OFFSET + (long) (random.nextDouble() * 2 * WANDER_DELAY_MAX_OFFSET);
            return true;
        }

        return false;
    }

    private Player findAggroPlayer() {

        Player toAttack = null;
        double toAttackAggro = 0;

        for (Player player : base.getWorld().getPlayers()) {
            // Don't attack Creative / Adventure
            if (player.getGameMode() != GameMode.SURVIVAL) {
                continue;
            }

            // Don't attack NPCs
            if (plugin.msm.getBridge().isNPC(player)) {
                continue;
            }

            // Is the player close enough?
            if (player.getLocation().distanceSquared(npc.getEntity().getLocation()) > AGGRO_CHECK_RANGE_SQUARED) {
                continue;
            }

            final PlayerData data = plugin.pm.getData(player);

            // Can we attack this player?
            if (!AggroMath.canAttack(npc, data)) {
                continue;
            }

            // Find the highest aggro
            double playerAggro = AggroMath.aggro(npc, data);
            if (playerAggro > toAttackAggro) {
                toAttack = player;
                toAttackAggro = playerAggro;
            }
        }

        lastAggroCheckTick = Util.getServerTick();
        return toAttack;
    }

    private void target(MobTarget newTarget) {
        // If the target's a player, this will remove the mob from the assailants list
        if (currentTarget != null) {
            currentTarget.unapplyTo(npc);
        }

        // Set the new target
        currentTarget = newTarget;

        if (currentTarget != null) {
            currentTarget.applyTo(npc);
        }
    }

    private Location findLocation() {
        for (int tryNum = 0; tryNum < 10; tryNum++) {

            int x = base.getBlockX() + random.nextInt(2 * wanderRadius) - wanderRadius;
            int z = base.getBlockZ() + random.nextInt(2 * wanderRadius) - wanderRadius;

            // From 3 to -3 in Y change
            for (int i = 3; i > -3; i--) {
                int y = base.getBlockY() + i;

                Block block = base.getWorld().getBlockAt(x, y, z);

                if (MinecraftBlockExaminer.canStandOn(block)) {
                    return MinecraftBlockExaminer.findValidLocation(block.getLocation().add(0, 1, 0), 2);
                }
            }
        }

        return null;
    }
}
