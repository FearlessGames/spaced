package se.spaced.client.launcher;

import se.spaced.client.launcher.modules.DevResourceModule;
import se.spaced.client.net.GameServer;

import java.util.Arrays;
import java.util.List;

public class ArdorRealGame {

	private ArdorRealGame() {
	}

	public static void main(String[] args) {

		List<GameServer> servers = Arrays.asList(
				new GameServer("localhost", "http://localhost:9000/InformationService"),
				new GameServer("spaced-dev", "http://spaced-alpha.fearlessgames.se:9000/InformationService"),
				new GameServer("spaced-alpha", "http://spaced-alpha.fearlessgames.se:9001/InformationService"));

		ClientStarter clientStarter = new ClientStarter(servers, new DevResourceModule());
		clientStarter.start(null);

	}


}