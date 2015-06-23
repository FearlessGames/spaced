package se.spaced.client.ardor.effect;

import com.ardor3d.extension.effect.particle.ParticleSystem;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.view.entity.VisualEntity;

public class ParticleEffect extends AbstractEffect implements SpatialEffect {
	private final ParticleSystem particleSystem;
	private int oldRate;

	public static class Builder implements EffectBuilder<ParticleEffect> {
		private final ParticleSystem particleSystem;

		private double delay;
		private double sustain = 5;
		private double release = 1;

		private BuildState state;
		private String entityAliasA;
		private String entityAliasB;
		private String metaNodeA;
		private String metaNodeB;
		private Node spatialParent;
		private String jointName;

		public Builder(ParticleSystem particleSystem) {
			this.particleSystem = particleSystem;
		}

		@LuaMethod
		public void playAtEntity(final String entityAlias) {
			state = BuildState.ENTITY;
			entityAliasA = entityAlias;
		}

		@LuaMethod
		public void atEntityMetaNode(final String entityAlias, final String metaNodeName) {
			state = BuildState.ENTITY;
			this.entityAliasA = entityAlias;
			this.metaNodeA = metaNodeName;
		}

		@LuaMethod
		public void atAttachmentPoint(final String entityAlias, final String jointName) {
			state = BuildState.ATTACHMENT;
			this.entityAliasA = entityAlias;
			this.jointName = jointName;
		}

		@LuaMethod
		public void fromTo(String fromEntityAlias, String toEntityAlias) {
			state = BuildState.PROJECTILE;
			this.entityAliasA = fromEntityAlias;
			this.entityAliasB = toEntityAlias;
		}

		@LuaMethod
		public void projectile(
				final String sourceAlias,
				final String sourceMetaNode,
				final String targetAlias,
				final String targetMetaNode) {
			state = BuildState.PROJECTILE;
			entityAliasA = sourceAlias;
			metaNodeA = sourceMetaNode;
			entityAliasB = targetAlias;
			metaNodeB = targetMetaNode;
		}

		@LuaMethod
		public void envelope(double delayTime, double playtime, double releaseTime) {
			this.delay = delayTime;
			this.sustain = playtime;
			this.release = releaseTime;
		}

		@Override
		public ParticleEffect buildEffect(final EffectContext context) {
			final ParticleEffect effect = new ParticleEffect(this);
			final EffectComponent<ParticleEffect> spatialComponent = state.build(this, context);
			final ParticleEnvelopeComponent envelope = new ParticleEnvelopeComponent.Builder(spatialParent)
					.delayTime(delay)
					.playtime(sustain)
					.releaseTime(release)
					.childComponent(spatialComponent)
					.build();

			effect.addEffectComponent(envelope);

			return effect;
		}

		private enum BuildState {
			PROJECTILE {
				@Override
				EffectComponent<ParticleEffect> build(final Builder outer, final EffectContext context) {
					outer.spatialParent = context.getSpatialParent();
					return new HomingComponent<ParticleEffect>(
							resolveEntity(context, outer.entityAliasA),
							context.getSource().getMetaNodePosition(outer.metaNodeA),
							resolveEntity(context, outer.entityAliasB),
							context.getTarget().getMetaNodePosition(outer.metaNodeB),
							context.getProjectileSpeed()
					);
				}
			},
			ENTITY {
				@Override
				EffectComponent<ParticleEffect> build(final Builder outer, final EffectContext context) {
					final VisualEntity entity = resolveEntity(context, outer.entityAliasA);
					outer.spatialParent = entity.getNode();
					return outer.metaNodeA == null ? ParticleEnvelopeComponent.NULL : new EntityMetaNodeComponent<ParticleEffect>(
							entity,
							outer.metaNodeA);
				}
			},
			ATTACHMENT {
				@Override
				EffectComponent<ParticleEffect> build(final Builder builder, final EffectContext context) {
					final VisualEntity entity = resolveEntity(context, builder.entityAliasA);
					builder.spatialParent = entity.getNode();
					return new AttachmentPointComponent<ParticleEffect>(entity, builder.jointName);
				}
			};

			abstract EffectComponent<ParticleEffect> build(Builder outer, EffectContext context);

			private static VisualEntity resolveEntity(final EffectContext context, final String alias) {
				return alias.equals("TARGET") ? context.getTarget() : context.getSource();
			}
		}
	}

	private ParticleEffect(Builder builder) {
		this.particleSystem = builder.particleSystem;
	}

	public void resetReleaseRate() {
		particleSystem.setReleaseRate(oldRate);
	}

	public void setReleaseRate(int rate) {
		// If the release rate already is 0 when you try to set it to 0, the particleSystem will be set to active,
		// we don't want that!
		oldRate = particleSystem.getReleaseRate();
		if (oldRate != 0) {
			particleSystem.setReleaseRate(rate);
		}
	}

	public void setSpatialParent(final Node spatialParent) {
		spatialParent.attachChild(particleSystem);
		particleSystem.updateGeometricState(0.0, true);
	}

	public void removeSpatialParent() {
		particleSystem.removeFromParent();
		particleSystem.getParticleController().setActive(false);
	}

	@Override
	public void setTranslation(final ReadOnlyVector3 translation) {
		particleSystem.setTranslation(translation);
		particleSystem.updateGeometricState(0.0, true);
	}

	@Override
	public void setRotation(final ReadOnlyMatrix3 rotation) {
		particleSystem.setRotation(rotation);
		particleSystem.updateGeometricState(0.0, true);
	}

	@Override
	public void setVelocity(final ReadOnlyVector3 velocity) {
	}

	@Override
	public void setTransform(final ReadOnlyTransform transform) {
		particleSystem.setTransform(transform);
		particleSystem.updateGeometricState(0.0, true);
	}
}
 
