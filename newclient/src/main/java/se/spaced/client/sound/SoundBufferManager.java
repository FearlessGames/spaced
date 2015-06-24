package se.spaced.client.sound;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.io.StreamLocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class SoundBufferManager {
	private static final int DECODE_BUFFER_SIZE = 4096 * 16;
	private static final int MAX_CACHE_SIZE = 1024 * 1000 * 50;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final StreamLocator streamLocator;
	private final ThreadLocal<ByteBuffer> decodeBufferHolder;

	@Inject
	public SoundBufferManager(final StreamLocator streamLocator) {
		this.streamLocator = streamLocator;
		decodeBufferHolder = new ThreadLocal<ByteBuffer>() {
			@Override
			protected ByteBuffer initialValue() {
				return BufferUtils.createByteBuffer(DECODE_BUFFER_SIZE);
			}
		};
	}

	public SoundBuffer newSoundBuffer(final String filepath) {
		if (filepath.endsWith(".ogg")) {
			return newOggBuffer(filepath);
		} else {
			return newWavBuffer(filepath);
		}
	}

	public boolean updateSoundBuffer(SoundBuffer soundBuffer, OggInputStream ogg) {
		checkNotNull(soundBuffer == null || ogg == null, "soundBuffer or ogg can't be null");

		final ByteBuffer decodeBuffer = decodeBufferHolder.get();
		decodeBuffer.clear();

		int bytesRead = -1;

		try {
			bytesRead = ogg.read(decodeBuffer, 0, decodeBuffer.capacity());
		} catch (IOException e) {
			logger.error("Unable to stream Ogg file", e);
		}

		if (bytesRead < 0) {
			return false;
		}

		decodeBuffer.flip();
		soundBuffer.setData(decodeBuffer, resolveFormat(ogg), ogg.getRate());

		return true;
	}

	public List<SoundBuffer> newSoundBuffers(final int nrBuffers) {
		List<SoundBuffer> buffers = Lists.newArrayListWithCapacity(nrBuffers);
		for (int i = 0; i < nrBuffers; i++) {
			buffers.add(new SoundBuffer(AL10.alGenBuffers()));
		}
		return buffers;
	}

	private SoundBuffer newWavBuffer(final String filepath) {

		try (InputStream is = streamLocator.getInputSupplier(filepath).getInput()) {
			//somehow WaveData.create(is) fails when its run from a webservice (most likely because of underlying AudioSystem.getAudioInputStream(is)
			//but if we load the whole thing instead and send it in it works
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[2048];
			while ((len = is.read(buffer)) > 0) {
				baos.write(buffer, 0, len);
			}
			byte[] pcm = baos.toByteArray();
			is.close();

			WaveData waveData = WaveData.create(pcm);
			return newSoundBuffer(waveData.data, waveData.format, waveData.samplerate);
		} catch (IOException e) {
			logger.error("Could not load pcm data for " +  filepath, e);
			return null;
		}
	}

	private SoundBuffer newOggBuffer(final String filepath) {


		try (OggInputStream ogg = new OggInputStream(streamLocator.getInputSupplier(filepath).getInput())){
			final byte[] oggBytes = ByteStreams.toByteArray(ogg);
			final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(oggBytes.length);
			byteBuffer.put(oggBytes);
			byteBuffer.rewind();

			return newSoundBuffer(byteBuffer, resolveFormat(ogg), ogg.getRate());
		} catch (IOException e) {
			logger.error("Could not load pcm data for {}", filepath);
			return null;
		}
	}

	private SoundBuffer newSoundBuffer(final ByteBuffer data, final int format, final int freq) {
		final SoundBuffer soundBuffer = new SoundBuffer(AL10.alGenBuffers());
		soundBuffer.setData(data, format, freq);
		return soundBuffer;
	}

	private int resolveFormat(final OggInputStream ois) {
		return ois.getFormat() == OggInputStream.FORMAT_MONO16 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
	}

	private static class CachedSoundBuffer {
		private final SoundBuffer soundBuffer;
		private long lastUsed;

		private CachedSoundBuffer(final SoundBuffer soundBuffer, final long lastUsed) {
			this.soundBuffer = soundBuffer;
			this.lastUsed = lastUsed;
		}
	}
}
