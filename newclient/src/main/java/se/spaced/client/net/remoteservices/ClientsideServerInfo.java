package se.spaced.client.net.remoteservices;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import se.krka.kahlua.vm.KahluaTable;
import se.spaced.messages.protocol.c2s.remote.C2SChecksum;
import se.spaced.messages.protocol.s2c.remote.S2CChecksum;
import se.spaced.shared.network.webservices.informationservice.InformationWebService;
import se.spaced.shared.network.webservices.informationservice.ServerAccountLoadStatus;
import se.spaced.shared.network.webservices.informationservice.ServerInfo;
import se.spaced.shared.network.webservices.informationservice.ServerStatus;

import java.net.URL;

public class ClientsideServerInfo extends ServerInfo {

	private final String name;
	private final URL url;
	private final JaxWsProxyFactoryBean factory;

	public ClientsideServerInfo(
			String name,
			URL url,
			int onlineCount,
			ServerStatus serverStatus,
			int connectionQueue,
			ServerAccountLoadStatus serverLoadStatus,
			int gameServerPort) {
		super(onlineCount, serverStatus, connectionQueue, serverLoadStatus, "", "", gameServerPort);
		this.name = name;
		this.url = url;

		factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(InformationWebService.class);
		factory.setAddress(url.toExternalForm());
	}

	public void updateStatus() {
		try {
			InformationWebService informationWebService = (InformationWebService) factory.create();
			ServerInfo serverInfo = informationWebService.getServerStatus();
			copy(serverInfo);
		} catch (Exception e) {
			setServerStatus(ServerStatus.MAINTENANCE);
			setConnectionQueue(0);
			setOnlineCount(0);
		}
	}

	private void copy(ServerInfo serverInfo) {
		setServerStatus(serverInfo.getServerStatus());
		setConnectionQueue(serverInfo.getConnectionQueue());
		setOnlineCount(serverInfo.getOnlineCount());
		setServerLoadStatus(serverInfo.getServerLoadStatus());
		setS2cChecksum(serverInfo.getS2cChecksum());
		setC2sChecksum(serverInfo.getC2sChecksum());
		setGameServerPort(serverInfo.getGameServerPort());
	}

	@Override
	public KahluaTable toLuaTable() {
		KahluaTable t = super.toLuaTable();
		t.rawset("serverName", name);
		t.rawset("host", url.getHost());
		t.rawset("port", getGameServerPort());
		t.rawset("validProtocol",
				S2CChecksum.INSTANCE.toString().equals(ClientsideServerInfo.this.getS2cChecksum()) &&
						C2SChecksum.INSTANCE.toString().equals(getC2sChecksum())
		);

		return t;
	}
}
