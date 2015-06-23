package se.spaced.client.launcher;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import se.ardortech.Main;
import se.spaced.client.bot.BotConnection;
import se.spaced.client.launcher.modules.BotModule;
import se.spaced.client.launcher.modules.GameLogicModule;
import se.spaced.client.launcher.modules.smrt.NetworkModule;
import se.spaced.client.net.GameServer;
import se.spaced.client.presenter.UserCharacterPresenter;

import java.util.Arrays;
import java.util.List;

public class BotLauncher {

	public static void main(String[] args) {

		String host = "localhost";
		if (args.length > 0) {
			host = args[0];
		}
		final int port = 9234;

		final String hostName = host;
		final Module connectionDetails = new Module() {

			@Override
			public void configure(Binder arg0) {
				arg0.bind(String.class).annotatedWith(Names.named("hostName")).toInstance(hostName);
				arg0.bind(int.class).annotatedWith(Names.named("port")).toInstance(port);
			}
		};
		List<GameServer> gameServers = Arrays.asList(new GameServer("server", host));
		Injector injector = Guice.createInjector(new NetworkModule(gameServers), new BotModule(), connectionDetails,
				new GameLogicModule());
		injector.getInstance(UserCharacterPresenter.class);
		BotConnection bot = injector.getInstance(BotConnection.class);
		bot.setAccountName("anna10");

		Main botGame = injector.getInstance(Main.class);

		botGame.run();
	}
}