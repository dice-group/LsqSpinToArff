package org.dice_research.LsqSpinToArff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Creates ARFF instances
 * 
 * @author Adrian Wilke
 */
public abstract class Converter {

	public final static String ARFF_RELATION_NAME = "Dataset";
	public final static String ARFF_ATTRIBUTE_QUERY_URI = "http://lsq.aksw.org/vocab#Query";
	public final static String ARFF_ATTRIBUTE_POSITIVE_SET = "positive";

	/**
	 * Class attribute/value is to be predicted,
	 * https://www.youtube.com/watch?v=BO6XJSaFYzk&feature=youtu.be&t=49
	 */
	public final static String ARFF_ATTRIBUTE_CLASS = ARFF_ATTRIBUTE_POSITIVE_SET;

	/**
	 * Adds feature {@link Converter#ARFF_ATTRIBUTE_POSITIVE_SET} to all
	 * query-objects.
	 * 
	 * @param queries Collection of query-objects
	 */
	public static void setPositiveFeature(Collection<Query> queries) {
		for (Query query : queries) {
			query.features.add(ARFF_ATTRIBUTE_POSITIVE_SET);
		}
	}

	/**
	 * Creates ARFF data.
	 * 
	 * @param queries     Queries
	 * @param allFeatures List of all features
	 * @return ARFF data
	 */
	public static Instances createArff(Collection<Query> queries, List<String> allFeatures) {

		// Map is used to assign features of query-objects to attribute-objects
		Map<String, Attribute> featuresToAttributes = new HashMap<String, Attribute>(allFeatures.size());
		for (String feature : allFeatures) {
			featuresToAttributes.put(feature, new Attribute(feature));
		}

		// Create list of all attribute-objects
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(featuresToAttributes.values());
		Attribute queryUriAttibute = new Attribute(ARFF_ATTRIBUTE_QUERY_URI, true);
		attributes.add(queryUriAttibute);

		// Create instances-object to return
		Instances instances = new Instances(ARFF_RELATION_NAME, attributes, queries.size());

		// Set attribute to predict
		instances.setClass(featuresToAttributes.get(ARFF_ATTRIBUTE_POSITIVE_SET));

		// Add data
		double[] doubleArray = new double[attributes.size()];
		for (Query query : queries) {
			Instance instance = new DenseInstance(attributes.size());
			instance.setValue(queryUriAttibute, query.uri);
			for (String feature : query.features) {
				instance.setValue(featuresToAttributes.get(feature), 1);
			}

			instance.replaceMissingValues(doubleArray);

			instances.add(instance);
		}

		return instances;
	}

	/**
	 * Walks through all queries and collects URIs of features.
	 * 
	 * @return Sorted list of all features.
	 */
	public static List<String> getAllFeatures(Collection<Query> queries) {
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

}