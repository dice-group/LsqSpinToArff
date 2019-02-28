package org.dice_research.LsqSpinToArff.LSQ;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aksw.simba.lsq.cli.main.LsqCliParser;

/**
 * Runs LSQ.
 * 
 * Uses LSQ jar-with-dependencies, which can be specified with
 * {@link #setJarFile(String)} and {@link #setMainClass(String)}.
 * 
 * Options can be set with {@link #setOption(String, String)}. Keys are
 * specified as constants in this class.
 * 
 * Dev note: More LSQ options at {@link LsqCliParser#initOptionSpecs()}
 *
 * @author Adrian Wilke
 */
public class LsqRunner {

	public static final String DEFAULT_FORMAT = "sparql";
	public static final String DEFAULT_MODE = "q";

	public static final String KEY_FILE_INPUT = "file";
	public static final String KEY_FILE_OUTPUT = "output";
	public static final String KEY_FORMAT = "format";
	public static final String KEY_MODE = "rdfizer";

	/**
	 * Jar file default: Local maven repository in linux OS
	 */
	private String jarFile = System.getProperty("user.home")
			+ "/.m2/repository/org/aksw/simba/lsq/lsq-cli/1.0.0/lsq-cli-1.0.0-jar-with-dependencies.jar";

	private String mainClass = "org.aksw.simba.lsq.cli.main.MainLSQ";

	private Map<String, String> options = new HashMap<>();

	public String getJarFile() {
		return jarFile;
	}

	public String getMainClass() {
		return mainClass;
	}

	/**
	 * Sets LSQ Jar file with dependencies
	 */
	public LsqRunner setJarFile(String jarFile) {
		this.jarFile = jarFile;
		return this;
	}

	/**
	 * Sets main class in jar file.
	 */
	public LsqRunner setMainClass(String mainClass) {
		this.mainClass = mainClass;
		return this;
	}

	/**
	 * Sets an option.
	 */
	public LsqRunner setOption(String key, String value) {
		options.put(key, value);
		return this;
	}

	/**
	 * Runs LSQ.
	 */
	public LsqRunner run() throws IOException, InterruptedException {

		List<String> argumentsList = new LinkedList<>();
		argumentsList.add("java");
		argumentsList.add("-cp");
		argumentsList.add(jarFile);
		argumentsList.add(mainClass);
		argumentsList.addAll(getArguments());

		ProcessBuilder processBuilder = new ProcessBuilder(argumentsList);
		Process process = processBuilder.start();
		process.waitFor();

		return this;
	}

	/**
	 * Runs LSQ. Overwrites {@link #KEY_FILE_INPUT} and {@link #KEY_FILE_OUTPUT}.
	 */
	public LsqRunner run(String inputFile, String outputFile) throws IOException, InterruptedException {
		options.put(KEY_FILE_INPUT, inputFile);
		options.put(KEY_FILE_OUTPUT, outputFile);
		return run();
	}

	/**
	 * Transforms options to array. Overwrites default options with user options, if
	 * set.
	 */
	private List<String> getArguments() {

		// Set defaults
		Map<String, String> argumentsMap = new HashMap<>();
		argumentsMap.put(KEY_MODE, DEFAULT_MODE);
		argumentsMap.put(KEY_FORMAT, DEFAULT_FORMAT);

		// Set user config
		argumentsMap.putAll(options);

		// Map to list
		List<String> argumentsList = new LinkedList<>();
		for (String key : argumentsMap.keySet()) {
			argumentsList.add("--" + key);
			argumentsList.add(argumentsMap.get(key));
		}

		return argumentsList;
	}
}
