package org.dice_research.LsqSpinToArff;

import java.io.File;

import org.dice_research.LsqSpinToArff.LSQ.LsqRunner;
import org.dice_research.LsqSpinToArff.Weka.Weka;

/**
 * Execute sequence of transformation steps.
 *
 * @author Adrian Wilke
 */
public class Main {

	public static void main(String[] args) throws Exception {

		String lsqJarFile;
		if (args.length > 2) {
			lsqJarFile = args[2];
		} else {
			lsqJarFile = new LsqRunner().getJarFile();
		}
		if (!new File(lsqJarFile).exists()) {
			System.err.println("Please provide LSQ CLI Jar file (with dependencies).");
			System.err.println("Not found: " + lsqJarFile);
			System.exit(4);
		}

		if (args.length < 2) {
			System.err.println("Please provide SPARQL files with positive and negative sets");
			System.exit(3);
		}

		if (!new File(args[0]).canRead()) {
			System.err.println("Can not read " + args[0]);
			System.exit(1);
		}
		if (!new File(args[1]).canRead()) {
			System.err.println("Can not read " + args[1]);
			System.exit(2);
		}

		System.out.println(new Main().run(args[0], args[1]));
	}

	public double run(String inputQueriesPosFile, String inputQueriesNegFile, String lsqJarFile) throws Exception {

		// SPARQL queries to LSQ/SPIN

		File lsqPosFile = File.createTempFile("LsqSpinToArff", ".pos.lsq");
		lsqPosFile.deleteOnExit();
		new LsqRunner().setJarFile(lsqJarFile).run(inputQueriesPosFile, lsqPosFile.getPath());

		File lsqNegFile = File.createTempFile("LsqSpinToArff", ".pos.lsq");
		lsqPosFile.deleteOnExit();
		new LsqRunner().run(inputQueriesPosFile, lsqNegFile.getPath());

		// LSQ/SPIN to ARFF

		File arffFile = File.createTempFile("LsqSpinToArff", ".arff");
		arffFile.deleteOnExit();
		new LsqSpinToArff().run(lsqPosFile, lsqNegFile, arffFile);

		// ARFF to fMeasure

		return new Weka().createData(arffFile.toURI().toURL().toString()).getfMeasure();
	}

	public double run(String inputQueriesPosFile, String inputQueriesNegFile) throws Exception {
		return run(inputQueriesPosFile, inputQueriesNegFile, new LsqRunner().getJarFile());
	}
}