package sound.al;

import com.google.common.io.ByteStreams;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import se.fearlessgames.common.io.StreamLocator;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.client.sound.OggInputStream;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenALSoundEffectBufferLoader {
	private static final int MAX_BUFFERS = 60;

	private final LocalBufferCache bufferCache;
	private final StreamLocator streamLocator;
	private final TimeProvider timeProvider;

	protected OpenALSoundEffectBufferLoader(StreamLocator streamLocator, TimeProvider timeProvider) {
		this.streamLocator = streamLocator;
		this.timeProvider = timeProvider;
		bufferCache = new LocalBufferCache(new CacheLoader<String, BufferUsageCounter>() {
			@Override
			public BufferUsageCounter load(String filepath) {
				IntBuffer buffer;
				if (filepath.endsWith(".ogg")) {
					buffer = bufferOggSoundEffect(filepath);
				} else {
					buffer = bufferWavSoundEffect(filepath);
				}

				if (buffer != null) {
					return new BufferUsageCounter(buffer, filepath);
				}
				return null;
			}
		});
	}

	protected IntBuffer getBufferForFile(final String filePath) {
		BufferUsageCounter bufferUsageCounter = bufferCache.get(filePath);
		bufferUsageCounter.increment();
		bufferUsageCounter.setLastUsage(timeProvider.now());
		return bufferUsageCounter.buffer;
	}

	protected void removeBufferForFile(String filepath) {
		bufferCache.invalidate(filepath);
	}


	private IntBuffer bufferWavSoundEffect(final String filepath) {
		try (InputStream is = streamLocator.getInputSupplier(filepath).getInput()) {

			WaveData waveData = WaveData.create(is);
			int format = waveData.format;
			int freq = waveData.samplerate;

			return buffer(waveData.data, format, freq);

		} catch (IOException e) {
			return null;
		}
	}


	private IntBuffer bufferOggSoundEffect(final String filepath) {
		try (OggInputStream ogg = new OggInputStream(streamLocator.getInputSupplier(filepath).getInput())) {
			final byte[] oggBytes = ByteStreams.toByteArray(ogg);

			final ByteBuffer data = BufferUtils.createByteBuffer(oggBytes.length);
			data.put(oggBytes);
			data.rewind();

			return buffer(data, resolveFormat(ogg), ogg.getRate());

		} catch (IOException e) {
			return null;
		}
	}

	private IntBuffer buffer(ByteBuffer data, int format, int freq) {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		AL10.alGenBuffers(buffer);
		AL10.alBufferData(buffer.get(0), format, data, freq);
		return buffer;
	}

	private int resolveFormat(OggInputStream ois) {
		return ois.getFormat() == OggInputStream.FORMAT_MONO16 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
	}


	private static class BufferUsageCounter {
		private final IntBuffer buffer;
		private final AtomicInteger counter;
		private final String filePath;
		private long lastUsage;

		private BufferUsageCounter(IntBuffer buffer, String filePath) {
			this.buffer = buffer;
			this.filePath = filePath;
			counter = new AtomicInteger();
		}

		public long getLastUsage() {
			return lastUsage;
		}

		public void setLastUsage(long lastUsage) {
			this.lastUsage = lastUsage;
		}

		public void increment() {
			counter.incrementAndGet();
		}

		public void decrement() {
			counter.decrementAndGet();
		}

		public boolean isZeroUsage() {
			return counter.get() == 0;
		}

		public String getFilePath() {
			return filePath;
		}
	}

	private static class LocalBufferCache extends ThreadSafeCache<String, BufferUsageCounter> {


		private LocalBufferCache(CacheLoader<String, BufferUsageCounter> stringBufferUsageCounterCacheLoader) {
			super(stringBufferUsageCounterCacheLoader);
		}

		@Override
		public void invalidate(String filePath) {
			BufferUsageCounter bufferUsageCounter = get(filePath);
			bufferUsageCounter.decrement();
			if (map.size() > MAX_BUFFERS) {
				invalidateAll();
			}

		}

		@Override
		public void invalidateAll() {

			List<BufferUsageCounter> removeList = new ArrayList<BufferUsageCounter>();

			for (String filePath : map.keySet()) {
				BufferUsageCounter bufferUsageCounter = get(filePath);
				if (bufferUsageCounter.isZeroUsage()) {
					removeList.add(bufferUsageCounter);
				}
			}

			Collections.sort(removeList, new Comparator<BufferUsageCounter>() {
				@Override
				public int compare(BufferUsageCounter o1, BufferUsageCounter o2) {
					return (int) (o2.getLastUsage() - o1.getLastUsage());
				}
			});

			for (BufferUsageCounter bufferUsageCounter : removeList) {

				AL10.alDeleteBuffers(bufferUsageCounter.buffer);
				super.invalidate(bufferUsageCounter.getFilePath());

				if (map.size() < MAX_BUFFERS - (MAX_BUFFERS / 10)) {
					break;
				}
			}


		}
	}
}
