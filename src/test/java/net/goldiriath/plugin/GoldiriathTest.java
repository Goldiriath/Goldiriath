package net.goldiriath.plugin;

import net.goldiriath.plugin.math.AggroMathTest;
import net.goldiriath.plugin.math.XPMathTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    XPMathTest.class,
    AggroMathTest.class})
public class GoldiriathTest {

}
