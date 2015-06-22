package se.spaced.server.services.webservices;

import se.spaced.shared.network.webservices.admin.SpellAdminWebService;

import javax.jws.WebService;

@WebService(endpointInterface = "se.spaced.shared.network.webservices.admin.SpellAdminWebService", serviceName = "SpellAdmin")
public class SpellAdminWebServiceImpl implements SpellAdminWebService {

	public SpellAdminWebServiceImpl() {
	}

	@Override
	public int getNumberOfSpells() {
		return 0;
	}
}
