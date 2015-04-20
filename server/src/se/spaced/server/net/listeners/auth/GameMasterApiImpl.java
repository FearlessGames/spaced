package se.spaced.server.net.listeners.auth;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.mob.brains.templates.NullBrainTemplate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.BrainTemplateDao;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;
import se.spaced.server.services.GameMasterService;
import se.spaced.server.spell.SpellService;

public class GameMasterApiImpl implements GameMasterApi {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ClientConnection clientConnection;
	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final EntityService entityService;
	private final ItemService itemService;
	private final EntityTemplateDao entityTemplateDao;
	private final TransactionManager transactionManager;
	private final BrainTemplateDao brainTemplateDao;
	private final SpellService spellService;
	private final MoneyService moneyService;
	private final GameMasterService gameMasterService;

	public GameMasterApiImpl(
			EntityService entityService, ClientConnection clientConnection, SmrtBroadcaster<S2CProtocol> broadcaster,
			ItemService itemService,
			EntityTemplateDao entityTemplateDao,
			TransactionManager transactionManager,
			BrainTemplateDao brainTemplateDao,
			SpellService spellService,
			MoneyService moneyService,
			GameMasterService gameMasterService) {
		this.entityService = entityService;
		this.clientConnection = clientConnection;
		this.broadcaster = broadcaster;
		this.itemService = itemService;
		this.entityTemplateDao = entityTemplateDao;
		this.transactionManager = transactionManager;
		this.brainTemplateDao = brainTemplateDao;
		this.spellService = spellService;
		this.moneyService = moneyService;
		this.gameMasterService = gameMasterService;
	}

	@Override
	public void visit(Player self, String name) {
		ServerEntity entityToVisit = entityService.findEntityByName(name);
		if (entityToVisit == null) {
			broadcaster.create().to(self).send().entity().unknownEntityName(name);
			return;
		}
		gameMasterService.visit(self, entityToVisit);
	}


	@Override
	public void giveItem(Player self, String playerName, String templateName, int quantity) {
		S2CProtocol gmReceiver = clientConnection.getReceiver();
		ServerEntity player = getPlayerByName(gmReceiver, playerName);
		if (player == null) {
			return;
		}
		log.info("Trying to get item of template: {}", templateName);
		ServerItemTemplate template = itemService.getTemplateByName(templateName);
		if (template == null) {
			log.warn("No such template found");
			broadcaster.create().to(gmReceiver).send().gamemaster().failureNotification("Couldn't find itemtemplate " + templateName);
			return;
		}
		gameMasterService.giveItem(self, player, template, quantity);
	}

	private ServerEntity getPlayerByName(S2CProtocol gmReceiver, String playerName) {
		ServerEntity player = entityService.findEntityByName(playerName);
		if (player == null) {
			broadcaster.create().to(gmReceiver).send().gamemaster().failureNotification("Couldn't find player called " + playerName);
		}
		return player;
	}

	@Override
	public void reloadMob(Player self, final Entity entity) {
		S2CProtocol gmReceiver = clientConnection.getReceiver();
		if (entity == null) {
			broadcaster.create().to(gmReceiver).send().gamemaster().failureNotification(
					"Failed to reload mob. Unknown entity");
			return;
		}
		gameMasterService.reloadMob(self, (ServerEntity) entity);
	}

	@Override
	public void spawnMob(Player self, String mobTemplateName, String brainTemplateName) {
		S2CProtocol gmReceiver = clientConnection.getReceiver();
		Transaction transaction = transactionManager.beginTransaction();
		try {
			MobTemplate mobTemplate = (MobTemplate) entityTemplateDao.findByName(mobTemplateName);
			BrainTemplate brainTemplate = brainTemplateDao.findByName(brainTemplateName);
			if (brainTemplate == null) {
				brainTemplate = new NullBrainTemplate();
			}
			if (mobTemplate == null) {
				broadcaster.create().to(gmReceiver).send().gamemaster().failureNotification("Couldn't find mobtemplate " + mobTemplateName);
				return;
			}
			gameMasterService.spawnMob(self, mobTemplate, brainTemplate);
			transaction.commit();
		} catch (HibernateException e) {
			String message = "Failed to spawn mob ";
			log.error(message, e);
			broadcaster.create().to(gmReceiver).send().gamemaster().failureNotification("Failed to spawn mob " + e.getMessage());
			transaction.rollback();
		}
	}

	@Override
	public void reloadServerContent(Player self) {
		gameMasterService.reloadServerContent(self);
	}

	@Override
	public void grantSpell(Player self, String playerName, String spellName) {
		S2CProtocol gmReceiver = clientConnection.getReceiver();
		ServerEntity player = getPlayerByName(gmReceiver, playerName);
		if (player == null) {
			return;
		}

		ServerSpell spell = spellService.findByName(spellName);

		if (spell == null) {
			broadcaster.create().to(gmReceiver).send().gamemaster().failureNotification("Couldn't find spell with name: " + spellName);
		} else {
			gameMasterService.grantSpell(self, player, spell);
		}
	}

	@Override
	public void requestAiInfo(Player gm, Entity entity) {
		if (entity == null) {
			broadcaster.create().to(gm).send().gamemaster().failureNotification("Couldn't get AiInfo for unknown entity:");
			return;
		}
		if (entity instanceof Mob) {
			gameMasterService.sendAiInfo(gm, (Mob) entity);
		} else {
			broadcaster.create().to(gm).send().gamemaster().failureNotification("Couldn't get AiInfo for non mob: " + entity.toString());
		}
	}

	@Override
	public void giveMoney(Player gm, String playerName, String currencyName, long amount) {
		S2CProtocol gmReceiver = clientConnection.getReceiver();
		ServerEntity player = getPlayerByName(gmReceiver, playerName);
		if (player == null) {
			return;
		}

		PersistedCurrency currency = moneyService.getCurrencies().get(currencyName);
		if (currency == null) {
			broadcaster.create().to(gmReceiver).send().gamemaster().failureNotification(
					"Couldn't find currency with name: " + currencyName);
		} else {
			gameMasterService.giveMoney(gm, player, currency, amount);
		}
	}

	@Override
	public void summonEntity(Player self, String entityName) {
		S2CProtocol gmReceiver = clientConnection.getReceiver();
		ServerEntity player = getPlayerByName(gmReceiver, entityName);
		if (player == null) {
			return;
		}
		gameMasterService.summonEntity(self, player);
	}

	@Override
	public void forceException(Player gm, boolean includeActionLoop) {
		gameMasterService.forceException(gm, includeActionLoop);
	}
}
