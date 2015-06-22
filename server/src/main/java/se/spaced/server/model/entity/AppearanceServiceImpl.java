package se.spaced.server.model.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.ServerAuraInstance;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.persistence.dao.interfaces.EquipmentDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class AppearanceServiceImpl implements AppearanceService {

	private final EquipmentDao equipmentDao;
	private final EntityService entityService;

	private final EntityTargetService entityTargetService;
	private final AuraService auraService;
	private final EntityCombatService combatService;

	@Inject
	public AppearanceServiceImpl(
			EntityService entityService,
			EquipmentDao equipmentDao,
			EntityTargetService entityTargetService,
			AuraService auraService, EntityCombatService combatService) {
		this.equipmentDao = equipmentDao;
		this.entityService = entityService;
		this.entityTargetService = entityTargetService;
		this.auraService = auraService;
		this.combatService = combatService;
	}

	@Override
	@AutoTransaction
	public void notifyAppeared(ServerEntity receiverEntity, ServerEntity appeared) {
		S2CProtocol receiver = entityService.getSmrtReceiver(receiverEntity);
		notifyAppeared(receiver, appeared);
	}

	@Override
	@AutoTransaction
	public void notifyAppeared(S2CProtocol receiver, ServerEntity appeared) {
		Preconditions.checkNotNull(receiver, "receiver was null");
		EquippedItems equippedItems = equipmentDao.findByOwner(appeared);
		Map<ContainerType, ServerItem> items = equippedItems.getEquippedItems();
		Map<ContainerType, ItemTemplate> itemTemplates = Maps.newHashMap();
		for (Map.Entry<ContainerType, ServerItem> entry : items.entrySet()) {
			itemTemplates.put(entry.getKey(), entry.getValue().getTemplate());
		}
		ServerEntity target = entityTargetService.getCurrentTarget(appeared);
		EntityData data = appeared.createEntityData(target);
		receiver.entity().entityAppeared(appeared, data, itemTemplates);
		ImmutableSet<ServerAuraInstance> allAuras = auraService.getAllAuras(appeared);
		for (ServerAuraInstance aura : allAuras) {
			receiver.combat().gainedAura(appeared, aura);
		}
		if (combatService.isInCombat(appeared)) {
			receiver.combat().combatStatusChanged(appeared, true);
		}
	}

	@Override
	public void notifyDisappeared(ServerEntity receiverEntity, ServerEntity disappeared) {
		S2CProtocol receiver = entityService.getSmrtReceiver(receiverEntity);
		notifyDisappeared(receiver, disappeared);
	}

	@Override
	public void notifyDisappeared(S2CProtocol receiver, ServerEntity disappeared) {
		receiver.entity().entityDisappeared(disappeared);
	}
}
