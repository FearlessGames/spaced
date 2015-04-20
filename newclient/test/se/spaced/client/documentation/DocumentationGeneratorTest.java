package se.spaced.client.documentation;

import org.junit.Ignore;
import org.junit.Test;
import se.spaced.client.launcher.DocumentationGenerator;

import java.io.StringWriter;
import java.net.MalformedURLException;

import static org.junit.Assert.assertTrue;

public class DocumentationGeneratorTest {

	@Test
	@Ignore
	public void testDocumentationGenerator() throws MalformedURLException {
		StringWriter writer = new StringWriter();
		new DocumentationGenerator().runGenerator(writer);
		assertTrue(writer.getBuffer().toString().length() > 100);
	}
}