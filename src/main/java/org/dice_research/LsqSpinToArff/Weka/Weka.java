package org.dice_research.LsqSpinToArff.Weka;

import java.net.URL;
import java.util.Arrays;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
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

	public static void main(String[] args) throws Exception {

		URL url;
		ArffLoader loader;

		// Problem: Strings (URIs) inside
//		url = Weeeka.class.getClassLoader().getResource("dataset.arff");
//		loader = new ArffLoader();
//		loader.setURL(url.toString());
//		Instances data = loader.getDataSet();
//		System.out.println(data.size());

		// Problem: J48Tree can not handle numeric data
		url = Weka.class.getClassLoader().getResource("dataset-numeric.arff");
		loader = new ArffLoader();
		loader.setURL(url.toString());
		Instances dataNumeric = loader.getDataSet();
		System.out.println(dataNumeric.size());

		NumericToNominal filterNumNom = new NumericToNominal();
		filterNumNom.setInputFormat(dataNumeric);
		Instances dataNominal = Filter.useFilter(dataNumeric, filterNumNom);

//		NumericToBinary filterNumBin = new NumericToBinary();
//		filterNumBin.setInputFormat(dataNumeric);
//		Instances dataBinary = Filter.useFilter(dataNumeric, filterNumBin);

		J48 tree = new J48();
		dataNominal.setClass(dataNominal.attribute("positive"));
		tree.buildClassifier(dataNominal);

		for (Instance instance : dataNominal) {
			System.out.println(tree.classifyInstance(instance));
		}

		// Problem: NaN ?!
		// How can an evaluation be done without the tree?!
		// http://weka.sourceforge.net/doc.stable-3-8/index.html?weka/classifiers/evaluation/Evaluation.html
		Evaluation evaluation = new Evaluation(dataNominal);
		System.out.println(evaluation.fMeasure(0));
		System.out.println(evaluation.fMeasure(1));
		System.out.println(evaluation.precision(0));
		System.out.println(evaluation.precision(1));
		System.out.println(evaluation.recall(0));
		System.out.println(evaluation.recall(1));
		System.out.println(dataNominal.numClasses());

		// No ...
//		dataNumeric.setClass(dataNumeric.attribute("positive"));
//		Evaluation evaluationNumeric = new Evaluation(dataNumeric);
//		System.out.println(evaluationNumeric.fMeasure(0));
//		System.out.println(evaluationNumeric.fMeasure(1));

		// Yeah, already known ...
		System.out.println();
		double[] predictions = evaluation.evaluateModel(tree, dataNominal);
		for (double d : predictions) {
			System.out.println(d);
		}

		// That works
		System.out.println();
		System.out.println(evaluation.numTruePositives(0));
		System.out.println(evaluation.numTrueNegatives(0));
		System.out.println(evaluation.numFalsePositives(0));
		System.out.println(evaluation.numFalseNegatives(0));
		System.out.println(evaluation.numTruePositives(1));
		System.out.println(evaluation.numTrueNegatives(1));
		System.out.println(evaluation.numFalsePositives(1));
		System.out.println(evaluation.numFalseNegatives(1));

		// Aaaah! The model has to be evaluated in own method first.
		System.out.println(evaluation.fMeasure(0));
		System.out.println(evaluation.fMeasure(1));
		
		// Data scientist is happy.
	}

}
