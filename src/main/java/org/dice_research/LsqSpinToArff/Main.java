package org.dice_research.LsqSpinToArff;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringUtils;
import org.dice_research.LsqSpinToArff.LSQ.LsqRunner;
import org.dice_research.LsqSpinToArff.Weka.Weka;

/**
 * Execute sequence of transformation steps.
 *
 * @author Adrian Wilke
 */
public class Main {

	/**
	 * @param args [0] File with positive queries
	 * 
	 *             [1] File with negative queries
	 * 
	 *             [2] Output directory
	 * 
	 *             [3] (Optional) LSQ Jar file
	 */
	public static void main(String[] args) throws Exception {

		// Set LSQ Jar file

		String lsqJarFile;
		if (args.length > 3) {
			lsqJarFile = args[3];
		} else {
			lsqJarFile = new LsqRunner().getJarFile();
		}
		if (!new File(lsqJarFile).exists()) {
			System.err.println("Please provide LSQ CLI Jar file (with dependencies).");
			System.err.println("Not found: " + lsqJarFile);
			System.exit(1);
		}

		// Check required argumets

		if (args.length < 3) {
			System.err.println(
					"Please provide SPARQL files with positive (1) and negative (2) sets and output directory (3).");
			System.exit(1);
		}

		// Check filesystem

		if (!new File(args[0]).canRead()) {
			System.err.println("Can not read " + args[0]);
			System.exit(1);
		}
		if (!new File(args[1]).canRead()) {
			System.err.println("Can not read " + args[1]);
			System.exit(1);
		}
		if (!new File(args[2]).canWrite()) {
			System.err.println("Can not write output directory: " + args[2]);
			System.exit(1);
		}

		// Run

		System.out.println(new Main().run(args[0], args[1], args[2], lsqJarFile));
	}

	public double run(String inputQueriesPosFile, String inputQueriesNegFile, String outputDirectory, String lsqJarFile)
			throws Exception {

		File outputDirectoryFile = new File(outputDirectory);
		String prefix = StringUtils.getCommonPrefix(
				new String[] { new File(inputQueriesPosFile).getName(), new File(inputQueriesNegFile).getName() });

		// SPARQL queries to LSQ/SPIN

		long lsqTime = System.currentTimeMillis();
		File lsqPosFile = new File(outputDirectoryFile, prefix + "weka.pos.lsq");
		System.out.println("Executing: " + lsqJarFile + " " + inputQueriesPosFile + " " + lsqPosFile.getPath());
		new LsqRunner().setJarFile(lsqJarFile).run(inputQueriesPosFile, lsqPosFile.getPath());

		File lsqNegFile = new File(outputDirectoryFile, prefix + "weka.neg.lsq");
		System.out.println("Executing: " + lsqJarFile + " " + inputQueriesNegFile + " " + lsqNegFile.getPath());
		new LsqRunner().setJarFile(lsqJarFile).run(inputQueriesNegFile, lsqNegFile.getPath());
		lsqTime = System.currentTimeMillis() - lsqTime;

		// LSQ/SPIN to ARFF

		long arffTime = System.currentTimeMillis();
		File arffFile = new File(outputDirectoryFile, prefix + "weka.arff");
		System.out.println("Executing: " + lsqPosFile + " " + lsqNegFile + " " + arffFile);
		new LsqSpinToArff().run(lsqPosFile, lsqNegFile, arffFile);
		arffTime = System.currentTimeMillis() - arffTime;

		// Weka: ARFF to fMeasure

		long wekaTime = System.currentTimeMillis();
		Weka weka = new Weka().createData(arffFile.toURI().toURL().toString());
		double fmeasure = weka.getfMeasure();
		wekaTime = System.currentTimeMillis() - wekaTime;

		// Write

		weka.writeModel(new File(outputDirectoryFile, prefix + "weka.model"));

		CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File(outputDirectoryFile, prefix + "weka.csv")),
				CSVFormat.DEFAULT);
		csvPrinter.printRecord(new String[] { "fMeasure", "" + fmeasure });
		csvPrinter.printRecord(new String[] { "timeLsq", "" + lsqTime });
		csvPrinter.printRecord(new String[] { "timeArff", "" + arffTime });
		csvPrinter.printRecord(new String[] { "timeWeka", "" + wekaTime });
		csvPrinter.flush();
		csvPrinter.close();

		return fmeasure;
	}

	public double run(String inputQueriesPosFile, String inputQueriesNegFile, String outputDirectory) throws Exception {
		return run(inputQueriesPosFile, inputQueriesNegFile, outputDirectory, new LsqRunner().getJarFile());
	}
}