package net.goldiriath.plugin.math;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;

/*
 * https://github.com/Goldiriath/Goldiriath/wiki/XP-Math
 */
public class XPMathTest {

    @Test
    public void testMax() {
        assertThat(XPMath.f(XPMath.MAX_LEVEL)).isEqualTo(XPMath.MAX_XP);
        assertThat(XPMath.g(XPMath.MAX_XP)).isWithin(0.001).of(XPMath.MAX_LEVEL);
    }

    @Test
    public void testWithLevel() {
        assertThat(XPMath.levelToXp(0)).isEqualTo(0);
        assertThat(XPMath.levelToXp(1)).isEqualTo(0);
        assertThat(XPMath.levelToXp(2)).isEqualTo(100);

        assertThat(XPMath.levelToXp(13)).isEqualTo(7800);
        assertThat(XPMath.levelToXp(14)).isEqualTo(9100);
        assertThat(XPMath.levelToNextXp(13)).isEqualTo(1300);
    }

    @Test
    public void testWithXp() {
        assertThat(XPMath.xpToLevel(0)).isEqualTo(1);
        assertThat(XPMath.xpToLevel(98)).isEqualTo(1);
        assertThat(XPMath.xpToLevel(99)).isEqualTo(1);
        assertThat(XPMath.xpToLevel(100)).isEqualTo(2);
        assertThat(XPMath.xpToLevel(101)).isEqualTo(2);

        assertThat(XPMath.xpToLevel(15800)).isEqualTo(18);
        assertThat(XPMath.xpToLevelXp(15800)).isEqualTo(500);
        assertThat(XPMath.xpToNextXp(15800)).isEqualTo(1300);
    }

}
