package se.spaced.client.net.remoteservices;

import java.net.URL;
import java.util.List;


public interface ServerInfoWSC {

	List<ClientsideServerInfo> getServers();

	void addServer(String name, URL url);

	void fetchServerInfo();
}
