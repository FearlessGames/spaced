package se.spaced.shared.world.area;

import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;

import java.util.Iterator;

public class Funnel {
	private Funnel() {
	}

	public static SpacedVector3 getNextWayPoint(SpacedVector3 currentPos, Iterable<Gate> gates) {
		VectorMath.InViewPoint standingInCurrentPosition = VectorMath.standingIn(currentPos);
		Iterator<Gate> gateIterator = gates.iterator();
		Gate firstGate = gateIterator.next();
		SpacedVector3 leftOfFunnel = standingInCurrentPosition.getLeftMost(firstGate.getPoint1(), firstGate.getPoint2());
		SpacedVector3 rightOfFunnel = getRight(firstGate, leftOfFunnel);
		for (Gate gate : gates) {
			SpacedVector3 leftGatePoint = standingInCurrentPosition.getLeftMost(gate.getPoint1(), gate.getPoint2());
			SpacedVector3 rightGatePoint = getRight(gate, leftGatePoint);
			if (insideFunnel(standingInCurrentPosition, leftOfFunnel, rightOfFunnel, leftGatePoint)) {
				leftOfFunnel = leftGatePoint;
			} else if (standingInCurrentPosition.is(rightOfFunnel).leftOf(leftGatePoint)) {
				return rightOfFunnel;
			}
			if (insideFunnel(standingInCurrentPosition, leftOfFunnel, rightOfFunnel, rightGatePoint)) {
				rightOfFunnel = rightGatePoint;
			} else if (standingInCurrentPosition.is(rightGatePoint).leftOf(leftOfFunnel)) {
				return leftOfFunnel;
			}
		}
		return leftOfFunnel;
	}

	private static SpacedVector3 getRight(Gate gate, SpacedVector3 leftGatePoint) {
		return gate.getPoint1().equals(leftGatePoint) ? gate.getPoint2() : gate.getPoint1();
	}


	static boolean insideFunnel(VectorMath.InViewPoint standingInCurrentPosition, SpacedVector3 leftEdge, SpacedVector3 rightEdge, SpacedVector3 point) {
		VectorMath.SpatialQuery thePointIs = standingInCurrentPosition.is(point);
		return thePointIs.leftOf(rightEdge) && !thePointIs.leftOf(leftEdge);
	}
}
