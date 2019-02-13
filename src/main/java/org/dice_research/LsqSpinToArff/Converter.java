package org.dice_research.LsqSpinToArff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * Reads a LSQ output turtle file, extracts feature information, and creates an
 * ARFF file.
 * 
 * @author Adrian Wilke
 */
public class Converter {

	public static void main(String[] args) throws IOException {

		if (args.length < 2) {
			System.err.println("Please provide a SPIN turtle file path.");
			System.err.println("And please provide an output file path.");
			System.exit(1);
		}

		Converter parser = new Converter();
		Map<String, Query> queries = parser.extract(args[0]);

		// Print it
		StringBuilder stringBuilder = new StringBuilder();
		for (Query query : queries.values()) {
			stringBuilder.append(query.toStringBuilder());
		}
		System.out.println(stringBuilder);

		// Print features
		List<String> features = parser.getAllFeatures(queries.values());
		for (String feature : features) {
			System.out.println(feature);
		}

		Instances instances = parser.createArff(queries.values(), features);
		System.out.println(instances);

		ArffSaver arffSaver = new ArffSaver();
		arffSaver.setInstances(instances);
		arffSaver.setFile(new File(args[1]));
		arffSaver.writeBatch();
	}

	/**
	 * Creates ARFF data.
	 * 
	 * @param queries  Queries
	 * @param features List of features
	 * @return ARFF data
	 */
	private Instances createArff(Collection<Query> queries, List<String> features) {

		// Create ARFF attributes
		Map<String, Attribute> featuresToAttributes = new HashMap<String, Attribute>(features.size());
		for (String feature : features) {
			featuresToAttributes.put(feature, new Attribute(feature));
		}
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(featuresToAttributes.values());
		Attribute classAttibute = new Attribute("queryUri", true);
		attributes.add(classAttibute);

		// Create list to return
		Instances instances = new Instances("Attributes", attributes, queries.size());
		instances.setClass(classAttibute);

		// Add data
		double[] doubleArray = new double[attributes.size()];
		for (Query query : queries) {
			Instance instance = new DenseInstance(attributes.size());
			instance.setValue(classAttibute, query.uri);
			for (String feature : query.features) {
				instance.setValue(featuresToAttributes.get(feature), 1);
			}

			instance.replaceMissingValues(doubleArray);

			instances.add(instance);

		}

		return instances;
	}

	/**
	 * Walks through all queries and looks for features.
	 * 
	 * @return Sorted list of all features.
	 */
	private List<String> getAllFeatures(Collection<Query> queries) {
		List<String> features = new LinkedList<String>();
		for (Query query : queries) {
			for (String feature : query.features) {
				if (!features.contains(feature)) {
					features.add(feature);
				}
			}
		}
		features.sort(null);
		return features;
	}

	/**
	 * Extracts SPARQL query information (URI, query text, features).
	 * 
	 * @param ttlFileUrl URL of a turtle file
	 * 
	 * @return Map queryUri to query-object
	 */
	private Map<String, Query> extract(String ttlFileUrl) {

		// Import file into model
		Model model = ModelFactory.createDefaultModel();
		model.read(ttlFileUrl, "TURTLE");

		// Execute query
		String sparqlQuery = "SELECT ?q ?t ?f WHERE { "

				+ "?q a <http://lsq.aksw.org/vocab#Query> . "

				+ "?q <http://lsq.aksw.org/vocab#text> ?t . "

				+ "?q <http://lsq.aksw.org/vocab#hasStructuralFeatures> ?sf . "

				+ "?sf <http://lsq.aksw.org/vocab#usesFeature> ?f}";
		QueryExecution queryExecution = QueryExecutionFactory.create(sparqlQuery, model);
		ResultSet resultSet = queryExecution.execSelect();

		// Extract results to map
		Query query;
		Map<String, Query> queries = new HashMap<String, Query>();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();

			// Get query in map
			String queryUri = querySolution.get("q").toString();
			if (queries.containsKey(queryUri)) {
				query = queries.get(queryUri);
			} else {
				query = new Query(queryUri);
				queries.put(queryUri, query);
			}

			query.text = querySolution.get("t").toString();
			query.features.add(querySolution.get("f").toString());
		}

		return queries;
	}
}
