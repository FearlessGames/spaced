package se.spaced.client.ardor.effect;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.extension.effect.particle.ParticleFactory;
import com.ardor3d.extension.effect.particle.ParticleSystem;
import com.ardor3d.extension.effect.particle.SimpleParticleInfluenceFactory;
import com.ardor3d.extension.effect.particle.emitter.SavableParticleEmitter;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.controller.ComplexSpatialController.RepeatType;
import com.ardor3d.scenegraph.hint.DataMode;
import se.krka.kahlua.integration.annotations.LuaMethod;

public class ParticleEffectPrototype implements EffectPrototype {
	private final Builder builder;

	public static class Builder implements se.spaced.shared.util.Builder<ParticleEffectPrototype> {
		private final String name;
		private final TextureState ts;
		private final int particleCount;

		private RepeatType repeatType = RepeatType.WRAP;
		private Vector3 emissionDirection = new Vector3(Vector3.UNIT_Y);
		private double initialVelocity = 1.0;
		private BlendState blendState;
		private ZBufferState zBufferState;
		private double startSize = 20;
		private double endSize = 4;
		private double minLifetime = 2000;
		private double maxLifetime = 3000;
		private double startSpin = 0;
		private double endSpin = 0;
		private double startMass = 1;
		private double endMass = 1;
		private ColorRGBA startColor = new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f);
		private ColorRGBA endColor = new ColorRGBA(1.0f, 1.0f, 0.0f, 0.0f);
		private double maximumAngle = 0.7853982;
		private boolean controlFlow = false;
		private boolean particlesInWorldCoords = true;
		private int warmUpIterations = 50;
		private SimpleParticleInfluenceFactory.BasicVortex basicVortex;
		private SavableParticleEmitter emitter;

		public Builder(String name, TextureState ts, int particleCount) {
			this.name = name;
			this.ts = ts;
			this.particleCount = particleCount;
		}

		@LuaMethod
		public void setStartMass(double startMass) {
			this.startMass = startMass;
		}

		@LuaMethod
		public void setEndMass(double endMass) {
			this.endMass = endMass;
		}

		@LuaMethod
		public void setBlendState(boolean enable, String sourceFunction, String destinationFunction) {
			blendState = new BlendState();
			blendState.setBlendEnabled(enable);
			blendState.setSourceFunction(BlendState.SourceFunction.valueOf(sourceFunction));
			blendState.setDestinationFunction(BlendState.DestinationFunction.valueOf(destinationFunction));
		}

		@LuaMethod
		public void setZBufferState(boolean read, boolean writeable) {
			zBufferState = new ZBufferState();
			zBufferState.setEnabled(read);
			zBufferState.setWritable(writeable);
		}

		@LuaMethod
		public void setRepeatType(String repeatType) {
			this.repeatType = RepeatType.valueOf(repeatType);
		}

		@LuaMethod
		public void setEmissionDirection(double x, double y, double z) {
			this.emissionDirection = new Vector3(x, y, z);
		}

		@LuaMethod
		public void setInitialVelocity(double initialVelocity) {
			this.initialVelocity = initialVelocity;
		}

		@LuaMethod
		public void setStartSize(double startSize) {
			this.startSize = startSize;
		}

		@LuaMethod
		public void setEndSize(double endSize) {
			this.endSize = endSize;
		}

		@LuaMethod
		public void setMinimumLifetime(double minLifetime) {
			this.minLifetime = minLifetime;
		}

		@LuaMethod
		public void setMaximumLifetime(double maxLifetime) {
			this.maxLifetime = maxLifetime;
		}

		@LuaMethod
		public void setStartSpin(double startSpin) {
			this.startSpin = startSpin;
		}

		@LuaMethod
		public void setEndSpin(double endSpin) {
			this.endSpin = endSpin;
		}

