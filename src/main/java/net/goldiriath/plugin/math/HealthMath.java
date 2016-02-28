package net.goldiriath.plugin.math;

import com.google.common.annotations.VisibleForTesting;

public class HealthMath {

    private HealthMath() {
    }

    @VisibleForTesting
    static double f(int l) {
        return 100D * Math.pow(1.096479D, l - 1);
    }

    public static int levelToMaxHealth(int level) {
        return (int) f(level);
    }

}
