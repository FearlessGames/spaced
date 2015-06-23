package se.spaced.client.ardor.effect;

import com.ardor3d.scenegraph.Node;

public class ParticleEnvelopeComponent extends AbstractEnvelopeComponent<ParticleEffect> {
	private final Node spatialParent;
	// Used when ParticleEnvelopeComponent is built without an EffectComponent child specified
	// Move it of we some day get a class similar to java.util.Collections 
	public static final EffectComponentAdapter<ParticleEffect> NULL = new EffectComponentAdapter<ParticleEffect>();

	public static class Builder implements se.spaced.shared.util.Builder<ParticleEnvelopeComponent> {
		private final Node spatialParent;
		private EffectComponent<? extends Effect> childComponent = NULL;
		private double delayTime;
		private double playtime;
		private double releaseTime;

		public Builder(final Node attachmentNode) {
			this.spatialParent = attachmentNode;
		}

		public Builder childComponent(EffectComponent<? extends Effect> childComponent) {
			this.childComponent = childComponent;
			return this;
		}

		public Builder delayTime(double delayTime) {
			this.delayTime = delayTime;
			return this;
		}

		public Builder playtime(double playtime) {
			this.playtime = playtime;
			return this;
		}

		public Builder releaseTime(double releaseTime) {
			this.releaseTime = releaseTime;
			return this;
		}

		@Override
		public ParticleEnvelopeComponent build() {
			return new ParticleEnvelopeComponent(this);
		}
	}

	private ParticleEnvelopeComponent(Builder builder) {
		super(builder.childComponent, builder.delayTime, builder.playtime, builder.releaseTime);
		this.spatialParent = builder.spatialParent;
	}

	@Override
	public void onRelease(ParticleEffect effect) {
		effect.setReleaseRate(0);
	}

	@Override
	public void onReleasing(ParticleEffect effect, double dt) {
	}

	@Override
	public void onReleased(ParticleEffect effect) {
		effect.removeSpatialParent();
	}

	@Override
	public void onStart(ParticleEffect effect) {
		effect.setSpatialParent(spatialParent);
	}
}
