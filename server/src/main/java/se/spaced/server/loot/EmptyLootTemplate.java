package se.spaced.server.loot;

import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.shared.util.random.RandomProvider;

import javax.persistence.Entity;
import java.util.Collection;
import java.util.Collections;

@Entity
public class EmptyLootTemplate extends PersistableLootTemplate {
	public static final EmptyLootTemplate INSTANCE = new EmptyLootTemplate();

	protected EmptyLootTemplate() {
		super(UUID.fromString("b128a12a-45a7-4ca1-8870-9db8601fc0b6"), "EmptyLootTemplate");
	}

	private final UUID uuid = UUIDFactoryImpl.INSTANCE.randomUUID();

	@Override
	public Collection<Loot> generateLoot(RandomProvider randomProvider) {
		return Collections.emptyList();
	}
}
