package se.spaced.server.services.webservices.external;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface KillStatisticsWebService {
	@WebMethod(operationName = "getTopKilled")
	@WebResult(name = "TopKilleds")
	List<KillStatDTO> getTopKilled(
			@WebParam(name = "firstResult") int firstResult, @WebParam(name = "maxResults") int maxResults);

	@WebMethod(operationName = "getTopKillers")
	@WebResult(name = "TopKillers")
	List<KillStatDTO> getTopKillers(
			@WebParam(name = "firstResult") int firstResult, @WebParam(name = "maxResults") int maxResults);

	@WebMethod(operationName = "getTopEntityKilledBy")
	@WebResult(name = "TopEntityKilledByList")
	List<KillStatDTO> getTopEntityKilledBy(
			@WebParam(name = "entityTemplatePk") String entityTemplatePk,
			@WebParam(name = "firstResult") int firstResult,
			@WebParam(name = "maxResults") int maxResults);

	@WebMethod(operationName = "getTopKilledByEntity")
	@WebResult(name = "TopKilledByEntityList")
	List<KillStatDTO> getTopKilledByEntity(
			@WebParam(name = "entityTemplatePk") String entityTemplatePk,
			@WebParam(name = "firstResult") int firstResult,
			@WebParam(name = "maxResults") int maxResults);
}
