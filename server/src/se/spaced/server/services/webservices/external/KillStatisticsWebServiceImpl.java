package se.spaced.server.services.webservices.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.util.PageParameters;
import se.spaced.server.stats.KillStat;
import se.spaced.server.stats.KillStatisticsService;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@Singleton
@WebService(endpointInterface = "se.spaced.server.services.webservices.external.KillStatisticsWebService",
		serviceName = "KillStatisticsService")
public class KillStatisticsWebServiceImpl implements KillStatisticsWebService {
	private final KillStatisticsService killStatisticsService;

	@Inject
	public KillStatisticsWebServiceImpl(KillStatisticsService killStatisticsService) {
		this.killStatisticsService = killStatisticsService;
	}


	@Override
	public List<KillStatDTO> getTopKilled(int firstResult, int maxResults) {
		List<KillStat> killStats = killStatisticsService.getTopKilled(new PageParameters(firstResult, maxResults));
		return copyToDto(killStats);
	}

	@Override
	public List<KillStatDTO> getTopKillers(int firstResult, int maxResults) {
		List<KillStat> killStats = killStatisticsService.getTopKillers(new PageParameters(firstResult, maxResults));
		return copyToDto(killStats);
	}

	@Override
	public List<KillStatDTO> getTopEntityKilledBy(String entityTemplatePk, int firstResult, int maxResults) {
		List<KillStat> killStats = killStatisticsService.getTopEntityKilledBy(UUID.fromString(entityTemplatePk),
				new PageParameters(firstResult, maxResults));
		return copyToDto(killStats);
	}

	@Override
	public List<KillStatDTO> getTopKilledByEntity(String entityTemplatePk, int firstResult, int maxResults) {
		List<KillStat> killStats = killStatisticsService.getTopKilledByEntity(UUID.fromString(entityTemplatePk),
				new PageParameters(firstResult, maxResults));
		return copyToDto(killStats);
	}

	private List<KillStatDTO> copyToDto(List<KillStat> killStats) {
		List<KillStatDTO> killStatDTOs = new ArrayList<KillStatDTO>();
		for (KillStat killStat : killStats) {
			KillStatDTO killStatDTO = new KillStatDTO();
			killStatDTO.setEntityPk(killStat.getEntity().getPk().toString());
			killStatDTO.setEntityName(killStat.getEntity().getName());
			killStatDTO.setKillCount(killStat.getKillCount());
			killStatDTOs.add(killStatDTO);
		}
		return killStatDTOs;
	}


}
