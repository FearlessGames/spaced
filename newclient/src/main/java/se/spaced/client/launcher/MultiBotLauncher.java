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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MultiBotLauncher {

	private static final int MAX_PING = 5000;
	private static final int MAX_BOTS = 100;

	public static void main(String[] args) throws Exception {
		String host = "localhost";
		if (args.length > 0) {
			host = args[0];
		}
		final int port = 9234;
		final List<GameServer> gameServers = Arrays.asList(new GameServer("server", host));

		final String hostName = host;
		final Module connectionDetails = new Module() {

			@Override
			public void configure(Binder arg0) {
				arg0.bind(String.class).annotatedWith(Names.named("hostName")).toInstance(hostName);
				arg0.bind(int.class).annotatedWith(Names.named("port")).toInstance(port);
			}
		};


		final Queue<Integer> freeAccounts = new ConcurrentLinkedQueue<Integer>();
		final Collection<BotConnection> runningBots = new HashSet<BotConnection>();

		for (int i = 0; i < 50; i++) {
			freeAccounts.add(i + 1);
		}

		final AtomicReference<Exception> exception = new AtomicReference<Exception>();
		while (true) {
			if (exception.get() != null) {
				throw exception.get();
			}
			long highestPing = 0;
			long totalPing = 0;
			int numPings = 0;
			boolean connecting = false;
			for (BotConnection b : runningBots) {
				long latency = b.getLatency();
				if (latency > 0) {
					highestPing = Math.max(highestPing, latency);
					totalPing += latency;
					numPings++;
				}
				connecting |= b.isConnecting();
			}
			if (!connecting && !freeAccounts.isEmpty() && highestPing < MAX_PING && runningBots.size() < MAX_BOTS) {
				final Integer id = freeAccounts.remove();
				final String accountName = "anna" + id;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {

							Injector injector = Guice.createInjector(new NetworkModule(gameServers),
									new BotModule(),
									connectionDetails,
									new GameLogicModule(),
									connectionDetails);
							BotConnection bot = injector.getInstance(BotConnection.class);
							bot.setAccountName(accountName);
							injector.getInstance(UserCharacterPresenter.class);
							Main botGame = injector.getInstance(Main.class);
							runningBots.add(bot);
							botGame.run();
							runningBots.remove(bot);
							freeAccounts.add(id);
						} catch (Exception e) {
							exception.set(e);
						}
					}
				}).start();
			}

			long average = 0;
			int numBots = runningBots.size();
			if (numPings > 0) {
				average = totalPing / numPings;
			}
			// TODO: change this to log
			System.out.println("Number of running bots: " + numBots + ", numPings: " + numPings + ", highest ping: " + highestPing + ", average ping: " + average);
			Thread.sleep(1000);
		}
	}
}