package org.stokesdrift.accretion.container;

/**
 * Creates and application container based on a runtime type.  
 * 
 * @author driedtoast
 *
 */
public class ComponentContainerFactory {

	public static ComponentContainer createContainer(RuntimeType type) throws ReflectiveOperationException {
		Class<? extends ComponentContainer> clazz = type.runtimeClass();
		return clazz.newInstance();
	}
	
}
