package org.stokesdrift.accretion.container.ruby;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.stokesdrift.accretion.config.ComponentConfig;

public class RubyComponentContainerTest {

	
	@Test
	public void testSimpleScript() throws Exception
	{
		String rubyTestFile = "test_paths.rb";
		
		URL rubyFile = this.getClass().getClassLoader().getResource("examples/" + rubyTestFile);
		File file = new File(rubyFile.toURI());
		String directoryName = file.getParent();
		
		ComponentConfig config = new ComponentConfig();
		config.setRootPath(directoryName);
		config.setSourceFile(rubyTestFile);
		
		RubyComponentContainer container = new RubyComponentContainer();
		container.initialize(config);
		container.run();
		
		Assert.assertNotNull(container.component());
		Assert.assertEquals("hello world", container.component().toString());
	}
}
