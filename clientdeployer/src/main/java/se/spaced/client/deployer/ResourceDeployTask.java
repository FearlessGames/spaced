package se.spaced.client.deployer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.util.ArrayList;
import java.util.List;

public class ResourceDeployTask extends Task {
	private final List<Resource> resources = new ArrayList<Resource>();
	private String targetPath;

	@Override
	public void execute() throws BuildException {
		List<String> paths = new ArrayList<String>();
		for (Resource resource : resources) {
			paths.add(resource.getPath());
		}

		ResourceDeployer resourceDeployer = new ResourceDeployer(targetPath, paths, new AntLogger());
		try {
			resourceDeployer.indexAndCopyResources();

			log("Deploy successfull, copied " + resourceDeployer.getIndexedResources().size() + " resources");
		} catch (ResourceException e) {
			log("Failed to deploy resources");
			throw new BuildException(e);
		}
	}

	public void addResource(Resource resource) {
		resources.add(resource);
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public static class Resource {
		private String path;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}

	protected class AntLogger {
		public void log(String message) {
			ResourceDeployTask.this.log(message);
		}
	}
}
