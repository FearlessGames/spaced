package se.spaced.server.guice.modules;

import com.google.inject.Inject;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.contribution.ContributionService;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.vendor.VendorService;
import se.spaced.server.trade.TradeService;
import se.spaced.shared.util.ListenerDispatcher;

public class EntityListenerDispatcherConnector {

	@Inject
	public EntityListenerDispatcherConnector(
			ListenerDispatcher<EntityServiceListener> listenerDispatcher,
			VisibilityService visibilityService,
			AuraService auraService,
			EntityCombatService entityCombatService,
			EntityTargetService entityTargetService,
			MobOrderExecutor mobOrderExecutor,
			TradeService tradeService,
			ContributionService contributionService,
			VendorService vendorService) {
		listenerDispatcher.addListener(contributionService);
		listenerDispatcher.addListener(visibilityService);
		listenerDispatcher.addListener(entityTargetService);
		listenerDispatcher.addListener(auraService);
		listenerDispatcher.addListener(entityCombatService);
		listenerDispatcher.addListener(tradeService);
		listenerDispatcher.addListener(mobOrderExecutor);
		listenerDispatcher.addListener(vendorService);
	}
}
