package se.spaced.client.sound;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class DirectBufferCache {
	private final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(3);
	private final IntBuffer intBuffer = BufferUtils.createIntBuffer(1);

	public IntBuffer intBuffer() {
		intBuffer.clear();
		return intBuffer;
	}

	public FloatBuffer asFloatBuffer(final float x, final float y, final float z) {
		intBuffer.clear();
		floatBuffer.put(0, x).put(1, y).put(2, z);
		return floatBuffer;
	}
}
