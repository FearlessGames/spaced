package se.spaced.shared.playback;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.MockTimeProvider;
import se.spaced.shared.model.AnimationState;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class BufferedMovementPlayerTest {

	private PlaybackUpdater<AnimationState> updater;
	private MockTimeProvider timeProvider;

	@Before
	public void setUp() throws Exception {
		updater = mock(PlaybackUpdater.class);
		timeProvider = new MockTimeProvider();
	}

	@Test
	public void createAndNoUpdate() throws Exception {
		timeProvider.setNow(1000);
		SpacedVector3 pos = new SpacedVector3(100, 200, 300);
		SpacedRotation rot = new SpacedRotation(1, 0, 0, 0, false);
		MovementPoint<AnimationState> first = new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, pos, rot);
		BufferedMovementPlayer<AnimationState> player = new BufferedMovementPlayer<AnimationState>(updater, first);
		assertEquals(pos, player.position());
		assertEquals(rot, player.rotation());
		assertEquals(AnimationState.IDLE, player.state());

		timeProvider.advanceTime(25);
		player.step(timeProvider.now());

		assertEquals(pos, player.position());
		assertEquals(rot, player.rotation());
		assertEquals(AnimationState.IDLE, player.state());

		timeProvider.advanceTime(25);
		player.step(timeProvider.now());

		assertEquals(pos, player.position());
		assertEquals(rot, player.rotation());
		assertEquals(AnimationState.IDLE, player.state());
	}
}
