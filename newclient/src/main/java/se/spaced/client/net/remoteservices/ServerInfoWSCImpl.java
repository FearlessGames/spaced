package se.spaced.client.net.remoteservices;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.lifetime.ExecutorServiceLifetimeAdapter;
import se.fearless.common.lifetime.LifetimeManager;
import se.krka.kahlua.vm.KahluaTable;
import se.spaced.client.ardor.ui.events.WorldGuiEvents;
import se.spaced.client.net.GameServer;
import se.spaced.shared.concurrency.SimpleThreadFactory;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.network.webservices.informationservice.InformationWebService;
import se.spaced.shared.network.webservices.informationservice.ServerAccountLoadStatus;
import se.spaced.shared.network.webservices.informationservice.ServerStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class ServerInfoWSCImpl implements ServerInfoWSC {

	private final List<ClientsideServerInfo> servers = new ArrayList<ClientsideServerInfo>();

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ExecutorService executor = Executors.newFixedThreadPool(3,
			SimpleThreadFactory.withPrefix("ServerInfoWSCImplThread-"));

	private final EventHandler eventHandler;

	@Inject
	public ServerInfoWSCImpl(
			@Named("gameServers") List<GameServer> gameServers,
			EventHandler eventHandler,
			LifetimeManager lifetimeManager) {

		this.eventHandler = eventHandler;
		lifetimeManager.addListener(new ExecutorServiceLifetimeAdapter(executor));

		try {
			for (GameServer gameServer : gameServers) {
				addServer(gameServer.getName(), new URL(gameServer.getUrl()));
			}

		} catch (MalformedURLException e) {
			logger.warn("Malformed URL exception", e);
		}

		//we need to make a initial construction of the factory because its initial create is not threadsafe
		initBeanFactory();

	}

	private void initBeanFactory() {
		try {
			ClientProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setServiceClass(InformationWebService.class);
			factory.create();
		} catch (Exception e) {
			//
		}
	}

	@Override
	public List<ClientsideServerInfo> getServers() {
		return servers;
	}

	@Override
	public final void addServer(String name, URL wsUrl) {
		servers.add(new ClientsideServerInfo(name,
				wsUrl,
				0,
				ServerStatus.UNKNOWN,
				0,
				ServerAccountLoadStatus.UNKNOWN,
				0));
	}


	private class GetServerInfoWorker implements Runnable {
		private final ClientsideServerInfo server;

		private GetServerInfoWorker(ClientsideServerInfo server) {
			this.server = server;
		}

		@Override
		public void run() {
			server.updateStatus();
			KahluaTable serverStatusTable = server.toLuaTable();
			eventHandler.fireAsynchEvent(WorldGuiEvents.GOT_SERVER_INFO, serverStatusTable);
		}

	}

	@Override
	public void fetchServerInfo() {
		if (executor.isShutdown()) {
			return;
		}

		for (ClientsideServerInfo server : servers) {
			GetServerInfoWorker gsiw = new GetServerInfoWorker(server);
			executor.execute(gsiw);
		}
	}

}
