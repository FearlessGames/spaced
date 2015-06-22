package se.spaced.server.services.webservices.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.interfaces.SpellActionEntryDao;
import se.spaced.server.persistence.util.PageParameters;
import se.spaced.server.stats.SpellActionEntry;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@Singleton
@WebService(endpointInterface = "se.spaced.server.services.webservices.external.SpellStatisticsWebService",
		serviceName = "SpellStatisticsService")
public class SpellStatisticsWebServiceImpl implements SpellStatisticsWebService {
	private final SpellActionEntryDao spellActionEntryDao;

	@Inject
	public SpellStatisticsWebServiceImpl(SpellActionEntryDao spellActionEntryDao) {
		this.spellActionEntryDao = spellActionEntryDao;
	}

	@Override
	public List<SpellActionEntryDTO> getEntitySpellActions(String entityTemplatePk, int firstResult, int maxResults) {
		UUID pk = UUID.fromString(entityTemplatePk);
		List<SpellActionEntry> spellActionEntries = spellActionEntryDao.findPerformersSpellActions(pk,
				new PageParameters(firstResult, maxResults));
		return toDTOs(spellActionEntries);
	}

	private List<SpellActionEntryDTO> toDTOs(Iterable<SpellActionEntry> spellActionEntries) {
		List<SpellActionEntryDTO> dtos = new ArrayList<SpellActionEntryDTO>();
		for (SpellActionEntry spellActionEntry : spellActionEntries) {
			SpellActionEntryDTO dto = new SpellActionEntryDTO();
			dto.setCompleted(spellActionEntry.isCompleted());
			dto.setEndTime(spellActionEntry.getEndTime());
			dto.setPerformerTemplateName(spellActionEntry.getPerformer().getName());
			dto.setPerformerTemplatePk(spellActionEntry.getPerformer().getPk().toString());
			dto.setSpellName(spellActionEntry.getSpell().getName());
			dto.setSpellPk(spellActionEntry.getSpell().getPk().toString());
			dto.setStartTime(spellActionEntry.getStartTime());
			dto.setTargetTemplateName(spellActionEntry.getTarget().getName());
			dto.setTargetTemplatePk(spellActionEntry.getTarget().getPk().toString());
			dtos.add(dto);
		}
		return dtos;
	}
}
