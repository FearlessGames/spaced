package se.ardortech.math;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArdorSpacedRotationTest {
	private static final double EPSILON = 1e-10;

	@Test
	public void testRotationOfUnitVectorX() {
		Vector3 ardor = new Vector3(1, 0, 0);
		SpacedVector3 spacedVector = new SpacedVector3(1, 0, 0);

		assertSameVectors(ardor, spacedVector);

		Quaternion ardorRot = new Quaternion(0,0,0.70710678118654752440084436210485,0.70710678118654752440084436210485);

		SpacedRotation spacedRot = new SpacedRotation(0,0,0.70710678118654752440084436210485,0.70710678118654752440084436210485);
		assertSameRotations(ardorRot, spacedRot);

		Vector3 ardorResult = new Vector3();
		ardorRot.apply(ardor, ardorResult);

		SpacedVector3 spacedResult = spacedRot.applyTo(spacedVector);
		assertSameVectors(ardorResult, spacedResult);

		assertEquals(0.0, spacedResult.getX(), EPSILON);
		assertEquals(1.0, spacedResult.getY(), EPSILON);
		assertEquals(0.0, spacedResult.getZ(), EPSILON);
	}



	@Test
	public void testRotationOfVector() {
		Vector3 ardor = new Vector3(30, 40, 50);
		SpacedVector3 spacedVector = new SpacedVector3(30, 40, 50);

		assertSameVectors(ardor, spacedVector);

		Quaternion ardorRot = new Quaternion(3,4,5,1);
		ardorRot.normalizeLocal();

		SpacedRotation spacedRot = new SpacedRotation(3, 4, 5, 1, true);
		assertSameRotations(ardorRot, spacedRot);

		Vector3 ardorResult = new Vector3();
		ardorRot.apply(ardor, ardorResult);

		SpacedVector3 spacedResult = spacedRot.applyTo(spacedVector);
		assertSameVectors(ardorResult, spacedResult);
	}

	@Test
	public void testRotationOfVector2() {
		Vector3 ardor = new Vector3(0, 0, 9);
		SpacedVector3 spacedVector = new SpacedVector3(0, 0, 9);

		assertSameVectors(ardor, spacedVector);

		Quaternion ardorRot = new Quaternion(0.0, 0.19866933079506122, 0.0, 0.9800665778412416);

		SpacedRotation spacedRot = new SpacedRotation(0.0, 0.19866933079506122, 0.0, 0.9800665778412416);
		assertSameRotations(ardorRot, spacedRot);

		Vector3 ardorResult = new Vector3();
		ardorRot.apply(ardor, ardorResult);

		SpacedVector3 spacedResult = spacedRot.applyTo(spacedVector);
		assertSameVectors(ardorResult, spacedResult);
	}





	@Test
	public void testChainingRotations() {
		Vector3 ardor = new Vector3(30, 40, 50);
		SpacedVector3 spacedVector = new SpacedVector3(30, 40, 50);

		assertSameVectors(ardor, spacedVector);

		Quaternion ardorRot1 = new Quaternion(3,4,5,1);
		ardorRot1.normalizeLocal();

		Quaternion ardorRot2 = new Quaternion(25,7,6,8);
		ardorRot2.normalizeLocal();

		Quaternion ardorChained = ardorRot1.multiply(ardorRot2, null);

		SpacedRotation spacedRot1 = new SpacedRotation(3, 4, 5, 1, true);
		SpacedRotation spacedRot2 = new SpacedRotation(25, 7, 6, 8, true);

		SpacedRotation spacedChained = spacedRot1.applyTo(spacedRot2);

		assertSameRotations(ardorChained, spacedChained);
	}

	@Test
	public void testChainingInverse() {
		Quaternion ardorRot1 = new Quaternion(3,4,5,1);
		ardorRot1.normalizeLocal();

		Quaternion ardorRot2 = ardorRot1.invert(null);

		Quaternion ardorChained = ardorRot1.multiply(ardorRot2, null);

		SpacedRotation spacedRot1 = new SpacedRotation(3, 4, 5, 1, true);


		SpacedRotation spacedChained = spacedRot1.applyInverseTo(spacedRot1.applyTo(SpacedRotation.IDENTITY));

		assertSameRotations(ardorChained, spacedChained);
	}


	@Test
	public void fromEuler() {
		Quaternion ardorRot = new Quaternion();

		ardorRot.fromEulerAngles(radFromDeg(50),radFromDeg(30),radFromDeg(20));
		assertEquals(0.259, ardorRot.getX(), 1e-3);

		SpacedRotation spacedRotation = Rotations.fromEulerAngles(radFromDeg(50),radFromDeg(30),radFromDeg(20));
		assertEquals(0.259, spacedRotation.getX(), 1e-3);

//		SpacedRotation spacedRotation2 = new SpacedRotation(SpacedRotationOrder.ZYX, radFromDeg(50),radFromDeg(30),radFromDeg(20));
//		for (SpacedRotationOrder spacedRotationOrder : SpacedRotationOrder.values()) {
//			SpacedRotation spacedRot = new SpacedRotation(spacedRotationOrder, radFromDeg(50),radFromDeg(30),radFromDeg(20));
//			System.out.println(spacedRotationOrder + ": " + spacedRot);
//		}
//		assertSameRotations(ardorRot, spacedRotation2);

		assertSameRotations(ardorRot, spacedRotation);
	}

	@Test
	public void lookAt() {
		Quaternion ardorRot = new Quaternion();

		ardorRot.lookAt(new Vector3(30.0, 20.0, 10.0), Vector3.UNIT_Y);
		SpacedRotation spacedRotation = VectorMath.lookAt(new SpacedVector3(30.0, 20.0, 10.0), SpacedVector3.PLUS_J);

		assertSameRotations(ardorRot, spacedRotation);
	}




	private double radFromDeg(int deg) {
		return deg* Math.PI/180;
	}

	private void assertSameRotations(Quaternion ardorRot, SpacedRotation spacedRot) {
		assertEquals("x", ardorRot.getX(), spacedRot.getX(), EPSILON);
		assertEquals("y", ardorRot.getY(), spacedRot.getY(), EPSILON);
		assertEquals("z", ardorRot.getZ(), spacedRot.getZ(), EPSILON);
		assertEquals("w", ardorRot.getW(), spacedRot.getW(), EPSILON);

		Matrix3 matrix3 = ardorRot.toRotationMatrix((Matrix3) null);

		double[][] matrix = spacedRot.getMatrix();
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[row].length; column++) {
				assertEquals("Failed @ " + row + ", " + column,matrix3.getValue(row, column), matrix[column][row], EPSILON);
			}
		}
	}

	private void assertSameVectors(Vector3 ardor, SpacedVector3 spacedVector) {
		assertEquals(ardor.getX(), spacedVector.getX(), EPSILON);
		assertEquals(ardor.getY(), spacedVector.getY(), EPSILON);
		assertEquals(ardor.getZ(), spacedVector.getZ(), EPSILON);
	}
}