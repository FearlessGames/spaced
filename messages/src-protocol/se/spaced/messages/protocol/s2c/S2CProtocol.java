package se.spaced.messages.protocol.s2c;

import se.smrt.core.ProtocolVersion;
import se.smrt.core.SmrtProtocol;

@SmrtProtocol("S2C")
@ProtocolVersion("1.1")
public interface S2CProtocol {
	ServerChatMessages chat();

	ServerCombatMessages combat();

	ServerConnectionMessages connection();

	ServerPingMessages ping();

	ServerEntityDataMessages entity();

	ServerMovementMessages movement();

	ServerSpellMessages spell();

	ServerStatisticsMessages stats();

	ServerItemMessages item();

	ServerEquipmentMessages equipment();

	ServerProjectileMessages projectile();

	ServerAccountMessages account();

	ServerTradeMessages trade();

	ServerVendorMessages vendor();

	ServerGameMasterMessages gamemaster();

	ServerLootMessages loot();

}
