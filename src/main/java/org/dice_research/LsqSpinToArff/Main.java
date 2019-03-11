package org.dice_research.LsqSpinToArff;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringUtils;
import org.dice_research.LsqSpinToArff.LSQ.LsqRunner;
import org.dice_research.LsqSpinToArff.Weka.Weka;

/**
 * Execute sequence of transformation steps.
 * 
 * Note: The whitelist of features can be specified in
 * {@link #FEATURES_WHITELIST}. It can be set to null to use all features;
 *
 * @author Adrian Wilke
 */
public class Main {

	// Configuration
	private static final boolean USE_FEATURES_WHITELIST = false;
	private static final boolean RUN_LSQ = true;

	private static final String POSTFIX_LSQ_POS = "weka.pos.lsq";
	private static final String POSTFIX_LSQ_NEG = "weka.neg.lsq";
	private static final String POSTFIX_ARFF = "weka.arff";
	private static final String POSTFIX_WEKA_MODEL = "weka.model";
	private static final String POSTFIX_META_CSV = "weka.csv";

	// Whitelist of features to integrate
	private static final String LSQV_NAMESPACE = "http://lsq.aksw.org/vocab#";
	public static final String[] FEATURES_WHITELIST = new String[] { "Ask", "Construct", "Describe", "Filter", "Group",
			"GroupBy", "Limit", "Offset", "OrderBy", "Select", "TriplePath", "TriplePattern", "Union", "fnbound",
			"fnisLiteral", "fnregex" };
	private static final List<String> featureWhitelist;
	static {
		if (USE_FEATURES_WHITELIST) {
			featureWhitelist = new LinkedList<>();
			for (String feature : FEATURES_WHITELIST) {
				featureWhitelist.add(LSQV_NAMESPACE + feature);
			}
		} else {
			featureWhitelist = null;
		}
	}

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
		long lsqTime = 0;
		File lsqPosFile = new File(outputDirectoryFile, prefix + POSTFIX_LSQ_POS);
		File lsqNegFile = new File(outputDirectoryFile, prefix + POSTFIX_LSQ_NEG);
		if (RUN_LSQ) {
			lsqTime = System.currentTimeMillis();
			System.out.println("Executing: " + lsqJarFile + " " + inputQueriesPosFile + " " + lsqPosFile.getPath());
			new LsqRunner().setJarFile(lsqJarFile).run(inputQueriesPosFile, lsqPosFile.getPath());

			System.out.println("Executing: " + lsqJarFile + " " + inputQueriesNegFile + " " + lsqNegFile.getPath());
			new LsqRunner().setJarFile(lsqJarFile).run(inputQueriesNegFile, lsqNegFile.getPath());
			lsqTime = System.currentTimeMillis() - lsqTime;
		}

		// LSQ/SPIN to ARFF

		long arffTime = System.currentTimeMillis();
		File arffFile = new File(outputDirectoryFile, prefix + POSTFIX_ARFF);
		System.out.println("Executing: " + lsqPosFile + " " + lsqNegFile + " " + arffFile);
		LsqSpinToArff lsqSpinToArff = new LsqSpinToArff().run(lsqPosFile, lsqNegFile, arffFile, featureWhitelist);
		arffTime = System.currentTimeMillis() - arffTime;

		// Get used LSQ/SPIN features

		List<String> featureUris = new ArrayList<>(lsqSpinToArff.getAllFeatures().size());
		for (String featureUri : lsqSpinToArff.getAllFeatures()) {
			featureUris.add(featureUri.replace("http://lsq.aksw.org/vocab#", "lsqv:"));
		}

		// Weka: ARFF to fMeasure

		long wekaTime = System.currentTimeMillis();
		Weka weka = new Weka().createData(arffFile);
		double fmeasure = weka.getfMeasure();
		wekaTime = System.currentTimeMillis() - wekaTime;

		// Write

		weka.writeModel(new File(outputDirectoryFile, prefix + POSTFIX_WEKA_MODEL));

		CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(new File(outputDirectoryFile, prefix + POSTFIX_META_CSV)),
				CSVFormat.DEFAULT);
		csvPrinter.printRecord(new String[] { "fMeasure", "" + fmeasure });
		csvPrinter.printRecord(new String[] { "timeLsq", "" + lsqTime });
		csvPrinter.printRecord(new String[] { "timeArff", "" + arffTime });
		csvPrinter.printRecord(new String[] { "timeWeka", "" + wekaTime });
		csvPrinter.printRecord(new String[] { "features", "" + featureUris });
		csvPrinter.printRecord(new String[] { "prefix", "" + prefix });
		csvPrinter.flush();
		csvPrinter.close();

		return fmeasure;
	}

	public double run(String inputQueriesPosFile, String inputQueriesNegFile, String outputDirectory) throws Exception {
		return run(inputQueriesPosFile, inputQueriesNegFile, outputDirectory, new LsqRunner().getJarFile());
	}
}