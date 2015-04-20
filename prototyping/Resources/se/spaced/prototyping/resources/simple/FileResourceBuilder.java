package se.spaced.prototyping.resources.simple;

import java.io.File;

public class FileResourceBuilder implements ResourceBuilder {
	private String basePath = System.getProperty("user.dir");
	private String resourcePath = "resource";

	@Override
	public Resource build(String key) {
		return new FileResource(new File(basePath + "/" + resourcePath + "/" + key));
	}
}
