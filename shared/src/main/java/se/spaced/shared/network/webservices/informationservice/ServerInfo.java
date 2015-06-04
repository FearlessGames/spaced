package se.spaced.shared.network.webservices.informationservice;

import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;

public class ServerInfo {
	private static final Platform PLATFORM = new J2SEPlatform();
	private int onlineCount;
	private ServerStatus serverStatus;
	private int connectionQueue;
	private ServerAccountLoadStatus serverLoadStatus;
	private String s2cChecksum;
	private String c2sChecksum;
	private int gameServerPort;


	// Needed for web services serialization
	public ServerInfo() {
	}

	public ServerInfo(
			int onlineCount, ServerStatus serverStatus,
			int connectionQueue, ServerAccountLoadStatus serverLoadStatus,
			String s2cChecksum, String c2sChecksum, int gameServerPort) {
		this.onlineCount = onlineCount;
		this.serverStatus = serverStatus;
		this.connectionQueue = connectionQueue;
		this.serverLoadStatus = serverLoadStatus;
		this.s2cChecksum = s2cChecksum;
		this.c2sChecksum = c2sChecksum;
		this.gameServerPort = gameServerPort;
	}

	public int getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(int onlineCount) {
		this.onlineCount = onlineCount;
	}

	public ServerStatus getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(ServerStatus serverStatus) {
		this.serverStatus = serverStatus;
	}

	public int getConnectionQueue() {
		return connectionQueue;
	}

	public void setConnectionQueue(int connectionQueue) {
		this.connectionQueue = connectionQueue;
	}

	public void setServerLoadStatus(ServerAccountLoadStatus serverLoadStatus) {
		this.serverLoadStatus = serverLoadStatus;
	}

	public ServerAccountLoadStatus getServerLoadStatus() {
		return serverLoadStatus;
	}

	public KahluaTable toLuaTable() {
		KahluaTable serverStatusTable = PLATFORM.newTable();
		ServerStatus status = getServerStatus();
		serverStatusTable.rawset("status", status.name());

		if (status == ServerStatus.ONLINE) {
			serverStatusTable.rawset("onlineCount", (double) getOnlineCount());
		}
		return serverStatusTable;
	}

	public String getS2cChecksum() {
		return s2cChecksum;
	}

	public String getC2sChecksum() {
		return c2sChecksum;
	}

	public void setS2cChecksum(String s2cChecksum) {
		this.s2cChecksum = s2cChecksum;
	}

	public void setC2sChecksum(String c2sChecksum) {
		this.c2sChecksum = c2sChecksum;
	}

	public int getGameServerPort() {
		return gameServerPort;
	}

	public void setGameServerPort(int gameServerPort) {
		this.gameServerPort = gameServerPort;
	}

	@Override
	public String toString() {
		return "ServerInfo{" +
				"onlineCount=" + onlineCount +
				", serverStatus=" + serverStatus +
				", connectionQueue=" + connectionQueue +
				", serverLoadStatus=" + serverLoadStatus +
				", s2cChecksum='" + s2cChecksum + '\'' +
				", c2sChecksum='" + c2sChecksum + '\'' +
				'}';
	}
}