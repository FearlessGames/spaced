package se.spaced.client.sound;

import com.ardor3d.math.type.ReadOnlyVector3;

import java.util.List;

public interface SoundSource {
	int getId();

	void play();

	boolean isPlaying();

	void stop();

	void pause();

	void rewind();

	void setLooping(boolean looping);

	void setPosition(ReadOnlyVector3 newPosition);

	void setVelocity(ReadOnlyVector3 newVelocity);

	void setReferenceDistance(float distance);

	void setFalloff(float fallOff);

	float getGain();

	void setGain(float gain);

	void setRelative(boolean isRelative);

	void setPitch(float pitch);

	void delete();

	void queueBuffers(Iterable<SoundBuffer> buffers);

	int getBuffersProcessed();

	List<SoundBuffer> unqueueBuffers();

	int buffersQueued();
}
