package sound.al;

import com.ardor3d.math.type.ReadOnlyVector3;

public abstract class OpenALBaseSound {
	protected final OpenALSource source;

	protected OpenALBaseSound() {
		source = new OpenALSource();
	}

	public boolean isPlaying() {
		return source.isPlaying();
	}

	public void setPosition(ReadOnlyVector3 newPosition) {
		source.setPosition(newPosition);
	}

	public void setVelocity(ReadOnlyVector3 newVelocity) {
		source.setVelocity(newVelocity);
	}

	public void setReferenceDistance(float distance) {
		source.setReferenceDistance(distance);
	}

	public void setFalloff(float fallOff) {
		source.setFalloff(fallOff);
	}

	public float getGain() {
		return source.getGain();
	}

	public void setGain(float gain) {
		source.setGain(gain);
	}

	public void setRelative(boolean isRelative) {
		source.setRelative(isRelative);
	}

	public void setPitch(float pitch) {
		source.setPitch(pitch);
	}


}
