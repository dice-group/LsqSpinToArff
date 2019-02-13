package org.dice_research.LsqSpinToArff;

import java.util.LinkedList;
import java.util.List;

/**
 * Container for SPARQL query metadata.
 * 
 * @author Adrian Wilke
 */
public class Query {

	public String uri;
	public String text;
	public List<String> features = new LinkedList<String>();

	public Query(String uri) {
		this.uri = uri;
	}

	public StringBuilder toStringBuilder() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(uri);
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(text);
		stringBuilder.append(System.lineSeparator());
		for (String feature : features) {
			stringBuilder.append(" ");
			stringBuilder.append(feature);
			stringBuilder.append(System.lineSeparator());
		}
		return stringBuilder;
	}
}