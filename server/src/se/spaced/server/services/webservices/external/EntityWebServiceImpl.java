package se.spaced.server.services.webservices.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;
import se.spaced.server.services.PlayerConnectedService;
import se.spaced.server.services.PlayerConnectionInfo;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@Singleton
@WebService(endpointInterface = "se.spaced.server.services.webservices.external.EntityWebService",
		serviceName = "EntityService")
public class EntityWebServiceImpl implements EntityWebService {

	private final EntityTemplateDao entityTemplateDao;
	private final PlayerConnectedService playerConnectedService;

	@Inject
	public EntityWebServiceImpl(EntityTemplateDao entityTemplateDao, PlayerConnectedService playerConnectedService) {
		this.entityTemplateDao = entityTemplateDao;
		this.playerConnectedService = playerConnectedService;
	}

	@Override
	public EntityTemplateDTO getEntity(String pk) {
		EntityTemplate template = entityTemplateDao.findByPk(UUID.fromString(pk));
		EntityTemplateDTO dto = new EntityTemplateDTO();
		dto.setTemplatePk(template.getPk().toString());
		dto.setTemplateName(template.getName());
		return dto;
	}

	@Override
	public List<ConnectedEntityDTO> getCurrentConnectedEntities() {
		List<ConnectedEntityDTO> list = new ArrayList<ConnectedEntityDTO>();
		for (PlayerConnectionInfo playerInfo : playerConnectedService.getConnectedPlayers()) {
			EntityTemplateDTO dto = new EntityTemplateDTO();
			dto.setTemplatePk(playerInfo.getPlayer().getTemplate().getPk().toString());
			dto.setTemplateName(playerInfo.getPlayer().getTemplate().getName());

			ConnectedEntityDTO ce = new ConnectedEntityDTO();
			ce.setEntityTemplate(dto);
			ce.setConnectedAt(playerInfo.getDate());

			list.add(ce);
		}
		return list;
	}
}
