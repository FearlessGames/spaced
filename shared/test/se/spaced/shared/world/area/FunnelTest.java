package se.spaced.shared.world.area;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FunnelTest {

	private Gate gate1;
	private Gate gate2;
	private Gate gate3;
	private List<Gate> gates;

	@Before
	public void setUp() throws Exception {
		gate1 = new Gate(new SpacedVector3(10, 0, 10), new SpacedVector3(11, 0 , 6));
		gate2 = new Gate(new SpacedVector3(12, 0, 11), new SpacedVector3(11, 0 , 6));
		gate3 = new Gate(new SpacedVector3(18, 0, 11), new SpacedVector3(12, 0, 11));

		gates = Lists.newArrayList(gate1, gate2, gate3);
	}

	@Test
	public void simpleFunnel() throws Exception {
		SpacedVector3 targetPoint = new SpacedVector3(14, 0, 4);
		gates.remove(gate3);
		gates.add(new Gate(targetPoint, targetPoint));

		SpacedVector3 currentPos = new SpacedVector3(8, 0, 6);
		SpacedVector3 nextWayPoint = Funnel.getNextWayPoint(currentPos, gates);

		assertEquals(new SpacedVector3(11, 0, 6), nextWayPoint);
	}

	@Test
	public void twoStepFunnel() throws Exception {
		SpacedVector3 targetPoint = new SpacedVector3(10, 0, 16);
		gates.add(new Gate(targetPoint, targetPoint));

		SpacedVector3 currentPos = new SpacedVector3(8, 0, 6);
		SpacedVector3 nextWayPoint = Funnel.getNextWayPoint(currentPos, gates);

		assertEquals(new SpacedVector3(12, 0, 11), nextWayPoint);
	}
}
