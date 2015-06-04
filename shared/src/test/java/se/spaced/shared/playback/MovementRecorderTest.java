package se.spaced.shared.playback;

import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class MovementRecorderTest {

	public static final SpacedRotation ROTATION = new SpacedRotation(0.2, 0.3, 0.4, 0.5, true);
	private static final double EPS = 1.1;

	@Test
	public void testSimpleMovement() {
		SpacedVector3 startPos = new SpacedVector3(0, 0, 0);
		SpacedRotation startRot = new SpacedRotation(0, 0, 1, 1);
		String startState = "start";
		int startTime = 0;


		PlaybackUpdater<String> updater = mock(PlaybackUpdater.class);
		final MovementPlayer movementPlayer = new MovementPlayer<String>(updater,
				new MovementPoint<String>(startTime, startState, startPos, startRot),
				0);


		PlaybackTransmitter<String> transmitter = new PlaybackTransmitter<String>() {
			@Override
			public void transmit(RecordingPoint<String> stringRecordingPoint) {
				movementPlayer.addData(stringRecordingPoint);
			}
		};

		MovementRecorder<String> movementRecorder = new MovementRecorder<String>(transmitter,
				startPos, startRot, startState, startTime);
		movementRecorder.add(210L, new SpacedVector3(0, 0, 10), ROTATION, "forward");
		movementRecorder.add(220L, new SpacedVector3(0, 0, 20), ROTATION, "forward");
		movementRecorder.add(230L, new SpacedVector3(0, 0, 30), ROTATION, "forward-right");
		movementRecorder.add(240L, new SpacedVector3(0, 0, 40), ROTATION, "forward");

		movementPlayer.step(100);
		verifyNever().on(updater).updateState(anyLong(), any(String.class),
				any(SpacedVector3.class),
				any(SpacedRotation.class));
		assertEquals("start", movementPlayer.state());

		movementPlayer.step(210);
		verifyExactly(1).on(updater).updateState(anyLong(), any(String.class),
				any(SpacedVector3.class),
				any(SpacedRotation.class));
		assertEquals("forward", movementPlayer.state());
		equalsVector(new SpacedVector3(0, 0, 10), movementPlayer.position(), EPS);

		movementPlayer.step(215);
		verifyExactly(1).on(updater).updateState(anyLong(), any(String.class),
				any(SpacedVector3.class),
				any(SpacedRotation.class));
		assertEquals("forward", movementPlayer.state());
		equalsVector(new SpacedVector3(0, 0, 15), movementPlayer.position(), EPS);

		movementPlayer.step(229);
		verifyExactly(1).on(updater).updateState(anyLong(), any(String.class),
				any(SpacedVector3.class),
				any(SpacedRotation.class));
		assertEquals("forward", movementPlayer.state());
		equalsVector(new SpacedVector3(0, 0, 30), movementPlayer.position(), EPS);

		movementPlayer.step(230);
		verifyExactly(2).on(updater).updateState(anyLong(), any(String.class),
				any(SpacedVector3.class),
				any(SpacedRotation.class));
		assertEquals("forward-right", movementPlayer.state());
		equalsVector(new SpacedVector3(0, 0, 31), movementPlayer.position(), EPS);

		movementPlayer.step(240);
		verifyExactly(3).on(updater).updateState(anyLong(), any(String.class),
				any(SpacedVector3.class),
				any(SpacedRotation.class));
		assertEquals("forward", movementPlayer.state());
		equalsVector(new SpacedVector3(0, 0, 40), movementPlayer.position(), EPS);
	}

	private void equalsVector(SpacedVector3 x, SpacedVector3 y, double eps) {
		double dist = x.distance(y);
		assertTrue(x + " was not equal to " + y + " (" + dist + ")", dist <= eps);
	}

	@Test
	public void testAppearanceAndTeleportWithManyEntities() {
	}

	private long t;

	@Test
	public void testNoDrift() {
		SpacedVector3 startPos = new SpacedVector3(0, 0, 0);
		SpacedRotation startRot = new SpacedRotation(0, 0, 1, 1);
		String startState = "start";
		t = 0;

		PlaybackUpdater<String> updater = mock(PlaybackUpdater.class);
		final MovementPlayer movementPlayer = new MovementPlayer<String>(updater,
				new MovementPoint<String>(t, startState, startPos, startRot),
				0);


		PlaybackTransmitter<String> transmitter = new PlaybackTransmitter<String>() {
			@Override
			public void transmit(RecordingPoint<String> stringRecordingPoint) {
				movementPlayer.addData(stringRecordingPoint);
				movementPlayer.step(movementPlayer.getEndTime() + stringRecordingPoint.delta);
			}
		};

		MovementRecorder<String> movementRecorder = new MovementRecorder<String>(transmitter,
				startPos, startRot, startState, t);

		Random r = new Random(1235);
		for (int i = 0; i < 1e4; i++) {
			startPos = startPos.add(new SpacedVector3(small(r), small(r), small(r)));
			movementRecorder.add(++t, startPos, ROTATION, "start");
		}
		movementRecorder.forceSend();

		double diff = startPos.distance(movementPlayer.position());

		r = new Random(1235);
		for (int i = 0; i < 1e4; i++) {
			startPos = startPos.add(new SpacedVector3(small(r), small(r), small(r)));
			movementRecorder.add(++t, startPos, ROTATION, "start");
		}
		movementRecorder.forceSend();

		double diff2 = startPos.distance(movementPlayer.position());

		assertEquals(diff, diff2, 0.01);
		equalsVector(startPos, movementPlayer.position(), 0.1);
	}

	private double small(Random r) {
		return (r.nextDouble() - 0.5) * 10;
	}


}
