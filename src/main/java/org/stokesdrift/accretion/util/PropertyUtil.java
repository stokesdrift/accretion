package org.stokesdrift.accretion.util;

public class PropertyUtil {
	
	/**
	 * Gets variable from system property or the environment based on a name
	 * 
	 * @param name
	 * @return value
	 */
	public static String getVariable(String name) {
		String envValue = System.getProperty(name);
		if (envValue == null) {
			envValue = System.getenv(name);
		}
		return envValue;
	}
}
