package sound.al;

import sound.SoundEffect;

import java.nio.IntBuffer;

public class OpenALDirectSoundEffect extends OpenALBaseSound implements SoundEffect {
	private final OpenALSource source;
	private final IntBuffer buffer;

	private final OpenALSoundEffectBufferLoader soundEffectBufferLoader;
	private final String filepath;

	protected OpenALDirectSoundEffect(IntBuffer buffer, String filepath, OpenALSoundEffectBufferLoader soundEffectBufferLoader) {
		this.buffer = buffer;
		this.filepath = filepath;
		this.soundEffectBufferLoader = soundEffectBufferLoader;
		source = new OpenALSource();
	}

	@Override
	public void play() {
		source.queueBuffers(buffer);
		source.play();
	}

	@Override
	public void stop() {
		source.stop();
	}

	@Override
	public void pause() {
		source.pause();
	}

	@Override
	public void rewind() {
		source.rewind();
	}

	@Override
	public void delete() {
		source.unqueueBuffers(buffer);
		soundEffectBufferLoader.removeBufferForFile(filepath);
		source.delete();
	}

	@Override
	public void setLooping(boolean looping) {
		source.setLooping(looping);
	}
}
