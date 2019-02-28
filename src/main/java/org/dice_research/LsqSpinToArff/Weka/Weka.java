package org.dice_research.LsqSpinToArff.Weka;

import java.net.URL;

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

//		Evaluation.evaluateModel(tree, null);
		Evaluation evaluation = new Evaluation(dataNominal);

		// Problem: NaN ?!
		System.out.println(evaluation.fMeasure(0));
		System.out.println(evaluation.fMeasure(1));
	}

}
