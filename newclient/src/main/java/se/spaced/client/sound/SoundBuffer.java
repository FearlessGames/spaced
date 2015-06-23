package se.spaced.client.sound;

import org.lwjgl.openal.AL10;

import java.nio.ByteBuffer;

public class SoundBuffer {
	final int id;

	public SoundBuffer(final int id) {
		this.id = id;
	}

	public void setData(final ByteBuffer data, final int format, final int freq) {
		AL10.alBufferData(id, format, data, freq);
	}

	public int getId() {
		return id;
	}

	public int getSize() {
		return AL10.alGetBufferi(id, AL10.AL_SIZE);
	}

	public void delete() {
		AL10.alDeleteBuffers(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SoundBuffer that = (SoundBuffer) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
