package se.spaced.client.ardor.effect;

import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.sound.SoundBuffer;
import se.spaced.client.sound.SoundSource;
import se.spaced.client.sound.SoundSourceFactory;

public class SoundEffectPrototype implements EffectPrototype {
	private final SoundBuffer soundBuffer;
	private final SoundSourceFactory sourceFactory;
	private final float falloff;
	private final float gain;
	private final boolean looping;
	private final float pitch;
	private final float referenceDistance;

	public static class Builder implements se.spaced.shared.util.Builder<SoundEffectPrototype> {
		private final SoundBuffer soundBuffer;
		private final SoundSourceFactory sourceFactory;

		private double falloff = 1.0;
		private double gain = 1.0;
		private boolean looping;
		private double pitch = 1.0;
		private double referenceDistance = 5.0;

		public Builder(SoundBuffer soundBuffer, SoundSourceFactory sourceFactory) {
			this.soundBuffer = soundBuffer;
			this.sourceFactory = sourceFactory;
		}

		@LuaMethod
		public void setFalloff(double falloff) {
			this.falloff = falloff;
		}

		@LuaMethod
		public void setGain(double gain) {
			this.gain = gain;
		}

		@LuaMethod
		public void setLooping(boolean looping) {
			this.looping = looping;
		}

		@LuaMethod
		public void setPitch(double pitch) {
			this.pitch = pitch;
		}

		@LuaMethod
		public void setReferenceDistance(double referenceDistance) {
			this.referenceDistance = referenceDistance;
		}

		@Override
		public SoundEffectPrototype build() {
			return new SoundEffectPrototype(this);
		}
	}

	private SoundEffectPrototype(Builder builder) {
		soundBuffer = builder.soundBuffer;
		sourceFactory = builder.sourceFactory;
		falloff = (float) builder.falloff;
		gain = (float) builder.gain;
		looping = builder.looping;
		pitch = (float) builder.pitch;
		referenceDistance = (float) builder.referenceDistance;
	}

	@Override
	public EffectBuilder<SoundEffect> createBuilder() {
		SoundSource source = sourceFactory.newSoundSource(soundBuffer);
		source.setFalloff(falloff);
		source.setGain(gain);
		source.setLooping(looping);
		source.setPitch(pitch);
		source.setReferenceDistance(referenceDistance);

		return new SoundEffect.Builder(source);
	}
}
