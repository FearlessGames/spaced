package se.spaced.shared.resources.zone;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.spaced.client.model.Prop;

import static org.junit.Assert.assertEquals;

public class ZoneTest {

	@Test
	public void testMarshallingOk() {
		Zone zone = new Zone("Dark Forest Of Utter Death", new Sphere(new SpacedVector3(100, 200, 300), 400));
		Prop p = new Prop("xmo1.xmo", new SpacedVector3(0, 1, 0), new SpacedVector3(0, 0, 0), SpacedRotation.IDENTITY);
		zone.addProp(p);
		XStream marshaller = new XStream(new DomDriver());
		String xml = marshaller.toXML(zone);
		Zone zone2 = (Zone) marshaller.fromXML(xml);
		System.out.println(xml);
		assertEquals(zone.getName(), zone2.getName());
	}

}
