package jkml.jms;

import java.util.Map;
import java.util.TreeMap;

public class JmsMessage {

	private Map<String, String> properties;

	private String body;

	JmsMessage(String body) {
		this.body = body;
		this.properties = new TreeMap<>();
	}

	JmsMessage(String body, Map<String, String> properties) {
		this.body = body;
		this.properties = properties;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
