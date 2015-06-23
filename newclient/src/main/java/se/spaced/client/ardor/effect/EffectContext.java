package se.spaced.client.ardor.effect;

import com.ardor3d.scenegraph.Node;
import se.spaced.client.view.entity.VisualEntity;

public class EffectContext {
	private final Node spatialParent;
	private final VisualEntity source;
	private final VisualEntity target;
	private final double projectileSpeed;

	public static class Builder implements se.spaced.shared.util.Builder<EffectContext> {
		private Node spatialParent;
		private VisualEntity source;
		private VisualEntity target;
		private double projectileSpeed;

		public Builder spatialParent(Node spatialParent) {
			this.spatialParent = spatialParent;
			return this;
		}

		public Builder source(VisualEntity source) {
			this.source = source;
			return this;
		}

		public Builder target(VisualEntity target) {
			this.target = target;
			return this;
		}

		public Builder setProjectileSpeed(double projectileSpeed) {
			this.projectileSpeed = projectileSpeed;
			return this;
		}

		@Override
		public EffectContext build() {
			return new EffectContext(this);
		}
	}

	private EffectContext(Builder builder) {
		this.spatialParent = builder.spatialParent;
		this.source = builder.source;
		this.target = builder.target;
		this.projectileSpeed = builder.projectileSpeed;
	}

	public Node getSpatialParent() {
		return spatialParent;
	}

	public VisualEntity getSource() {
		return source;
	}

	public VisualEntity getTarget() {
		return target;
	}

	public double getProjectileSpeed() {
		return projectileSpeed;
	}
}
