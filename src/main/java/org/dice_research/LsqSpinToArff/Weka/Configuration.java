package org.dice_research.LsqSpinToArff.Weka;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	public static final String WEKA_DIRECTORY = "weka.directory";
	public static final String DEFAULT_WEKA_DIRECTORY = "/opt/weka/";

	private File propertiesFile = new File("src/main/resources/config.properties");
	private Properties properties = new Properties();

	public Configuration load() throws IOException {
		properties.load(new FileInputStream(propertiesFile));
		return this;
	}

	public Configuration write() throws FileNotFoundException, IOException {
		properties.store(new FileOutputStream(propertiesFile), null);
		return this;
	}

	public File getPropertiesFile() {
		return propertiesFile;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setDefaults() {
		properties.setProperty(WEKA_DIRECTORY, DEFAULT_WEKA_DIRECTORY);
	}

}