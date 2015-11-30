package net.goldiriath.plugin.math;

import com.google.common.annotations.VisibleForTesting;

/*
 * https://github.com/Goldiriath/Goldiriath/wiki/Calculation:-Leveling
 */
public class XPMath {

    public static final int MAX_LEVEL = 50;
    public static final int MAX_XP = f(MAX_LEVEL);

    @VisibleForTesting
    static int f(int x) {
        return 50 * (x - 1) * x;
    }

    @VisibleForTesting
    static double g(int x) {
        return 0.1 * (Math.sqrt(2 * x + 25) + 5);
    }

    // Level -> Something
    public static int levelToXp(int level) {
        return f(level);
    }

    public static int levelToNextXp(int level) {
        return levelToXp(level + 1) - levelToXp(level);
    }

    // XP -> Something
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
}
