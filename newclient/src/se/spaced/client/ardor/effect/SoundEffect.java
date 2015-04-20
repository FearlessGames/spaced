package se.spaced.client.ardor.effect;

import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.sound.SoundSource;

public class SoundEffect extends AbstractEffect implements SpatialEffect {
	private final SoundSource soundSource;
	final float originalGain;
	final double originalReleaseTime;

	public static class Builder implements EffectBuilder<SoundEffect> {
		private final SoundSource soundSource;

		private String playAtEntity;
		private String from;
		private double delayTime;
		private double playtime = 5;
		private double releaseTime = 1;
		private double originalReleaseTime = 1;

		public Builder(SoundSource soundSource) {
			this.soundSource = soundSource;
		}

		@LuaMethod
		public void playAtEntity(String entity) {
			playAtEntity = entity;
		}

		@LuaMethod
		public void fromTo(String from, String to) {
			this.from = from;
		}

		@LuaMethod
		public void envelope(double delayTime, double playtime, double releaseTime) {
			this.delayTime = delayTime;
			this.playtime = playtime;
			this.releaseTime = releaseTime;
			this.originalReleaseTime = this.releaseTime;
		}

		@Override
		public SoundEffect buildEffect(EffectContext effectContext) {
			SoundEffect effect = new SoundEffect(this);

			// TODO Solve this with a state enum
			EffectComponent<SoundEffect> spatialComponent;
			if (from != null) {
				spatialComponent = new HomingComponent<SoundEffect>(
						effectContext.getSource(),
						effectContext.getSource().getMetaNodePosition("impact"),
						effectContext.getTarget(),
						effectContext.getTarget().getMetaNodePosition("impact"),
						effectContext.getProjectileSpeed()
				);
			} else if (playAtEntity.equals("TARGET")) {
				spatialComponent = PlayAtEntityPositionComponent.create(effectContext.getTarget());
			} else {
				spatialComponent = PlayAtEntityPositionComponent.create(effectContext.getSource());
			}

			EffectComponent<SoundEffect> envelope = new SoundEnvelopeComponent(spatialComponent,
					delayTime,
					playtime,
					releaseTime);

			effect.addEffectComponent(envelope);

			return effect;
		}
	}

	private SoundEffect(Builder builder) {
		soundSource = builder.soundSource;
		originalGain = soundSource.getGain();
		originalReleaseTime = builder.originalReleaseTime;
	}

	@Override
	public void setTranslation(final ReadOnlyVector3 position) {
		soundSource.setPosition(position);
	}

	@Override
	public void setVelocity(final ReadOnlyVector3 velocity) {
		soundSource.setVelocity(velocity);
	}

	@Override
	public void setRotation(final ReadOnlyMatrix3 rotation) {
		// Sound sources don't have a rotation
	}

	@Override
	public void setTransform(ReadOnlyTransform transform) {
		// Sound sources don't have a transformation
	}

	public float getGain() {
		return soundSource.getGain();
	}

	public void setGain(float gain) {
		soundSource.setGain(gain);
	}

	public void playSound() {
		soundSource.play();
	}

	public void stopSound() {
		soundSource.stop();
		soundSource.delete();
	}

	public void pauseSound() {
		soundSource.stop();
	}
}
