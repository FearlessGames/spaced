package se.spaced.shared.network.webservices.informationservice;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface InformationWebService {

	@WebMethod(operationName = "GetServerStatus")
	@WebResult(name = "ServerInfo")
	ServerInfo getServerStatus();

	@WebMethod(operationName = "GetServerMetrics")
	@WebResult(name = "ServerMetrics")
	ServerMetrics getServerMetrics();

}
