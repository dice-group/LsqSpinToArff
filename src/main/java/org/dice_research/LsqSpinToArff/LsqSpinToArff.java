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

	private static final String LSQV_NAMESPACE = "http://lsq.aksw.org/vocab#";
	public static final String[] FEATURES_WHITELIST = new String[] { "Ask", "Construct", "Describe", "Filter", "Group",
			"GroupBy", "Limit", "Offset", "OrderBy", "Select", "TriplePath", "TriplePattern", "Union", "fnbound",
			"fnisLiteral", "fnregex" };
	private static final List<String> featureWhitelist;
	static {
		featureWhitelist = new LinkedList<>();
		for (String feature : FEATURES_WHITELIST) {
			featureWhitelist.add(LSQV_NAMESPACE + feature);
		}
	}

	public LsqSpinToArff run(File inFilePositive, File inFileNegative, File outFile) throws IOException {

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