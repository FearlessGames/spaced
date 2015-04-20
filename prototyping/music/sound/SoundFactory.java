package sound;

public interface SoundFactory {

	public SoundEffect newStreamingSoundEffect(final String filepath);

	public SoundEffect newDirectSoundEffect(final String filepath);
}
