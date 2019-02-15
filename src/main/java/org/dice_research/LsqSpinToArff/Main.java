package org.dice_research.LsqSpinToArff;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import weka.core.Instances;

/**
 * Main entry point.
 * 
 * @author Adrian Wilke
 */
public class Main {

	public static boolean PRINT = true;
	public static boolean PRINT_EXTRACTION = false;
	public static boolean PRINT_INSTANCES = true;

	public static void main(String[] args) throws IOException {

		// Check user input

		if (args.length < 2) {
			System.err.println("Please provide a SPIN turtle file path.");
			System.err.println("And please provide an output file path.");
			System.exit(1);
		}

		File inFile = new File(args[0]);
		if (!inFile.canRead()) {
			System.err.println("can not read input file");
			System.exit(1);
		}

		File outFile = new File(args[1]);

		if (PRINT) {
			System.out.println("\n" + "FILES" + "\n");
			System.out.println(inFile);
			System.out.println(outFile);
		}

		// Extract information out of SPARQL queries

		Map<String, Query> queries = Reader.extract(inFile.getAbsolutePath());
		if (PRINT_EXTRACTION) {
			System.out.println("\n" + "EXTRACTION" + "\n");
			StringBuilder stringBuilder = new StringBuilder();
			for (Query query : queries.values()) {
				stringBuilder.append(query.toStringBuilder());
			}
			System.out.println(stringBuilder);
		}

		// Create ARFF

		List<String> features = Converter.getAllFeatures(queries.values());
		if (PRINT) {
			System.out.println("\n" + "FEATURES" + "\n");
			for (String feature : features) {
				System.out.println(feature);
			}
		}

		Instances instances = Converter.createArff(queries.values(), features);
		if (PRINT_INSTANCES) {
			System.out.println("\n" + "INSTANCES" + "\n");
			System.out.println(instances);
		}

		// Write

		Writer.write(instances, outFile);
	}

}