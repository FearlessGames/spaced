package se.ardorgui.components.area;

import org.junit.Test;

import java.awt.Point;

import static org.junit.Assert.assertEquals;

public class TestAnchorPoint {
	@Test
	public void testGetAnchorCoordinates() {
		final MockJmeFrame frame = new MockJmeFrame(0,0,100, 100);
		Point anchorCoordinates = AnchorPoint.getAnchorPointOffsetFromLowerLeft(frame.getDimension(), AnchorPoint.TOPLEFT);
		assertEquals("Bad anchor point", new Point(0,100), anchorCoordinates);

		anchorCoordinates = AnchorPoint.getAnchorPointOffsetFromLowerLeft(frame.getDimension(), AnchorPoint.MIDCENTER);
		assertEquals("Bad anchor point", new Point(50,50), anchorCoordinates);
	}
}