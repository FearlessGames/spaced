package se.spaced.client.net.smrt;

import se.spaced.messages.protocol.c2s.C2SProtocol;

import java.net.InetSocketAddress;

public interface ServerConnection {
	void connect(String host, int port);

	void disconnect(String message);

	double getDownSpeed();

	double getUpSpeed();

	C2SProtocol getReceiver();

	boolean isConnected();

	InetSocketAddress getLocalAddress();
}
