package se.spaced.server.persistence.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.stats.ModStat;
import se.spaced.shared.model.stats.SpacedStatType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatTypeConverterTest {
	XStream xStream;

	@Before
	public void setUp() {
		xStream = new XStream(new DomDriver());
		xStream.setMode(XStream.NO_REFERENCES);

		xStream.alias("modStat", ModStat.class);

		xStream.alias("statType", SpacedStatType.class);
		xStream.registerConverter(new StatTypeConverter());
	}

	@Test
	public void unmarshalSpeed() throws Exception {
		SpacedStatType speed = (SpacedStatType) xStream.fromXML("<statType>SPEED</statType>");
		assertEquals(SpacedStatType.SPEED, speed);
	}

	@Test
	public void unmarshalModStat() throws Exception {
		ModStat modStat = (ModStat) xStream.fromXML("<modStat>\n" +
				"\t\t\t\t\t\t\t<amount>0.0</amount>\n" +
				"\t\t\t\t\t\t\t<statType>SPEED</statType>\n" +
				"\t\t\t\t\t\t\t<operator>POST_MULTIPLY</operator>\n" +
				"\t\t\t\t\t\t</modStat>");

		assertNotNull(modStat);
		assertEquals(SpacedStatType.SPEED, modStat.getStatType());
	}
}