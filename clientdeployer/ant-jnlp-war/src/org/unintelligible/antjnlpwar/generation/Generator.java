/*
 * Created on 18-Apr-2005
 */
package org.unintelligible.antjnlpwar.generation;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogSystem;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.unintelligible.antjnlpwar.task.BaseJnlpWar;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

/**
 * Generates a JNLP deployment descriptor
 *
 * @author ngc
 */
public class Generator {
	private VelocityEngine engine = new VelocityEngine();
	private BaseJnlpWar task;
	private Template template;
	private File outputFile;


	public Generator(BaseJnlpWar task, File outputFile, String templateName) {
		this.task = task;

		this.outputFile = outputFile;
		//initialise the resource loader to use the class loader
		Properties props = new Properties();
		props.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, "org.apache.velocity.runtime.log.NullLogSystem");
		props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class
				.getName());
		try {
			//initialise the Velocity engine
			engine.setProperty("runtime.log.logsystem", new NullLogSystem());
			engine.init(props);
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not initialise Velocity", e);
		}
		//set the template
		try {
			this.template = engine.getTemplate(templateName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new IllegalArgumentException(
					"Could not load the template file 'org/unintelligible/antjnlp/template/jnlp.vm'");
		}

	}

	public void generate() throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("task", task);
		FileWriter writer = new FileWriter(outputFile);
		try {
			//parse the template
			//StringWriter writer = new StringWriter();
			template.merge(context, writer);
			writer.flush();
		} catch (Exception e) {
			throw new Exception("Could not generate the template " + template.getName() + ": " + e.getMessage(), e);
		} finally {
			writer.close();
		}
	}

}
