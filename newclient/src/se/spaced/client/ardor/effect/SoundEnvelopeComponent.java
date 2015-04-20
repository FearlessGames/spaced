package se.spaced.client.ardor.effect;

public class SoundEnvelopeComponent extends AbstractEnvelopeComponent<SoundEffect> {

	public SoundEnvelopeComponent(
			EffectComponent<? extends Effect> childComponent, double delayTime, double playtime, double releaseTime) {
		super(childComponent, delayTime, playtime, releaseTime);
	}

	@Override
	public void onRelease(final SoundEffect effect) {
	}

	@Override
	public void onReleasing(final SoundEffect effect, final double dt) {
		final float currentGain = effect.getGain();
		final float newGain = Math.max(0,
				currentGain - (effect.originalGain / (float) effect.originalReleaseTime) * (float) dt);
		effect.setGain(newGain);
	}

	@Override
	public void onReleased(final SoundEffect effect) {
		effect.stopSound();
	}

	@Override
	public void onStart(final SoundEffect effect) {
		effect.playSound();
	}
}
