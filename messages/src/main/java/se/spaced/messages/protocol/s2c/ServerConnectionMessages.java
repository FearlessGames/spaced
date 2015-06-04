package se.spaced.messages.protocol.s2c;

import se.fearless.common.uuid.UUID;
import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Salts;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.world.TimeSystemInfo;

import java.util.List;
import java.util.Map;

@SmrtProtocol
public interface ServerConnectionMessages {
	void accountLoginResponse(String accountName, boolean successful, String message);

	void playerLoginResponse(
			boolean successful,
			String message,
			EntityData playerData,
			Map<ContainerType, ? extends SpacedItem> equipment,
			boolean isGm);

	void playerLoggedIn(EntityData data);

	void playerDisconnected(Entity entity, String name);

	void playerListResponse(List<EntityData> players);

	void requestSaltsResponse(Salts salts);

	void logoutResponse();

	void locationResponse(
			UUID playerId,
			String worldName,
			PositionalData position,
			TimeSystemInfo worldTimeSystemInfo,
			long currentDayOffset);

	void playerInfoResponse(UUID player, Map<ContainerType, String> equippedItems, boolean isGm);

	void needsAuthenticator();
}
