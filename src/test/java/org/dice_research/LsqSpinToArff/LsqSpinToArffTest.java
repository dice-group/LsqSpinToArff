package org.dice_research.LsqSpinToArff;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

public class LsqSpinToArffTest {

	public static final boolean PRINT_RESULT_LINES = false;

	@org.junit.Test
	public void test() throws IOException {

		File outFile = File.createTempFile("LsqSpinToArffTest", ".arff");
		outFile.deleteOnExit();

		File inFilePositive = new File("src/main/resources/positive.ttl");
		File inFileNegative = new File("src/main/resources/negative.ttl");

		new LsqSpinToArff().run(inFilePositive, inFileNegative, outFile);

		assertTrue(outFile.exists());
		assertTrue(outFile.length() > 0);

		if (PRINT_RESULT_LINES) {
			for (String line : FileUtils.readLines(outFile, StandardCharsets.UTF_8)) {
				System.out.println(line);
			}
		}
	}
}