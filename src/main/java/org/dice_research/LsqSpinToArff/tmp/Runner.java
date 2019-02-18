package org.dice_research.LsqSpinToArff.tmp;

import java.io.File;
import java.io.IOException;

public class Runner {

	public static void main(String[] args) throws IOException, InterruptedException {

		Configuration configuration = new Configuration().load();

		File wekaJar = new File(configuration.getProperties().get(Configuration.WEKA_DIRECTORY).toString(), "weka.jar");

		String command = "java -jar " + wekaJar.getAbsolutePath() + " -t /tmp/lsqtesttmp/dataset-noUris-nominal.arff "
				+ "-W weka.classifiers.trees.J48";
		System.out.println(command);
		Process process = Runtime.getRuntime().exec(command);
		process.waitFor();

		new String(process.getOutputStream().toString());

//		String result = new BufferedReader(new InputStreamReader(inputStream))
//				  .lines().collect(Collectors.joining("\n"));
//		String result = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))
//				  .lines().collect(Collectors.joining("\n"));

//		InputStream in = process.getInputStream();
//		InputStream err = process.getErrorStream();
//		process.getOutputStream()

	}

}
