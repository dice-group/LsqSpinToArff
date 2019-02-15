package org.dice_research.LsqSpinToArff;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * Writes ARFF file.
 * 
 * @author Adrian Wilke
 */
public abstract class Writer {

	/**
	 * Writes ARFF file.
	 * 
	 * @param instances Instances to write
	 * @param outFile   Output file
	 * @throws IOException
	 */
	public static void write(Instances instances, File outFile) throws IOException {
		ArffSaver arffSaver = new ArffSaver();
		arffSaver.setInstances(instances);
		arffSaver.setFile(outFile);
		arffSaver.writeBatch();
	}
}
