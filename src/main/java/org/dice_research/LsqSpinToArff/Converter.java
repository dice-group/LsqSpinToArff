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

	/**
	 * Creates ARFF data.
	 * 
	 * @param queries  Queries
	 * @param features List of features
	 * @return ARFF data
	 */
	public static Instances createArff(Collection<Query> queries, List<String> features) {

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