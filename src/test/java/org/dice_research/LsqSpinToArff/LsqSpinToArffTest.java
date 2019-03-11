package org.dice_research.LsqSpinToArff;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class LsqSpinToArffTest {

	public static final boolean PRINT_RESULT_LINES = false;

	@org.junit.Test
	public void test() throws IOException {

		File outFile = File.createTempFile("LsqSpinToArffTest", ".arff");
		outFile.deleteOnExit();

		File inFilePositive = new File("src/main/resources/positive.ttl");
		File inFileNegative = new File("src/main/resources/negative.ttl");

		new LsqSpinToArff().run(inFilePositive, inFileNegative, outFile, null);

		assertTrue(outFile.exists());
		assertTrue(outFile.length() > 0);

		if (PRINT_RESULT_LINES) {
			for (String line : FileUtils.readLines(outFile, StandardCharsets.UTF_8)) {
				System.out.println(line);
			}
		}
	}

	@org.junit.Test
	public void testWhtelist() throws IOException {

		List<String> featureWhitelist = new LinkedList<>();
		featureWhitelist.add("http://lsq.aksw.org/vocab#Select");
		featureWhitelist.add("http://lsq.aksw.org/vocab#OrderBy");

		File outFile = File.createTempFile("LsqSpinToArffTest", ".arff");
		outFile.deleteOnExit();

		File inFilePositive = new File("src/main/resources/positive.ttl");
		File inFileNegative = new File("src/main/resources/negative.ttl");

		new LsqSpinToArff().run(inFilePositive, inFileNegative, outFile, featureWhitelist);

		assertTrue(outFile.exists());
		assertTrue(outFile.length() > 0);

		if (PRINT_RESULT_LINES) {
			for (String line : FileUtils.readLines(outFile, StandardCharsets.UTF_8)) {
				System.out.println(line);
			}
		}
	}
}