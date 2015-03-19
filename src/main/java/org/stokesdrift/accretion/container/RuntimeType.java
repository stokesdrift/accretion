package org.stokesdrift.accretion.container;

import org.stokesdrift.accretion.container.ruby.RubyComponentContainer;

public enum RuntimeType {
	RUBY(RubyComponentContainer.class)
	;
	// JAVA,
	// CLOJURE;
	
	private Class<? extends ComponentContainer> runtimeClass;
	
	private RuntimeType(Class<? extends ComponentContainer> clazz) {
		this.runtimeClass = clazz;		
	}
	
	public Class<? extends ComponentContainer> runtimeClass() {
		return this.runtimeClass;
	}
}
