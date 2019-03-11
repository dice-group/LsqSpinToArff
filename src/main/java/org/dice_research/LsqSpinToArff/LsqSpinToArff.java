package org.dice_research.LsqSpinToArff;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;

import weka.core.Instances;

public class LsqSpinToArff {

	private Collection<Query> queriesPositive;
	private Collection<Query> queriesNegative;
	private List<String> allFeatures;

	public LsqSpinToArff run(File inFilePositive, File inFileNegative, File outFile, List<String> featureWhitelist)
			throws IOException {

		// Extract data
		queriesPositive = Reader.extract(inFilePositive.getAbsolutePath(), featureWhitelist);
		queriesNegative = Reader.extract(inFileNegative.getAbsolutePath(), featureWhitelist);

		// Combine all queries. Mark positive queries.
		LinkedList<Query> queries = Lists.newLinkedList(queriesPositive);
		Converter.setPositiveFeature(queries);
		queries.addAll(queriesNegative);

		// Get all features to create matrix/table. Create data instances.
		allFeatures = Converter.getAllFeatures(queries);
		Instances instances = Converter.createArff(queries, allFeatures);

		// Write results
		Writer.write(instances, outFile);

		return this;
	}

	public Collection<Query> getQueriesPositive() {
		return queriesPositive;
	}

	public Collection<Query> getQueriesNegative() {
		return queriesNegative;
	}

	public List<String> getAllFeatures() {
		return allFeatures;
	}

}