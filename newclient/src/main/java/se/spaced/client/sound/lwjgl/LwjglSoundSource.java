package se.spaced.client.sound.lwjgl;

import com.ardor3d.math.type.ReadOnlyVector3;
import com.google.common.collect.Lists;
import org.lwjgl.openal.AL10;
import se.spaced.client.sound.DirectBufferCache;
import se.spaced.client.sound.SoundBuffer;
import se.spaced.client.sound.SoundSource;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;

/**
 * Sources are points emitting sound.
 */
public class LwjglSoundSource implements SoundSource {
	private final int id;
	private final DirectBufferCache bufferCache;

	public LwjglSoundSource(final int id, final DirectBufferCache bufferCache) {
		this.id = id;
		this.bufferCache = bufferCache;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void play() {
		AL10.alSourcePlay(id);
	}

	@Override
	public boolean isPlaying() {
		return AL10.alGetSourcei(id, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	@Override
	public void stop() {
		AL10.alSourceStop(id);
	}

	@Override
	public void pause() {
		AL10.alSourcePause(id);
	}

	@Override
	public void rewind() {
		AL10.alSourceRewind(id);
	}

	@Override
	public void setLooping(final boolean looping) {
		if (looping) {
			AL10.alSourcei(id, AL10.AL_LOOPING, AL10.AL_TRUE);
		} else {
			AL10.alSourcei(id, AL10.AL_LOOPING, AL10.AL_FALSE);
		}
	}

	@Override
	public void setPosition(final ReadOnlyVector3 newPosition) {
		final FloatBuffer fb = bufferCache.asFloatBuffer(newPosition.getXf(), newPosition.getYf(), newPosition.getZf());
		AL10.alSource(id, AL10.AL_POSITION, fb);
	}

	@Override
	public void setVelocity(final ReadOnlyVector3 newVelocity) {
		final FloatBuffer fb = bufferCache.asFloatBuffer(newVelocity.getXf(), newVelocity.getYf(), newVelocity.getZf());
		AL10.alSource(id, AL10.AL_VELOCITY, fb);
	}

	@Override
	public void setReferenceDistance(float distance) {
		AL10.alSourcef(id, AL10.AL_REFERENCE_DISTANCE, distance);
	}

	@Override
	public void setFalloff(float fallOff) {
		AL10.alSourcef(id, AL10.AL_ROLLOFF_FACTOR, fallOff);
	}

	@Override
	public float getGain() {
		return AL10.alGetSourcef(id, AL10.AL_GAIN);
	}

	@Override
	public void setGain(float gain) {
		AL10.alSourcef(id, AL10.AL_GAIN, gain);
	}

	@Override
	public void setRelative(boolean isRelative) {
		final int relative = isRelative ? AL10.AL_TRUE : AL10.AL_FALSE;
		AL10.alSourcei(id, AL10.AL_SOURCE_RELATIVE, relative);
	}

	@Override
	public void setPitch(float pitch) {
		AL10.alSourcef(id, AL10.AL_PITCH, pitch);
	}

	@Override
	public void delete() {
		AL10.alDeleteSources(id);
	}

	@Override
	public void queueBuffers(Iterable<SoundBuffer> buffers) {
		for (SoundBuffer buffer : buffers) {
			AL10.alSourceQueueBuffers(id, buffer.getId());
		}
	}

	@Override
	public int getBuffersProcessed() {
		return AL10.alGetSourcei(id, AL10.AL_BUFFERS_PROCESSED);
	}

	@Override
	public List<SoundBuffer> unqueueBuffers() {
		final int processed = getBuffersProcessed();
		if (processed < 1) {
			return Collections.emptyList();
		}

		final List<SoundBuffer> buffers = Lists.newArrayListWithCapacity(processed);

		for (int i = 0; i < processed; i++) {
			final int bufferId = AL10.alSourceUnqueueBuffers(id);
			buffers.add(new SoundBuffer(bufferId));
		}

		return buffers;
	}

	@Override
	public int buffersQueued() {
		return AL10.alGetSourcei(id, AL10.AL_BUFFERS_QUEUED);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		LwjglSoundSource that = (LwjglSoundSource) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
