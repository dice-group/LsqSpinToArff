package org.dice_research.LsqSpinToArff;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;

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

		if (args.length < 3) {
			System.err.println("Please provide 3 arguments:");
			System.err.println("- Input file with positive datasets (LSQ/SPIN/TURTLE format)");
			System.err.println("- Input file with negative datasets (LSQ/SPIN/TURTLE format)");
			System.err.println("- Output file (ARFF format)");
			System.exit(1);
		}

		File inFilePositive = new File(args[0]);
		if (!inFilePositive.canRead()) {
			System.err.println("can not read positive input file");
			System.exit(1);
		}

		File inFileNegative = new File(args[1]);
		if (!inFileNegative.canRead()) {
			System.err.println("can not read negative input file");
			System.exit(1);
		}

		File outFile = new File(args[2]);

		if (PRINT) {
			System.out.println("\n" + "FILES" + "\n");
			System.out.println(inFilePositive);
			System.out.println(inFileNegative);
			System.out.println(outFile);
		}

		// Extract information out of SPARQL queries

		Collection<Query> queriesPositive = Reader.extract(inFilePositive.getAbsolutePath());
		if (PRINT_EXTRACTION) {
			System.out.println("\n" + "EXTRACTION, POSITIVE" + "\n");
			StringBuilder stringBuilder = new StringBuilder();
			for (Query query : queriesPositive) {
				stringBuilder.append(query.toStringBuilder());
			}
			System.out.println(stringBuilder);
		}

		Collection<Query> queriesNegative = Reader.extract(inFilePositive.getAbsolutePath());
		if (PRINT_EXTRACTION) {
			System.out.println("\n" + "EXTRACTION, NEGATIVE" + "\n");
			StringBuilder stringBuilder = new StringBuilder();
			for (Query query : queriesNegative) {
				stringBuilder.append(query.toStringBuilder());
			}
			System.out.println(stringBuilder);
		}

		LinkedList<Query> queries = Lists.newLinkedList(queriesPositive);
		Converter.setPositive(queries);
		queries.addAll(queriesNegative);

		// Create ARFF

		List<String> allFeatures = Converter.getAllFeatures(queries);
		if (PRINT) {
			System.out.println("\n" + "FEATURES" + "\n");
			for (String feature : allFeatures) {
				System.out.println(feature);
			}
		}

		Instances instances = Converter.createArff(queries, allFeatures);
		if (PRINT_INSTANCES) {
			System.out.println("\n" + "INSTANCES" + "\n");
			System.out.println(instances);
			System.out.println(instances.size());
		}

		// Write

		Writer.write(instances, outFile);
	}

}