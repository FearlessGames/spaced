package se.spaced.server.mob.brains;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.WhisperMessage;
import se.spaced.shared.playback.RecordingPoint;

import static org.junit.Assert.assertFalse;
import static se.mockachino.Mockachino.*;

public class ProximityWhisperBrainTest extends ScenarioTestBase {
	private Mob talker;
	private Mob listener;
	private S2CProtocol listenerReceiver;
	private final String message = "Hi mr $NAME$, your quest is to kill Scott Michaels";
	private long whisperTimeout;

	@Before
	public void setup() {
		talker = new MobTemplate.Builder(uuidFactory.randomUUID(), "Talker").build().createMob(timeProvider,
				uuidFactory.randomUUID(), randomProvider);
		listener = new MobTemplate.Builder(uuidFactory.randomUUID(), "Listener").build().createMob(timeProvider,
				uuidFactory.randomUUID(), randomProvider);
		listenerReceiver = MockUtil.deepMock(S2CProtocol.class);

		entityService.addEntity(listener, listenerReceiver);
		whisperTimeout = 5 * 60 * 1000;
	}

	@Test
	public void testOnlySayOnce() {
		assertFalse(talker.getName().equals(listener.getName()));

		ProximityWhisperBrain brain = new ProximityWhisperBrain(talker,
				new WhisperMessage(message, 100, whisperTimeout),
				mobOrderExecutor,
				timeProvider);
		String actualMessage = message.replaceAll("\\$NAME\\$", listener.getName());

		brain.getSmrtReceiver().movement().sendPlayback(listener, mock(RecordingPoint.class));
		verifyOnce().on(listenerReceiver.chat()).whisperFrom(talker.getName(), actualMessage);

		brain.getSmrtReceiver().movement().sendPlayback(listener, mock(RecordingPoint.class));
		verifyOnce().on(listenerReceiver.chat()).whisperFrom(talker.getName(), actualMessage);

		timeProvider.advanceTime(whisperTimeout - 123);
		brain.getSmrtReceiver().movement().sendPlayback(listener, mock(RecordingPoint.class));
		verifyOnce().on(listenerReceiver.chat()).whisperFrom(talker.getName(), actualMessage);

		timeProvider.advanceTime(123);
		brain.getSmrtReceiver().movement().sendPlayback(listener, mock(RecordingPoint.class));
		verifyExactly(2).on(listenerReceiver.chat()).whisperFrom(talker.getName(), actualMessage);

	}


}
