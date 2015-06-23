package se.spaced.client.model.listener;

import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.util.List;

public interface LoginListener {
	void successfulPlayerLogin();

	void failedPlayerLogin(String message);

	void characterListUpdated(List<EntityData> characters);

	void successfulPlayerLogout();
}
