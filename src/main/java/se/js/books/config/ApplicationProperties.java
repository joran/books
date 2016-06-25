package se.js.books.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="app")
public class ApplicationProperties {
	private boolean testDataEnabled;
	private String eventsFile;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEventsFile() {
		return eventsFile;
	}

	public void setEventsFile(String eventsFile) {
		this.eventsFile = eventsFile;
	}

	public boolean isTestDataEnabled() {
		return testDataEnabled;
	}

	public void setTestDataEnabled(boolean testDataEnabled) {
		this.testDataEnabled = testDataEnabled;
	}
}
