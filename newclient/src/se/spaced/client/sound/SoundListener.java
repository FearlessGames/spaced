package se.spaced.client.sound;

import com.ardor3d.math.type.ReadOnlyVector3;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import se.ardortech.math.SpacedVector3;

import java.nio.FloatBuffer;

public class SoundListener {
	/**
	 * Position of the listener.
	 */
	FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});

	/**
	 * Velocity of the listener.
	 */
	FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});

	/**
	 * Orientation of the listener. (first 3 elements are "at", second 3 are up")
	 */
	FloatBuffer orientation = BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});

	public SoundListener() {
		if (!AL.isCreated()) {
			try {
				AL.create();
				AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}
		position.flip();
		velocity.flip();
		orientation.flip();

		AL10.alListener(AL10.AL_POSITION, position);
		AL10.alListener(AL10.AL_VELOCITY, velocity);
		AL10.alListener(AL10.AL_ORIENTATION, orientation);
	}

	public void setPosition(SpacedVector3 position) {
		this.position.put(0, (float) position.getX());
		this.position.put(1, (float) position.getY());
		this.position.put(2, (float) position.getZ());
		AL10.alListener(AL10.AL_POSITION, this.position);
	}

	public void setVelocity(ReadOnlyVector3 velocity) {
		this.velocity.put(0, velocity.getXf());
		this.velocity.put(1, velocity.getYf());
		this.velocity.put(2, velocity.getZf());
		AL10.alListener(AL10.AL_VELOCITY, this.velocity);
	}

	public void setOrientation(ReadOnlyVector3 at, ReadOnlyVector3 up) {
		orientation.put(0, at.getXf());
		orientation.put(1, at.getYf());
		orientation.put(2, at.getZf());
		orientation.put(3, up.getXf());
		orientation.put(4, up.getYf());
		orientation.put(5, up.getZf());
		AL10.alListener(AL10.AL_ORIENTATION, orientation);
	}
}
