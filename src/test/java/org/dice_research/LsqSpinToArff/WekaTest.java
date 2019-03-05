package org.dice_research.LsqSpinToArff;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.dice_research.LsqSpinToArff.Weka.Weka;
import org.junit.Test;

public class WekaTest {

	@Test
	public void test() throws Exception {
		URL url = getClass().getClassLoader().getResource("dataset-numeric.arff");
		Weka weka = new Weka().createData(new File(url.toURI()));
		assertTrue(weka.getfMeasure() > 0.8);
		assertTrue(weka.getPredictions().size() == 20);
	}
}