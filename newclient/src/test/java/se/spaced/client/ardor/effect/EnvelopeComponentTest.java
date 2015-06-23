package se.spaced.client.ardor.effect;

import com.ardor3d.scenegraph.Node;
import org.junit.Before;
import org.junit.Test;

import static se.mockachino.Mockachino.*;


public class EnvelopeComponentTest {
	private AbstractEnvelopeComponent<ParticleEffect> envelopeComponent;
	private EffectComponent<ParticleEffect> childEffectComponent;
	private ParticleEffect particleEffect;
	private Node attachmentNode;

	@Before
	public void setUp() {
		childEffectComponent = mock(EffectComponent.class);
		particleEffect = mock(ParticleEffect.class);
		attachmentNode = mock(Node.class);

		envelopeComponent = new ParticleEnvelopeComponent.Builder(attachmentNode)
				.delayTime(10).playtime(10).releaseTime(10).childComponent(childEffectComponent).build();
	}

	@Test
	public void shouldRemainStarting() {
		envelopeComponent.onUpdate(5, particleEffect);

		verifyNever().on(childEffectComponent).onUpdate(5, particleEffect);
		verifyNever().on(particleEffect).setReleaseRate(0);
		verifyNever().on(childEffectComponent).onStop(particleEffect);
	}

	@Test
	public void shouldEnterPlaying() {
		envelopeComponent.onUpdate(10, particleEffect);
		verifyOnce().on(childEffectComponent).onStart(particleEffect);

		envelopeComponent.onUpdate(1, particleEffect);
		// Inside playing state onUpdate is run
		verifyOnce().on(childEffectComponent).onUpdate(1, particleEffect);

		verifyNever().on(particleEffect).setReleaseRate(0);
	}

	@Test
	public void shouldEnterReleasing() {
		envelopeComponent.onStart(particleEffect);
		envelopeComponent.onUpdate(10, particleEffect);
		envelopeComponent.onUpdate(10, particleEffect);

		verifyOnce().on(particleEffect).setReleaseRate(0);

		getData(childEffectComponent).resetCalls();
		getData(particleEffect).resetCalls();

		envelopeComponent.onUpdate(1, particleEffect);
		verifyNever().on(childEffectComponent).onStart(particleEffect);
		verifyNever().on(childEffectComponent).onUpdate(1, particleEffect);
		verifyNever().on(particleEffect).setReleaseRate(0);
		verifyNever().on(childEffectComponent).onStop(particleEffect);

		envelopeComponent.onUpdate(9, particleEffect);
		verifyOnce().on(childEffectComponent).onStop(particleEffect);
		verifyOnce().on(particleEffect).detachChild(particleEffect);
	}
}
