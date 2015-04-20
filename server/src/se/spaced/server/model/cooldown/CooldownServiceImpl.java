package se.spaced.server.model.cooldown;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.server.persistence.dao.interfaces.CooldownTemplateDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

@Singleton
public class CooldownServiceImpl implements CooldownService {

	private final CooldownTemplateDao dao;

	@Inject
	public CooldownServiceImpl(CooldownTemplateDao dao) {
		this.dao = dao;
	}

	@Override
	@AutoTransaction
	public CooldownTemplate find(se.spaced.messages.protocol.Cooldown cooldown) {
		return dao.findByPk(cooldown.getPk());
	}
}
