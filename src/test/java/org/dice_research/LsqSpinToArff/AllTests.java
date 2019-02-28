package org.dice_research.LsqSpinToArff;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

		LsqTest.class,

		LsqSpinToArffTest.class,

		WekaTest.class,

		MainTest.class })

public class AllTests {
}