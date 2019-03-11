package org.dice_research.LsqSpinToArff;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Reads a LSQ output file in turtle/TTL format.
 * 
 * @author Adrian Wilke
 */
public abstract class Reader {

	public final static String SPARQL_QUERY = "SELECT ?q ?t ?f WHERE { "

			+ "?q a <http://lsq.aksw.org/vocab#Query> . "

			+ "?q <http://lsq.aksw.org/vocab#text> ?t . "

			+ "?q <http://lsq.aksw.org/vocab#hasStructuralFeatures> ?sf . "

			+ "?sf <http://lsq.aksw.org/vocab#usesFeature> ?f"

			+ "}";

	/**
	 * Extracts SPARQL query information (URI, query text, features).
	 * 
	 * @param ttlFileUrl URL of a turtle file
	 * 
	 * @return Query-objects
	 */
	public static Collection<Query> extract(String ttlFileUrl, List<String> featureWhitelist) {

		// Import file into model
		Model model = ModelFactory.createDefaultModel();
		model.read(ttlFileUrl, "TURTLE");

		// Execute query
		QueryExecution queryExecution = QueryExecutionFactory.create(SPARQL_QUERY, model);
		ResultSet resultSet = queryExecution.execSelect();

		// Extract results to map
		Query query;
		Map<String, Query> queries = new HashMap<String, Query>();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();

			// Only features in whilelist
			String feature = querySolution.get("f").toString();
			if (!featureWhitelist.contains(feature)) {
				continue;
			}

			// Get query in map
			String queryUri = querySolution.get("q").toString();
			if (queries.containsKey(queryUri)) {
				query = queries.get(queryUri);
			} else {
				query = new Query(queryUri);
				queries.put(queryUri, query);
			}

			query.text = querySolution.get("t").toString();
			query.features.add(feature);
		}

		return queries.values();
	}
}
