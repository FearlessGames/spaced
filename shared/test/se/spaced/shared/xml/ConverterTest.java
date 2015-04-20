package se.spaced.shared.xml;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConverterTest {
	private XStream xstream;

	@Before
	public void setup() {
		xstream = new XStream(new DomDriver());
		xstream.registerConverter(new QuaternionConverter());
		xstream.alias("rotation", SpacedRotation.class);
		xstream.registerConverter(new Vector3Converter());
		xstream.alias("vector", SpacedVector3.class);
		xstream.registerConverter(new ColorRGBAConverter());
		xstream.alias("color", ColorRGBA.class);
	}

	@Test
	public void testQuaternion() {
		SpacedRotation quaternion = new SpacedRotation(1.0, 2.0, 3.0, 4.0);
		String xml = xstream.toXML(quaternion);
		assertEquals("<rotation x=\"1.0\" y=\"2.0\" z=\"3.0\" w=\"4.0\"/>", xml);

		Object quat2 = xstream.fromXML(xml);
		assertEquals(quaternion, quat2);
	}

	@Test
	public void testVector3() {
		SpacedVector3 vector = new SpacedVector3(1.0, 2.0, 3.0);
		String xml = xstream.toXML(vector);
		assertEquals("<vector x=\"1.0\" y=\"2.0\" z=\"3.0\"/>", xml);

		Object vector2 = xstream.fromXML(xml);
		assertEquals(vector, vector2);
	}

	@Test
	public void convertsColorRGBA() {
		ColorRGBA color = new ColorRGBA(1, 1, 1, 1);
		String xml = xstream.toXML(color);
		assertEquals("<color r=\"1.0\" g=\"1.0\" b=\"1.0\" a=\"1.0\"/>", xml);

		Object color2 = xstream.fromXML(xml);
		assertEquals(color, color2);
	}

	@Test
	public void convertOldArdorVectorToSpacedVector() {
		ArdorVec ardorVec = new ArdorVec(new Vector3(1, 2, 3));

		XStream ardorXStream = new XStream(new DomDriver());
		String xml = ardorXStream.toXML(ardorVec);
		xml = xml.replaceAll("ArdorVec", "SpacedVec");

		XStream apacheXStream = new XStream(new DomDriver());
		apacheXStream.registerConverter(new Vector3Converter());

		SpacedVec spacedVec = (SpacedVec) apacheXStream.fromXML(xml);
		assertNotNull(spacedVec);
		assertEquals(ardorVec.vector.getX(), spacedVec.vector.getX(), 2);
		assertEquals(ardorVec.vector.getY(), spacedVec.vector.getY(), 2);
		assertEquals(ardorVec.vector.getZ(), spacedVec.vector.getZ(), 2);
	}

	@Test
	public void convertOldArdorQuatToSpacedRot() {
		ArdorQuat ardorQuat = new ArdorQuat(new Quaternion(1, 2, 3, 4));

		XStream ardorXStream = new XStream(new DomDriver());
		String xml = ardorXStream.toXML(ardorQuat);
		xml = xml.replaceAll("ArdorQuat", "SpacedRot");

		XStream apacheXStream = new XStream(new DomDriver());
		apacheXStream.registerConverter(new QuaternionConverter());

		SpacedRot spacedRot = (SpacedRot) apacheXStream.fromXML(xml);
		assertNotNull(spacedRot);
		assertEquals(ardorQuat.rot.getX(), spacedRot.rot.getX(), 2);
		assertEquals(ardorQuat.rot.getY(), spacedRot.rot.getY(), 2);
		assertEquals(ardorQuat.rot.getZ(), spacedRot.rot.getZ(), 2);
		assertEquals(ardorQuat.rot.getW(), spacedRot.rot.getW(), 2);
	}

	public static class ArdorVec {
		private Vector3 vector;

		public ArdorVec(Vector3 vector) {
			this.vector = vector;
		}
	}

	public static class SpacedVec {
		private SpacedVector3 vector;

		public SpacedVec(SpacedVector3 vector) {
			this.vector = vector;
		}
	}

	public static class ArdorQuat {
		private Quaternion rot;

		public ArdorQuat(Quaternion rot) {
			this.rot = rot;
		}
	}

	public static class SpacedRot {
		private SpacedRotation rot;

		public SpacedRot(SpacedRotation rot) {
			this.rot = rot;
		}
	}
}
