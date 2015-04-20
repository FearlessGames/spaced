package se.spaced.server.services.webservices.external;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface BroadcastWebService {
	@WebMethod(operationName = "sendMessage")
	BroadcastResultDTO sendMessage(@WebParam(name = "uuid") String pk, @WebParam(name = "message") String message);

	@WebMethod(operationName = "sendGlobalMessage")
	BroadcastResultDTO sendGlobalMessage(@WebParam(name = "message") String message);
}
