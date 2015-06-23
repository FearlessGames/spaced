package se.spaced.client.resources.zone;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.fearlessgames.common.io.FileStreamLocator;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ZoneFileIOTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testMarshalling() throws IOException, XmlIOException {
		tempFolder.create();

		String key = "z1.currentZone";
		File zoneFile = tempFolder.newFile(key);

		Zone z = new Zone("Mighty one of deadly destruction", new Sphere(new SpacedVector3(100, 200, 300), 1000));
		FileStreamLocator fileStreamLocator = new FileStreamLocator(zoneFile);

		XmlIO io = new XStreamIO(new XStream(new DomDriver()), fileStreamLocator);

		io.save(z, "");  //since the zonefile is abosolut, use no key

		Zone z2 = io.load(Zone.class, "");

		assertEquals(z.getName(), z2.getName());
	}


}
