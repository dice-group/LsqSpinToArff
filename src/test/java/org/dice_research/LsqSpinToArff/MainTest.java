package org.dice_research.LsqSpinToArff;

import java.io.File;

import org.junit.Test;

import com.google.common.io.Files;

public class MainTest {

	public static final boolean DELETE_TMP_DIR = true;

	@Test
	public void test() throws Exception {
		long time = System.currentTimeMillis();
		String[] args = new String[3];
		args[0] = new File(getClass().getClassLoader().getResource("positive.txt").toURI()).getPath();
		args[1] = new File(getClass().getClassLoader().getResource("negative.txt").toURI()).getPath();
		File tmpOutDir = Files.createTempDir();
		args[2] = tmpOutDir.getPath();
		Main.main(args);
		if (DELETE_TMP_DIR) {
			for (File tmpFile : tmpOutDir.listFiles()) {
				tmpFile.delete();
			}
			tmpOutDir.delete();
		} else {
			System.out.println("Tmp directory: " + tmpOutDir.getPath());
		}
		System.out.println("Runtime (millis): " + (System.currentTimeMillis() - time));
	}
}