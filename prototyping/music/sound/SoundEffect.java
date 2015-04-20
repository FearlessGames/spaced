package sound;

import com.ardor3d.math.type.ReadOnlyVector3;

public interface SoundEffect {
	public void play();

	public boolean isPlaying();

	public void stop();

	public void pause();

	public void rewind();

	public void setLooping(boolean looping);

	public void setPosition(ReadOnlyVector3 newPosition);

	public void setVelocity(ReadOnlyVector3 newVelocity);

	public void setReferenceDistance(float distance);

	public void setFalloff(float fallOff);

	public float getGain();

	public void setGain(float gain);

	public void setRelative(boolean isRelative);

	public void setPitch(float pitch);

	public void delete();

}
