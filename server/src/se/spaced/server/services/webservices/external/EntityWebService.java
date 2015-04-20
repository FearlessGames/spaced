package se.spaced.server.services.webservices.external;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface EntityWebService {
	@WebMethod(operationName = "getEntity")
	@WebResult(name = "Entity")
	EntityTemplateDTO getEntity(@WebParam(name = "uuid") String pk);

	@WebMethod(operationName = "getCurrentConnectedEntities")
	@WebResult(name = "Entity")
	List<ConnectedEntityDTO> getCurrentConnectedEntities();
}