		@LuaMethod
		public void setStartColor(double r, double g, double b, double a) {
			startColor = new ColorRGBA((float) r, (float) g, (float) b, (float) a);
		}

		@LuaMethod
		public void setEndColor(double r, double g, double b, double a) {
			endColor = new ColorRGBA((float) r, (float) g, (float) b, (float) a);
		}

		@LuaMethod
		public void setMaximumAngle(double degrees) {
			maximumAngle = degrees * MathUtils.DEG_TO_RAD;
		}

		@LuaMethod
		public void setControlFlow(boolean controlFlow) {
			this.controlFlow = controlFlow;
		}

		@LuaMethod
		public void setParticlesInWorldCoords(boolean particlesInWorldCoords) {
			this.particlesInWorldCoords = particlesInWorldCoords;
		}

		@LuaMethod
		public void warmUp(int warmUpIterations) {
			this.warmUpIterations = warmUpIterations;
		}

		@LuaMethod
		public void setEmitter(SavableParticleEmitter emitter) {
			this.emitter = emitter;
		}

		@LuaMethod
		public void addVortexInfluence(
				int type,
				double height,
				double radius,
				double strenght,
				double divergence,
				boolean random,
				boolean transformWithScene) {
			this.basicVortex = new SimpleParticleInfluenceFactory.BasicVortex();
			this.basicVortex.setType(type);
			this.basicVortex.setHeight(height);
			this.basicVortex.setRadius(radius);
			this.basicVortex.setStrength(strenght);
			this.basicVortex.setDivergence(divergence);
			this.basicVortex.setRandom(random);
			this.basicVortex.setTransformWithScene(transformWithScene);
		}

		@Override
		public ParticleEffectPrototype build() {
			return new ParticleEffectPrototype(this);
		}
	}

	private ParticleEffectPrototype(final Builder builder) {
		this.builder = builder;
	}

	@Override
	public EffectBuilder<ParticleEffect> createBuilder() {
		final ParticleSystem particleSystem = ParticleFactory.buildParticles(builder.name, builder.particleCount);
		particleSystem.setRenderState(builder.ts);
		particleSystem.getSceneHints().setAllPickingHints(false);
		particleSystem.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
		particleSystem.setRotateWithScene(true);
		particleSystem.getSceneHints().setDataMode(DataMode.Arrays);

		particleSystem.getParticleGeometry().setModelBound(new BoundingSphere());

		if (builder.blendState != null) {
			particleSystem.setRenderState(builder.blendState);
		}
		if (builder.zBufferState != null) {
			particleSystem.setRenderState(builder.zBufferState);
		}
		if (builder.basicVortex != null) {
			particleSystem.addInfluence(builder.basicVortex);
		}
		if (builder.emitter != null) {
			particleSystem.setParticleEmitter(builder.emitter);
		}
		particleSystem.setRepeatType(builder.repeatType);
		particleSystem.setEmissionDirection(builder.emissionDirection);
		particleSystem.setInitialVelocity(builder.initialVelocity);
		particleSystem.setStartSize(builder.startSize);
		particleSystem.setEndSize(builder.endSize);
		particleSystem.setMinimumLifeTime(builder.minLifetime);
		particleSystem.setMaximumLifeTime(builder.maxLifetime);
		particleSystem.setStartSpin(builder.startSpin);
		particleSystem.setEndSpin(builder.endSpin);
		particleSystem.setStartMass(builder.startMass);
		particleSystem.setEndMass(builder.endMass);
		particleSystem.setStartColor(builder.startColor);
		particleSystem.setEndColor(builder.endColor);
		particleSystem.setMaximumAngle(builder.maximumAngle);
		particleSystem.setControlFlow(builder.controlFlow);
		particleSystem.setParticlesInWorldCoords(builder.particlesInWorldCoords);
		particleSystem.warmUp(builder.warmUpIterations);
		particleSystem.forceRespawn();

		return new ParticleEffect.Builder(particleSystem);
	}
}
