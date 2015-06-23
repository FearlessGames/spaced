package se.spaced.client.ardor.effect;

import com.ardor3d.extension.effect.particle.emitter.PointEmitter;
import com.ardor3d.extension.effect.particle.emitter.RingEmitter;
import com.ardor3d.image.Texture;
import com.ardor3d.math.Ring;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.TextureState;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.TextureLoadCallback;
import se.ardortech.TextureManager;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.sound.SoundBufferManager;
import se.spaced.client.sound.SoundSourceFactory;

@Singleton
public class LuaEffectApi {
	private final SoundSourceFactory sourceFactory;
	private final SoundBufferManager bufferFactory;
	private final TextureManager textureManager;

	@Inject
	public LuaEffectApi(
			SoundSourceFactory sourceFactory, SoundBufferManager bufferFactory, TextureManager textureManager) {
		this.sourceFactory = sourceFactory;
		this.bufferFactory = bufferFactory;
		this.textureManager = textureManager;
	}

	@LuaMethod(name = "CreateSoundPrototype", global = true)
	public SoundEffectPrototype.Builder createSoundPrototype(String filepath) {
		return new SoundEffectPrototype.Builder(bufferFactory.newSoundBuffer(filepath), sourceFactory);
	}


	@LuaMethod(name = "CreateParticlePrototype", global = true)
	public ParticleEffectPrototype.Builder createParticlePrototype(String name, String textureName, int particleCount) {
		final TextureState ts = new TextureState();

		textureManager.loadTexture(textureName, new TextureLoadCallback() {
			@Override
			public void loadedRequestedTexture(Texture texture) {
				ts.setTexture(texture);
				texture.setWrap(Texture.WrapMode.BorderClamp);
				ts.setEnabled(true);
			}
		});

		return new ParticleEffectPrototype.Builder(name, ts, particleCount);
	}

	@LuaMethod(name = "CreatePointEmitter", global = true)
	public PointEmitter createPointEmitter() {
		return new PointEmitter();
	}

	@LuaMethod(name = "CreateRingEmitter", global = true)
	public RingEmitter createRingEmitter(
			double centerX,
			double centerY,
			double centerZ,
			double upX,
			double upY,
			double upZ,
			double innerRadius,
			double outerRadius) {
		return new RingEmitter(new Ring(new Vector3(centerX, centerY, centerZ),
				new Vector3(upX, upY, upZ),
				innerRadius,
				outerRadius));
	}
}
