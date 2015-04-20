package se.fearless.bender;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class PivotalParseTest {

	@Before
	public void setUp() {

	}

	@Test
	public void shouldParseTaskCreate() throws Exception {
		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<activity>\n" +
				"  <id type=\"integer\">14270815</id>\n" +
				"  <version type=\"integer\">5</version>\n" +
				"  <event_type>story_create</event_type>\n" +
				"  <occurred_at type=\"datetime\">2010/03/08 15:43:27 UTC</occurred_at>\n" +
				"  <author>Christopher &#214;stlund</author>\n" +
				"  <project_id type=\"integer\">63645</project_id>\n" +
				"  <description>Christopher &#214;stlund added &quot;Testing story&quot;</description>\n" +
				"  <stories>\n" +
				"    <story>\n" +
				"      <id type=\"integer\">2717170</id>\n" +
				"      <url>http://www.pivotaltracker.com/services/v3/projects/63645/stories/2717170</url>\n" +
				"      <name>Testing story</name>\n" +
				"      <story_type>feature</story_type>\n" +
				"      <current_state>unscheduled</current_state>\n" +
				"    </story>\n" +
				"  </stories>\n" +
				"</activity>";

		Reader reader = new StringReader(body);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(reader);
		String eventType = doc.getRootElement().getChild("event_type").getValue();

		String name = doc.getRootElement().getChild("stories").getChild("story").getChild("name").getValue();

		assertEquals(eventType, "story_create");
		assertEquals(name, "Testing story");
	}
}
