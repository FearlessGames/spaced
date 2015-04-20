package se.spaced.shared.network.webservices.admin;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface SpellAdminWebService {
	@WebMethod(operationName = "GetNumberOfSpells")
	@WebResult(name = "Integer")
	int getNumberOfSpells();
}
