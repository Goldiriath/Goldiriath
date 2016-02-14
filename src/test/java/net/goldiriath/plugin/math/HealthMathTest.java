package net.goldiriath.plugin.math;

import static com.google.common.truth.Truth.*;
import java.math.BigDecimal;

import org.junit.Test;

public class HealthMathTest {

    @Test
    public void testBounds() {
        assertThat(HealthMath.f(1)).isWithin(0.1).of(100);

        // TODO: Fix formula
        //assertThat(HealthMath.f(50)).isWithin(0.1).of(10000);
    }

}
