package se.spaced.server.net.mina;

import com.google.inject.Inject;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.CooldownProxy;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.c2s.remote.C2SAbstractRequiredReadCodecImpl;
import se.spaced.messages.protocol.c2s.remote.C2SRequiredReadCodec;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.network.protocol.codec.SharedCodec;
import se.spaced.shared.playback.RecordingPoint;

import java.io.IOException;
import java.io.InputStream;

public class ClientToServerRequiredReadCodec extends C2SAbstractRequiredReadCodecImpl implements C2SRequiredReadCodec {

	private final SharedCodec sharedCodec;
	private final EntityService entityService;
	private final SpellService spellService;
	private final ItemService itemService;
	private final InventoryDao inventoryDao;

	@Inject
	public ClientToServerRequiredReadCodec(
			SharedCodec sharedCodec,
			EntityService entityService,
			SpellService spellService,
			ItemService itemService, InventoryDao inventoryDao) {
		this.sharedCodec = sharedCodec;
		this.entityService = entityService;
		this.spellService = spellService;
		this.itemService = itemService;
		this.inventoryDao = inventoryDao;
	}

	@Override
	public se.spaced.messages.protocol.Cooldown readCooldown(InputStream input) throws IOException {
		return new CooldownProxy(sharedCodec.readUUID(this, input));
	}

	@Override
	public Entity readEntity(InputStream input) throws IOException {
		UUID uuid = sharedCodec.readUUID(this, input);
		if (uuid.equals(UUID.ZERO)) {
			return null;
		}
		ServerEntity entity = entityService.getEntity(uuid);
		if (entity == null) {
			// Not sure if this is a good idea
			//throw new IllegalArgumentException("Could not find entity with UUID " + uuid);
		}

		return entity;
	}

	@Override
	public SpacedInventory readSpacedInventory(InputStream input) throws IOException {
		return inventoryDao.findByPk(readUUID(input));
	}

	@Override
	public SpacedItem readSpacedItem(InputStream input) throws IOException {
		UUID id = sharedCodec.readUUID(this, input);
		return itemService.getItemByPk(id);
	}

	@Override
	public Gender readGender(InputStream input) throws IOException {
		byte ordinal = readByte(input);
		return Gender.values()[ordinal];
	}

	@Override
	public Spell readSpell(InputStream input) throws IOException {
		UUID spellId = readUUID(input);
		return spellService.getSpellById(spellId);
	}

	@Override
	public AnimationState readAnimationState(InputStream input) throws IOException {
		return sharedCodec.readAnimationState(this, input);
	}

	@Override
	public ContainerType readContainerType(InputStream input) throws IOException {
		return sharedCodec.readContainerType(this, input);
	}

	@Override
	public RecordingPoint<AnimationState> readRecordingPointOfAnimationState(InputStream input) throws IOException {
		return sharedCodec.readRecordingPoint(this, input);
	}

	@Override
	public UUID readUUID(InputStream input) throws IOException {
		return sharedCodec.readUUID(this, input);
	}
}
