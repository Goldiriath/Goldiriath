package net.goldiriath.plugin.math;

import com.google.common.annotations.VisibleForTesting;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.game.citizens.HostileMobTrait;
import net.goldiriath.plugin.player.PlayerData;

public class AggroMath {

    /**
     * Calculates a player's exposure.
     *
     * @param x Distance between the mob and the player
     * @param m Amount of mobs targeting the player
     *
     * @return Aggro value
     */
    @VisibleForTesting
    static double f(double x, int m) {
        return // Distance modifier: < 0.0 , 1.0 >
                (15 / x)
                // Aggresion increasal: + [0.0, 0.5]
                + Math.min(0.5, (0.1 * m));
    }

    /**
     * Calculates a mob-perceived aggro value for player.
     *
     * @param d Total damage this player has dealt to the mob
     * @param s Skill modifier
     * @param x Distance between the mob and the player
     * @param m Amount of mobs targeting the player
     * @return Aggro value
     */
    @VisibleForTesting
    static double g(int d, int h, double x, double s, int m) {

        return 1.0
                // Damage liability modifier: [ 1.0 , -> >
                * Math.max(1.0, ((double) d / (double) h) + 1.0)
                // Skill modifier: [ 0.2 , -> >
                * Math.max(0.2, s)
                // Distance modifier
                * (1.0 / x)
                // Aggression modifier: [1.0, 2.0]
                * (2.0 - (Math.min(4, m) / 4.0));
    }

    public static double exposure(NPC mob, PlayerData player) {
        double x = mob.getEntity().getLocation().distance(player.getPlayer().getLocation());
        int m = player.getBattle().getAssailingNpcs().size();

        return f(x, m);
    }

    public static boolean canAttack(NPC mob, PlayerData player) {
        return exposure(mob, player) >= 1.0;
    }

    public static double aggro(NPC mob, PlayerData player) {

        final HostileMobTrait hostile = mob.getTrait(HostileMobTrait.class);
        if (hostile == null) {
            return 1.0;
        }

        int d = hostile.getInflictedDamage(player.getPlayer());
        int h = hostile.getMaxHealth();
        double x = mob.getEntity().getLocation().distance(player.getPlayer().getLocation());
        double s = 1.0;
        int m = player.getBattle().getAssailingNpcs().size();

        return g(d, h, x, s, m);
    }

}
