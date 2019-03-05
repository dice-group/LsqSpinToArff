package org.dice_research.LsqSpinToArff.Weka;

import java.io.File;
import java.util.ArrayList;

import weka.classifiers.Classifier;
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
	public static final int CLASS_INDEX_POSITIVE = 1;

	private Instances data;
	private J48 j48tree;
	private Evaluation evaluation;

	/**
	 * Loads data at URL and converts numeric to nominal data. Sets data class name
	 * (the attribute to classify).
	 */
	public Weka createData(File arffFile) throws Exception {
		ArffLoader loader = new ArffLoader();
		loader.setFile(arffFile);
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
		return evaluation.fMeasure(CLASS_INDEX_POSITIVE);
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

	/**
	 * Gets the classifier / J48 tree.
	 */
	public Classifier getClassifier() throws Exception {
		evaluate();
		return j48tree;
	}

	/**
	 * Writes model to file.
	 */
	public void writeModel(File file) throws Exception {
		evaluate();
		weka.core.SerializationHelper.write(file.getPath(), j48tree);
	}

	/**
	 * Reads model from file.
	 */
	public void readModel(File file) throws Exception {
		j48tree = (J48) weka.core.SerializationHelper.read(file.getPath());
		evaluate();
	}

	private void evaluate() throws Exception {
		if (j48tree == null) {
			j48tree = new J48();
			j48tree.buildClassifier(data);
		}

		if (evaluation == null) {
			evaluation = new Evaluation(data);
			evaluation.evaluateModel(j48tree, data);
		}
	}
}