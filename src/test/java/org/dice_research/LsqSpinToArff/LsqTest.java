package org.dice_research.LsqSpinToArff;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.dice_research.LsqSpinToArff.LSQ.LsqRunner;
import org.junit.Test;

public class LsqTest {

	/**
	 * @see https://www.w3.org/TR/rdf-sparql-query/#select
	 */
	public static final String SPARQL = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> SELECT ?nameX ?nameY ?nickY WHERE"
			+ " { ?x foaf:knows ?y ; foaf:name ?nameX . ?y foaf:name ?nameY . OPTIONAL { ?y foaf:nick ?nickY } }";

	@Test
	public void test() throws Exception {

		LsqRunner lsqRunner = new LsqRunner();
		assumeTrue("LSQ jar file exists", new File(lsqRunner.getJarFile()).exists());

		File inputFile = File.createTempFile("LsqTestInput.", ".txt");
		inputFile.deleteOnExit();
		File outputFile = File.createTempFile("LsqTestOutput.", ".txt");
		outputFile.deleteOnExit();

		FileUtils.write(inputFile, SPARQL, StandardCharsets.UTF_8);

		lsqRunner.run(inputFile.getPath(), outputFile.getPath());

		String result = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);

		assertTrue(result.contains("<http://lsq.aksw.org/vocab#usesFeature>"));
	}

}