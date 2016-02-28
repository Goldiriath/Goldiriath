package net.goldiriath.plugin.math;

import com.google.common.annotations.VisibleForTesting;

public class XPMath {

    public static final int MAX_LEVEL = 50;
    public static final int MAX_XP = f(MAX_LEVEL);

    private XPMath() {
    }

    @VisibleForTesting
    static int f(int x) {
        return 50 * (x - 1) * x;
    }

    @VisibleForTesting
    static double g(int x) {
        return 0.1 * (Math.sqrt(2 * x + 25) + 5);
    }

    // TODO: Document on the forums
    @VisibleForTesting
    static int h(int p, int m) {
        int xpGain = 1;
        int diffLevel = Math.abs(p - m);

        if (diffLevel <= 1) {
            xpGain = 5;
        }
        if (diffLevel >= 2 && diffLevel <= 3 && m >= p) {
            xpGain = 7;
        }
        if (diffLevel >= 2 && diffLevel <= 3 && p >= m) {
            xpGain = 2;
        }

        return xpGain;
    }

    // Level -> Something
    public static int levelToXp(int level) {
        return f(level);
    }

    public static int levelToNextXp(int level) {
        return levelToXp(level + 1) - levelToXp(level);
    }

    public static int xpToLevel(int xp) {
        return (int) Math.floor(g(xp));
    }

    public static int xpToLevelXp(int xp) {
        int currentLevel = xpToLevel(xp);
        return xp - levelToXp(currentLevel);
    }

    public static int xpToNextXp(int xp) {
        int currentLevel = xpToLevel(xp);
        int levelXp = xpToLevelXp(xp);
        return levelToXp(currentLevel + 1) - levelToXp(currentLevel) - levelXp;
    }

    public static int xpGainForKill(int playerLevel, int mobLevel) {
        return h(playerLevel, mobLevel);
    }
}
