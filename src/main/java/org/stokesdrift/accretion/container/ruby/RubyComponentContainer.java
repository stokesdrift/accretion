package org.stokesdrift.accretion.container.ruby;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.builtin.IRubyObject;
import org.stokesdrift.accretion.config.ComponentConfig;
import org.stokesdrift.accretion.container.ComponentContainer;
import org.stokesdrift.accretion.util.PropertyUtil;
import org.stokesdrift.accretion.util.RubyUtil;

/**
 * Run a ruby script and keep it running in memory
 * 
 * @author driedtoast
 *
 */
public class RubyComponentContainer implements ComponentContainer {

	private static final String GEM_PATH = "GEM_PATH";
	private static final String JRUBY_HOME = "JRUBY_HOME";
	private static final Logger logger = Logger.getLogger(RubyComponentContainer.class.getName());

	private RubyInstanceConfig instanceConfig;
	private ComponentConfig componentConfig;
	
	private ThreadLocal<IRubyObject> rubyScript = new ThreadLocal<IRubyObject>();
	
	/** 
	 * Setup metadata and config
	 */
	public void initialize(ComponentConfig componentConfig) {
		this.componentConfig = componentConfig;
		if (instanceConfig == null) {
			createInstanceConfig();
		}
	}

	/**
	 * Entry point into running the component
	 */
	public void run() {
		Ruby runtime = fetchRuntime();
		try {
			rubyScript.set(runtime.executeScript(getScriptContent(), componentConfig.getSourceFile()));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ruby_component[status=error,source_file=" + componentConfig.getSourceFile() + "]", e);
		}
	}
	
	public Object component() {
		return rubyScript.get();
	}
	
	
	// Supporting methods below

	/**
	 * Get content of the script to run
	 * 
	 * @return script content 
	 * @throws IOException
	 */
	protected String getScriptContent() throws IOException {
		String scriptFile = new StringBuilder(componentConfig.getRootPath()).append(File.separator).append(componentConfig.getSourceFile()).toString();

		File file = new File(scriptFile);
		FileInputStream fis = new FileInputStream(file);
		String script = RubyUtil.inputStreamToString(fis);

		StringBuilder withHeader = new StringBuilder();
		withHeader.append("$:.unshift('").append(componentConfig.getRootPath()).append("')\n");
		withHeader.append(script);
		script = withHeader.toString();
		return script;
	}
	
	/**
	 * Creates instance config for the container
	 */
	protected void createInstanceConfig() {
		instanceConfig = new RubyInstanceConfig();
		initRuntimeConfig(instanceConfig);
	}

    /**
     * Create a runtime for the thread
     * @return ruby runtime
     * @throws RaiseException
     */
	public Ruby fetchRuntime() throws RaiseException {
		Ruby runtime = Ruby.getThreadLocalRuntime();
		if (runtime == null) {
			runtime = Ruby.newInstance(instanceConfig);
		}
		initRuntime(runtime);
		return runtime;
	}

	protected void initRuntime(Ruby runtime) {
		// TODO set global objects if needed
		// // set $servlet_context :
		// runtime.getGlobalVariables().set(
		// "$servlet_context", JavaUtil.convertJavaToRuby(runtime,
		// rackServletContext)
		// );
		// // load our (servlet) Rack handler :
		// runtime.evalScriptlet("require 'rack/handler/servlet'");
	}

	/**
	 * Sets up the runtime config for a ruby runtime 
	 * @param config
	 * @return ruby instance config
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected RubyInstanceConfig initRuntimeConfig(final RubyInstanceConfig config) {
		config.setLoader(Thread.currentThread().getContextClassLoader());
		config.setRunRubyInProcess(true);
		config.setDebug(true);

		// Set jruby home
		String home = PropertyUtil.getVariable(JRUBY_HOME);
		if (home != null) {
			config.setJRubyHome(home);
		} else {
			// derived from jruby rack home setting
			final URL resource = Ruby.class.getResource("/META-INF/jruby.home");
			if (resource != null && "jar".equals(resource.getProtocol())) {
				home = config.getJRubyHome(); // uri: protocol only since 9k :
				if (home == null || !home.startsWith("uri:classloader:")) {
					try {
						home = resource.toURI().getSchemeSpecificPart();
					} catch (URISyntaxException e) {
						home = resource.getPath();
					}

					final int last = home.length() - 1; // trailing '/' confuses
														// OSGi containers...
					if (home.charAt(last) == '/')
						home = home.substring(0, last);

					config.setJRubyHome(home);
				}
			}

		}

		// cleanup environment settings
		config.setNativeEnabled(false);

		Map env = new HashMap(config.getEnvironment());
		if (env.containsKey("PATH")) {
			env.put("PATH", "");
		}
		config.setEnvironment(env);

		setLoadPaths(config);
		return config;
	}

	/**
	 * Sets all the jar files as load paths
	 * 
	 * @param config
	 */
	protected void setLoadPaths(RubyInstanceConfig config) {
		List<String> pathDirs = componentConfig.getPaths();
		List<String> loadPaths = new ArrayList<String>();
		if (pathDirs != null) {
			loadPaths.addAll(pathDirs);
		}

		// Add root gems eg: /opt/jruby/lib/ruby/gems/shared/gems/
		String jrubyRootDir = PropertyUtil.getVariable(JRUBY_HOME);
		if (jrubyRootDir != null) {
			StringBuilder gemPath = new StringBuilder();
			gemPath.append(jrubyRootDir);
			String[] paths = new String[] { "lib", "jruby", "gems", "shared", "gems" };
			for (String path : paths) {
				gemPath.append(File.separator).append(path);
			}
			File gemPathDir = new File(gemPath.toString());
			if (gemPathDir.exists()) {
				File[] gemPaths = gemPathDir.listFiles();
				for (File path : gemPaths) {
					loadPaths.add(path.getAbsolutePath());
				}
			}
		}
		String gemPathEnv = PropertyUtil.getVariable(GEM_PATH);
		if (gemPathEnv != null) {
			loadPaths.add(gemPathEnv);
		}
		config.setLoadPaths(loadPaths);
	}

}
