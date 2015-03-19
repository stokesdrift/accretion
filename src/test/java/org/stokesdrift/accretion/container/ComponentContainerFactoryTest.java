package org.stokesdrift.accretion.container;

import org.junit.Assert;
import org.junit.Test;
import org.stokesdrift.accretion.container.ruby.RubyComponentContainer;

public class ComponentContainerFactoryTest {

	@Test
	public void testRubyClassRetrieval() throws Exception {
		ComponentContainer container = ComponentContainerFactory.createContainer(RuntimeType.RUBY);
		Assert.assertNotNull(container);
		Assert.assertEquals(RubyComponentContainer.class, container.getClass());
	}
	
}
