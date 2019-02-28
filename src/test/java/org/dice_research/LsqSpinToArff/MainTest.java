package org.dice_research.LsqSpinToArff;

import java.io.File;

import org.junit.Test;

public class MainTest {

	@Test
	public void test() throws Exception {
		long time = System.currentTimeMillis();
		String[] args = new String[2];
		args[0] = new File(getClass().getClassLoader().getResource("positive.txt").toURI()).getPath();
		args[1] = new File(getClass().getClassLoader().getResource("negative.txt").toURI()).getPath();
		Main.main(args);
		System.out.println("Runtime (millis): " + (System.currentTimeMillis() - time));
	}
}