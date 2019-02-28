package org.dice_research.LsqSpinToArff;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

public class LsqSpinToArffTest {

	@org.junit.Test
	public void test() throws IOException {

		File outFile = File.createTempFile("LsqSpinToArffTest", ".arff");
		outFile.deleteOnExit();

		File inFilePositive = new File("src/main/resources/positive.ttl");
		File inFileNegative = new File("src/main/resources/negative.ttl");

		new LsqSpinToArff().run(inFilePositive, inFileNegative, outFile);

		assertTrue(outFile.exists());
		assertTrue(outFile.length() > 0);
	}
}