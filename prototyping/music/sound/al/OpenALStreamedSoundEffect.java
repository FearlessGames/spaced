package sound.al;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.client.sound.OggInputStream;
import sound.SoundEffect;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class OpenALStreamedSoundEffect extends OpenALBaseSound implements SoundEffect {
	private static final int STREAM_BUFFER_SIZE = 4096 * 8;
	private final ByteBuffer readBuffer = BufferUtils.createByteBuffer(STREAM_BUFFER_SIZE);
	private final IntBuffer backBuffer = BufferUtils.createIntBuffer(1);

	private final ScheduledExecutorService executorService;

	private int format;
	private int freq;

	private ScheduledFuture<?> playbackFuture;

	private final String filePath;
	private final StreamLocator streamLocator;
	private OggInputStream musicStream;
	private boolean looping;

	protected OpenALStreamedSoundEffect(String filepath, StreamLocator streamLocator, ScheduledExecutorService executorService) {
		this.executorService = executorService;
		this.filePath = filepath;
		this.streamLocator = streamLocator;


		try {
			openOggStream();
		} catch (IOException ignored) {
		}

	}


	@Override
	public void play() {
		if (isPlaying()) {
			return;
		}

		IntBuffer buffers = BufferUtils.createIntBuffer(2);  //two buffers,  front and back (for flipping)
		AL10.alGenBuffers(buffers); //fill the buffers with id

		loadStreamToALBufferForId(buffers.get(0));
		loadStreamToALBufferForId(buffers.get(1));

		source.queueBuffers(buffers);
		source.play();

		playbackFuture = executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				updateBuffers();
			}
		}, 50, 50, TimeUnit.MILLISECONDS);

	}

	private void updateBuffers() {
		int processed = source.getProcessed();

		while (processed-- > 0) {
			backBuffer.clear();
			source.unqueueBuffers(backBuffer);
			if (loadStreamToALBufferForId(backBuffer.get(0))) {
				source.queueBuffers(backBuffer);
			} else {
				stop();
				delete();
			}
		}
	}


	private boolean loadStreamToALBufferForId(int buffer) {
		readBuffer.rewind();
		int bytesRead;

		try {
			if (musicStream.available() == 0 && looping) {
				openOggStream();
			}

			bytesRead = musicStream.read(readBuffer, 0, readBuffer.capacity());
		} catch (IOException e) {
			return false;
		}

		if (bytesRead < 0) {
			return false;
		}

		readBuffer.rewind();
		AL10.alBufferData(buffer, format, readBuffer, freq);
		return true;

	}

	private void openOggStream() throws IOException {
		if (musicStream != null) {
			musicStream.close();
		}

		musicStream = new OggInputStream(streamLocator.getInputSupplier(filePath).getInput());
		format = musicStream.getFormat() == OggInputStream.FORMAT_MONO16 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
		freq = musicStream.getRate();
	}


	@Override
	public void stop() {
		if (playbackFuture != null) {
			playbackFuture.cancel(false);
			playbackFuture = null;
		}

		try {
			musicStream.close();
		} catch (IOException ignored) {
		}

		source.stop();

		deleteAlBuffers();
	}

	@Override
	public void pause() {
		throw new RuntimeException("Not yet implementd!");
	}

	@Override
	public void rewind() {
		throw new RuntimeException("Not yet implementd!");
	}

	@Override
	public void delete() {
		if (playbackFuture != null) {
			stop();
		}
		source.delete();
	}

	@Override
	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	private void deleteAlBuffers() {

		int queued = source.getNrOfQueuedBuffers();

		while (queued-- > 0) {
			IntBuffer buffer = BufferUtils.createIntBuffer(1);
			source.unqueueBuffers(buffer);
			AL10.alDeleteBuffers(buffer);
		}
	}


}
