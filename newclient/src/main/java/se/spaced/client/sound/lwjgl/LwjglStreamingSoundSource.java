package se.spaced.client.sound.lwjgl;

import com.ardor3d.math.type.ReadOnlyVector3;
import com.google.common.io.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.sound.OggInputStream;
import se.spaced.client.sound.SoundBuffer;
import se.spaced.client.sound.SoundBufferManager;
import se.spaced.client.sound.SoundSource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LwjglStreamingSoundSource implements SoundSource {
	private static final Logger log = LoggerFactory.getLogger(LwjglStreamingSoundSource.class);
	private final LwjglSoundSource soundSource;
	private final ScheduledExecutorService scheduler;
	private final SoundBufferManager bufferFactory;
	private final ByteSource byteSource;
	private final List<SoundBuffer> buffers;

	private OggInputStream ogg;
	private ScheduledFuture<?> scheduledFuture;
	private boolean looping;

	public LwjglStreamingSoundSource(
			LwjglSoundSource soundSource,
			ScheduledExecutorService scheduler,
			SoundBufferManager bufferFactory,
			ByteSource byteSource) {
		this.soundSource = soundSource;
		this.scheduler = scheduler;
		this.bufferFactory = bufferFactory;
		buffers = bufferFactory.newSoundBuffers(3);
		this.byteSource = byteSource;
	}

	@Override
	public void play() {
		if (soundSource.isPlaying()) {
			return;
		}

		openOgg();

		soundSource.unqueueBuffers();

		for (SoundBuffer buffer : buffers) {
			bufferFactory.updateSoundBuffer(buffer, ogg);
		}

		soundSource.queueBuffers(buffers);

		scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						List<SoundBuffer> buffers = soundSource.unqueueBuffers();
						for (SoundBuffer buffer : buffers) {
							final boolean streamUpdated = bufferFactory.updateSoundBuffer(buffer, ogg);
							if (!streamUpdated) {
								if (looping) {
									openOgg();
									bufferFactory.updateSoundBuffer(buffer, ogg);
								} else {
									stop();
								}
							}
						}
						soundSource.queueBuffers(buffers);
					}
				}, 100, 100, TimeUnit.MILLISECONDS);

		soundSource.play();
	}

	@Override
	public boolean isPlaying() {
		return soundSource.isPlaying();
	}

	@Override
	public void setGain(float gain) {
		soundSource.setGain(gain);
	}

	@Override
	public void setRelative(boolean isRelative) {
		soundSource.setRelative(isRelative);
	}

	@Override
	public void setPitch(float pitch) {
		soundSource.setPitch(pitch);
	}

	@Override
	public void delete() {
		for (SoundBuffer buffer : buffers) {
			try {
				buffer.delete();
			} catch (Exception e) {
				log.error("Failed to delete soundbuffer", e);
			}
		}
		soundSource.delete();
	}

	@Override
	public void queueBuffers(Iterable<SoundBuffer> buffers) {
		soundSource.queueBuffers(buffers);
	}

	@Override
	public int getBuffersProcessed() {
		return soundSource.getBuffersProcessed();
	}

	@Override
	public List<SoundBuffer> unqueueBuffers() {
		return soundSource.unqueueBuffers();
	}

	@Override
	public int buffersQueued() {
		return soundSource.buffersQueued();
	}

	@Override
	public float getGain() {
		return soundSource.getGain();
	}

	@Override
	public void stop() {
		if (soundSource.isPlaying()) {
			scheduledFuture.cancel(false);
			soundSource.stop();
			try {
				ogg.close();
			} catch (IOException ignored) {
			}
		}
	}

	@Override
	public void pause() {
		soundSource.pause();
	}

	@Override
	public void rewind() {
		soundSource.rewind();
	}

	@Override
	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	@Override
	public void setPosition(ReadOnlyVector3 newPosition) {
		soundSource.setPosition(newPosition);
	}

	@Override
	public void setVelocity(ReadOnlyVector3 newVelocity) {
		soundSource.setVelocity(newVelocity);
	}

	@Override
	public void setReferenceDistance(float distance) {
		soundSource.setReferenceDistance(distance);
	}

	@Override
	public void setFalloff(float fallOff) {
		soundSource.setFalloff(fallOff);
	}

	@Override
	public int getId() {
		return soundSource.getId();
	}

	private void openOgg() {
		if (ogg != null) {
			try {
				ogg.close();
			} catch (IOException ignored) {
			}
		}

		try {
			ogg = new OggInputStream(byteSource.openStream());
		} catch (IOException e) {
			log.error("Could not create stream for ogg");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		LwjglStreamingSoundSource that = (LwjglStreamingSoundSource) o;

		if (soundSource != null ? !soundSource.equals(that.soundSource) : that.soundSource != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return soundSource != null ? soundSource.hashCode() : 0;
	}
}
