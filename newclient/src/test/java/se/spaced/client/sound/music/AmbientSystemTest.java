package se.spaced.client.sound.music;

import org.junit.Before;
import org.junit.Test;
import se.mockachino.annotations.*;
import se.mockachino.order.*;
import se.spaced.client.sound.SoundSource;
import se.spaced.client.sound.SoundSourceFactory;

import static se.mockachino.Mockachino.*;

public class AmbientSystemTest {
	@Mock private SoundSourceFactory sourceFactory;
	@Mock private SoundSource soundSourceA;
	@Mock private SoundSource soundSourceB;
	private AmbientSystem ambientSystem;
	private final String oggA = "ambientA.ogg";
	private final String oggB = "ambientB.ogg";

	@Before
	public void setUp() throws Exception {
		setupMocks(this);
		ambientSystem = new AmbientSystem(sourceFactory);
	}

	@Test
	public void startsSoundSource() {
		stubReturn(soundSourceA).on(sourceFactory).newStreamingSoundSource(oggA);

		ambientSystem.playInChannel(oggA, SoundChannel.EVENT1);

		verifyOnce().on(soundSourceA).play();
	}

	@Test
	public void startsOnlyOneSourcePerChannel() {
		stubReturn(soundSourceA).on(sourceFactory).newStreamingSoundSource(oggA);
		stubReturn(soundSourceB).on(sourceFactory).newStreamingSoundSource(oggB);

		ambientSystem.playInChannel(oggA, SoundChannel.EVENT1);
		ambientSystem.playInChannel(oggB, SoundChannel.EVENT1);

		OrderingContext order = newOrdering();
		order.verify().on(soundSourceA).play();
		order.verify().on(soundSourceA).stop();
		order.verify().on(soundSourceB).play();
	}

	@Test
	public void ignoresAlreadyStartedSound() {
		stubReturn(soundSourceA).on(sourceFactory).newStreamingSoundSource(oggA);

		ambientSystem.playInChannel(oggA, SoundChannel.EVENT1);
		ambientSystem.playInChannel(oggA, SoundChannel.EVENT1);

		verifyOnce().on(soundSourceA).play();
	}

	@Test
	public void stopsPlayingStream() {
		stubReturn(soundSourceA).on(sourceFactory).newStreamingSoundSource(oggA);

		ambientSystem.playInChannel(oggA, SoundChannel.EVENT1);
		ambientSystem.stopChannel(SoundChannel.EVENT1);

		OrderingContext order = newOrdering();
		order.verify().on(soundSourceA).play();
		order.verify().on(soundSourceA).stop();
	}
}
