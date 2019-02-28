package org.dice_research.LsqSpinToArff.Weka;

import java.net.URL;
import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

/**
 * https://www.cs.waikato.ac.nz/ml/weka/documentation.html
 *
 * @author Adrian Wilke
 */
public class Weka {

	public static final String CLASS_ATTRIBUTE_NAME = "positive";

	private Instances data;
	private Evaluation evaluation;

	/**
	 * Loads data at URL and converts numeric to nominal data. Sets data class name
	 * (the attribute to classify).
	 */
	public Weka createData(String arffUrl) throws Exception {
		URL url = Weka.class.getClassLoader().getResource("dataset-numeric.arff");
		ArffLoader loader = new ArffLoader();
		loader.setURL(url.toString());
		data = loader.getDataSet();

		// weka.classifiers.trees.J48 cannot handle numeric class
		NumericToNominal filterNumNom = new NumericToNominal();
		filterNumNom.setInputFormat(data);
		data = Filter.useFilter(data, filterNumNom);

		data.setClass(data.attribute(CLASS_ATTRIBUTE_NAME));

		return this;
	}

	/**
	 * Gets fMeasure.
	 */
	public double getfMeasure() throws Exception {
		evaluate();

		// TODO: Set in a semantically beautiful way. And check.
		return evaluation.fMeasure(1);
	}

	/**
	 * Gets predictions for instances.
	 */
	public ArrayList<Prediction> getPredictions() throws Exception {
		evaluate();
		return evaluation.predictions();
	}

	/**
	 * Gets evaluation object.
	 */
	public Evaluation getEvaluation() throws Exception {
		evaluate();
		return evaluation;
	}

	private void evaluate() throws Exception {
		if (evaluation == null) {
			J48 tree = new J48();
			tree.buildClassifier(data);

			evaluation = new Evaluation(data);
			evaluation.evaluateModel(tree, data);
		}
	}
}