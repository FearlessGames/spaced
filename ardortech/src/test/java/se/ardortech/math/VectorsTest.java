package se.ardortech.math;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VectorsTest {
	private static final double EPSILON = 1e-10;

	@Test
	public void testFromArdor() throws Exception {
		ReadOnlyVector3 ardor = new Vector3(10.0, 20.0, 30.0);
		SpacedVector3 apache = Vectors.fromArdor(ardor);

		assertEquals(ardor.getX(), apache.getX(), EPSILON);
		assertEquals(ardor.getY(), apache.getY(), EPSILON);
		assertEquals(ardor.getZ(), apache.getZ(), EPSILON);
	}

	@Test
	public void testFromApache() throws Exception {
		SpacedVector3 apache = new SpacedVector3(31.0, 21.0, 11.0);
		ReadOnlyVector3 ardor = Vectors.fromSpaced(apache);

		assertEquals(apache.getX(), ardor.getX(), EPSILON);
		assertEquals(apache.getY(), ardor.getY(), EPSILON);
		assertEquals(apache.getZ(), ardor.getZ(), EPSILON);
	}
}
