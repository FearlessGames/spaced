package se.spaced.server.services.webservices.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

import javax.jws.WebParam;
import javax.jws.WebService;

@Singleton
@WebService(endpointInterface = "se.spaced.server.services.webservices.external.BroadcastWebService",
		serviceName = "BroadcastService")
public class BroadcastWebServiceImpl implements BroadcastWebService {

	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final EntityService entityService;

	@Inject
	public BroadcastWebServiceImpl(SmrtBroadcaster<S2CProtocol> broadcaster, EntityService entityService) {
		this.broadcaster = broadcaster;
		this.entityService = entityService;
	}

	@Override
	public BroadcastResultDTO sendMessage(@WebParam(name = "uuid") String pk, @WebParam(name = "message") String message) {
		UUID uuid;
		try {
			uuid = UUID.fromString(pk);
		} catch (IllegalArgumentException e) {
			return BroadcastResultDTO.fail(String.format("Bad uuid %s", pk));
		}
		ServerEntity entity = entityService.getEntity(uuid);
		if (entity == null) {
			return BroadcastResultDTO.fail(String.format("No entity with id %s", pk));
		}
		broadcaster.create().to(entity).send().chat().systemMessage(message);
		return BroadcastResultDTO.success();
	}

	@Override
	public BroadcastResultDTO sendGlobalMessage(@WebParam(name = "message") String message) {
		broadcaster.create().toAll().send().chat().systemMessage(message);
		return BroadcastResultDTO.success();
	}
}
