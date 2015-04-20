package sound.al;

import com.ardor3d.math.type.ReadOnlyVector3;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class OpenALSource {
	private final int id;
	private final FloatBuffer position = BufferUtils.createFloatBuffer(3);
	private final FloatBuffer velocity = BufferUtils.createFloatBuffer(3);

	protected OpenALSource() {
		IntBuffer sorceHolder = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(sorceHolder);
		id = sorceHolder.get(0);
	}

	protected void queueBuffers(IntBuffer buffers) {
		AL10.alSourceQueueBuffers(id, buffers);
	}

	protected void play() {
		AL10.alSourcePlay(id);
	}

	protected int getProcessed() {
		return AL10.alGetSourcei(id, AL10.AL_BUFFERS_PROCESSED);
	}

	protected void unqueueBuffers(IntBuffer buffer) {
		AL10.alSourceUnqueueBuffers(id, buffer);
	}

	protected boolean isPlaying() {
		try {
			return AL10.alGetSourcei(id, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
		} catch (Exception e) {
			return false;
		}
	}

	protected void stop() {
		AL10.alSourceStop(id);
	}

	protected void delete() {
		AL10.alDeleteSources((IntBuffer) BufferUtils.createIntBuffer(1).put(id).rewind());
	}

	protected int getNrOfQueuedBuffers() {
		return AL10.alGetSourcei(id, AL10.AL_BUFFERS_QUEUED);
	}

	protected void setLooping(boolean looping) {
		if (looping) {
			AL10.alSourcei(id, AL10.AL_LOOPING, AL10.AL_TRUE);
		} else {
			AL10.alSourcei(id, AL10.AL_LOOPING, AL10.AL_FALSE);
		}
	}

	protected void setPosition(ReadOnlyVector3 newPosition) {
		position.put(0, newPosition.getXf());
		position.put(1, newPosition.getYf());
		position.put(2, newPosition.getZf());
		AL10.alSource(id, AL10.AL_POSITION, position);
	}

	protected void setVelocity(ReadOnlyVector3 newVelocity) {
		position.put(0, newVelocity.getXf());
		position.put(1, newVelocity.getYf());
		position.put(2, newVelocity.getZf());
		AL10.alSource(id, AL10.AL_VELOCITY, velocity);
	}

	protected void setReferenceDistance(float distance) {
		AL10.alSourcef(id, AL10.AL_REFERENCE_DISTANCE, distance);
	}

	protected void setFalloff(float fallOff) {
		AL10.alSourcef(id, AL10.AL_ROLLOFF_FACTOR, fallOff);
	}

	protected float getGain() {
		return AL10.alGetSourcef(id, AL10.AL_GAIN);
	}

	protected void setGain(float gain) {
		AL10.alSourcef(id, AL10.AL_GAIN, gain);
	}

	protected void setRelative(boolean isRelative) {
		final int relative = isRelative ? AL10.AL_TRUE : AL10.AL_FALSE;
		AL10.alSourcei(id, AL10.AL_SOURCE_RELATIVE, relative);
	}

	protected void setPitch(float pitch) {
		AL10.alSourcef(id, AL10.AL_PITCH, pitch);
	}

	protected void pause() {
		AL10.alSourcePause(id);
	}

	protected void rewind() {
		AL10.alSourceRewind(id);
	}
}
