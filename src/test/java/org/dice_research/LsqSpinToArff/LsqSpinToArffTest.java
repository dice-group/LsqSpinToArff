package org.dice_research.LsqSpinToArff;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LsqSpinToArffTest {

	/**
	 * Simple run
	 */
	@org.junit.Test
	public void test() throws IOException {

		File outFile = File.createTempFile("LsqSpinToArffTest", ".arff");

		try {

			List<String> args = new LinkedList<String>();
			args.add(new File("src/main/resources/positive.ttl").getAbsolutePath());
			args.add(new File("src/main/resources/negative.ttl").getAbsolutePath());
			args.add(outFile.getAbsolutePath());

			Main.main(args.toArray(new String[0]));

			assertTrue(outFile.exists());
			assertTrue(outFile.length() > 0);

		} finally {
			outFile.delete();
		}
	}
}