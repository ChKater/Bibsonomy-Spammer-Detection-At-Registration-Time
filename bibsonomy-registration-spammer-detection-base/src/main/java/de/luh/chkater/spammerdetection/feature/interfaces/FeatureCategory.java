package de.luh.chkater.spammerdetection.feature.interfaces;

/**
 * Category of an feature
 *
 * @author kater
 */
public enum FeatureCategory {

	POPULATION_BASED("Population-Based"), LOG("Log"), KEYBOARD("Keyboard"), LANGUAGE("Language"), ENVIRONMENT("Environment"), INTERACTION("Interaction"), ATTRIBUTE("Attribut");
	
	private final String name;
	
	private FeatureCategory(String name){
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
}
