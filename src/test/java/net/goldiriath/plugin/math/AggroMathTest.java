package net.goldiriath.plugin.math;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;

/*
 * https://github.com/Goldiriath/Goldiriath/issues/40
 */
public class AggroMathTest {

    @Test
    public void testAssumptions() {

        // Distance: attack?
        assertThat(AggroMath.f(30, 0)).isLessThan(1.0);
        assertThat(AggroMath.f(15, 0)).isWithin(0.001).of(1.0);
        assertThat(AggroMath.f(10, 0)).isGreaterThan(1.0);
        assertThat(AggroMath.f(16, 1)).isGreaterThan(1.0);
        assertThat(AggroMath.f(20, 1)).isLessThan(1.0);
        assertThat(AggroMath.f(25, 5)).isGreaterThan(1.0);
    }

    @Test
    public void testDamageProgression() {
        double d1 = AggroMath.g(00, 50, 15, 1, 0);
        double d2 = AggroMath.g(20, 50, 15, 1, 0);
        double d3 = AggroMath.g(36, 50, 15, 1, 0);
        assertThat(d1).isLessThan(d2);
        assertThat(d2).isLessThan(d3);
    }

    @Test
    public void testDistanceProgression() {
        double x1 = AggroMath.g(0, 50, 16, 1, 0);
        double x2 = AggroMath.g(0, 50, 15, 1, 0);
        double x3 = AggroMath.g(0, 50, 14, 1, 0);
        assertThat(x1).isLessThan(x2);
        assertThat(x2).isLessThan(x3);
    }

    @Test
    public void testSkillProgression() {
        double s1 = AggroMath.g(0, 50, 15, 0.9, 0);
        double s2 = AggroMath.g(0, 50, 15, 1.0, 0);
        double s3 = AggroMath.g(0, 50, 15, 1.1, 0);
        assertThat(s1).isLessThan(s2);
        assertThat(s2).isLessThan(s3);
    }

    @Test
    public void testMobsProgression() {
        double m1 = AggroMath.g(0, 50, 15, 1, 4);
        double m2 = AggroMath.g(0, 50, 15, 1, 2);
        double m3 = AggroMath.g(0, 50, 15, 1, 1);

        assertThat(m1).isLessThan(m2);
        assertThat(m2).isLessThan(m3);

        double mTop = AggroMath.g(0, 50, 15, 1, 20);
        assertThat(m1).isWithin(0.001).of(mTop);
    }

}
