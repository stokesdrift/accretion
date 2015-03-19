package org.stokesdrift.accretion.container;

import org.stokesdrift.accretion.config.ComponentConfig;

/**
 * Represents an application definition for a given application that can be used within the overall 
 * application, platform, etc...
 * 
 * @author driedtoast
 *
 */
public interface ComponentContainer extends Runnable {

	void initialize(ComponentConfig config);
	
	/**
	 * Get running component
	 * 
	 * @return component object
	 */
	Object component();
}
