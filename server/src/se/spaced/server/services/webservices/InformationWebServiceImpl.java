package se.spaced.server.services.webservices;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.messages.protocol.c2s.remote.C2SChecksum;
import se.spaced.messages.protocol.s2c.remote.S2CChecksum;
import se.spaced.server.account.AccountService;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.net.RemoteServer;
import se.spaced.server.services.PlayerConnectedService;
import se.spaced.shared.network.webservices.informationservice.InformationWebService;
import se.spaced.shared.network.webservices.informationservice.ServerAccountLoadStatus;
import se.spaced.shared.network.webservices.informationservice.ServerInfo;
import se.spaced.shared.network.webservices.informationservice.ServerMetrics;
import se.spaced.shared.network.webservices.informationservice.ServerStatus;
import se.spaced.shared.util.CacheUpdater;
import se.spaced.shared.util.CachedValue;
import se.spaced.shared.util.TimeConverter;

import javax.jws.WebService;

@Singleton
@WebService(endpointInterface = "se.spaced.shared.network.webservices.informationservice.InformationWebService",
		serviceName = "InformationService")
public class InformationWebServiceImpl implements InformationWebService, CacheUpdater<ServerInfo> {
	private static final long CACHE_TIMEOUT = TimeConverter.ONE_MINUTE.getTimeInMillis();
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private CachedValue<ServerInfo> cachedValue;
	private PlayerConnectedService playerConnectedService;
	private AccountService accountService;
	private RemoteServer remoteServer;
	private ActionScheduler actionScheduler;
	private int gameServerPort;

	public InformationWebServiceImpl() {
		//not used but needed acording to jaxws specs
	}

	@Inject
	public InformationWebServiceImpl(
			TimeProvider timeProvider,
			PlayerConnectedService playerConnectedService,
			AccountService accountService,
			RemoteServer remoteServer,
			ActionScheduler actionScheduler, @Named("gameServerPort") int gameServerPort) {
		this.playerConnectedService = playerConnectedService;
		this.accountService = accountService;
		this.remoteServer = remoteServer;
		this.actionScheduler = actionScheduler;
		this.gameServerPort = gameServerPort;
		cachedValue = new CachedValue<ServerInfo>(timeProvider, CACHE_TIMEOUT, this);
	}

	@Override
	public ServerInfo getServerStatus() {
		ServerInfo serverInfo = cachedValue.getCachedData();
		serverInfo.setServerStatus(getStatus());
		return serverInfo;
	}

	@Override
	public ServerMetrics getServerMetrics() {
		return new ServerMetrics(actionScheduler.getDelay());
	}

	@Override
	public ServerInfo refreshCashedData() {
		ServerAccountLoadStatus serverLoadStatus = accountService.getServerAccountLoadStatus();
		int nrOfCurrentlyLoggedInClients = playerConnectedService.getNrOfCurrentlyLoggedInClients();
		logger.info("UPDATING CACHE WITH:_" + nrOfCurrentlyLoggedInClients);

		return new ServerInfo(nrOfCurrentlyLoggedInClients, getStatus(), 0, serverLoadStatus,
				S2CChecksum.INSTANCE.toString(),
				C2SChecksum.INSTANCE.toString(), gameServerPort);
	}

	private ServerStatus getStatus() {
		if (remoteServer.isRunning()) {
			return ServerStatus.ONLINE;
		}
		return ServerStatus.STARTING;
	}
}
