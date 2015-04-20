package se.spaced.server.services.webservices.external;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface SpellStatisticsWebService {
	@WebMethod(operationName = "getEntitySpellActions")
	@WebResult(name = "EntitySpellActions")
	List<SpellActionEntryDTO> getEntitySpellActions(
			@WebParam(name = "entityTemplatePk") String entityTemplatePk,
			@WebParam(name = "firstResult") int firstResult,
			@WebParam(name = "maxResults") int maxResults);
}
