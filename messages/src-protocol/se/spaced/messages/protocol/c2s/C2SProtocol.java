package se.spaced.messages.protocol.c2s;

import se.smrt.core.ProtocolVersion;
import se.smrt.core.SmrtProtocol;

@SmrtProtocol("C2S")
@ProtocolVersion("1.0")
public interface C2SProtocol {
	ClientChatMessages chat();

	ClientCombatMessages combat();

	ClientConnectionMessages connection();

	ClientPingMessages ping();

	ClientEntityDataMessages entity();

	ClientSpellMessages spell();

	ClientItemMessages items();

	ClientEquipmentMessages equipment();

	ClientStatisticsMessages stats();

	ClientMovementMessages movement();

	ClientAccountMessages account();

	ClientTradeMessages trade();

	ClientVendorMessages vendor();

	ClientGameMasterMessages gamemaster();
}
