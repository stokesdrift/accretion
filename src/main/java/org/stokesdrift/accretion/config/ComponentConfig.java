package org.stokesdrift.accretion.config;

import java.util.List;

public class ComponentConfig {

	private List<String> paths;
	private String rootPath;
	private String sourceFile;
	
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootDir) {
		this.rootPath = rootDir;
	}

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

}
