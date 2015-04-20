import se.fearlessgames.common.io.FileStreamLocator;
import se.fearlessgames.common.util.SystemTimeProvider;
import sound.SoundEffect;
import sound.SoundFactory;
import sound.al.OpenALSoundFactory;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MusicPlayer {
	private MusicPlayer() {
	}

	public static void main(String[] args) throws InterruptedException {


		FileStreamLocator fileStreamLocator = new FileStreamLocator(new File("newclient\\resources\\"));
		SoundFactory soundFactory = new OpenALSoundFactory(fileStreamLocator, new ScheduledThreadPoolExecutor(4), new SystemTimeProvider());
		SoundEffect musicStream = soundFactory.newStreamingSoundEffect("sound\\music\\Crossover.ogg");
		musicStream.setLooping(true);
		musicStream.play();
		Thread.sleep(3000);

		//SoundEffect soundEffect = soundFactory.newDirectSoundEffect("sound\\sfx\\BrrDisch.ogg");
		//soundEffect.play();
		while (musicStream.isPlaying()) {
			Thread.sleep(3000);
		}

		System.out.println("done playing!");


	}
}
