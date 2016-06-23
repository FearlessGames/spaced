package se.spaced.server.persistence.migrator;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.io.FileStreamLocator;
import se.fearless.common.io.StreamLocator;
import se.fearless.common.mock.MockUtil;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.impl.inmemory.*;
import se.spaced.server.persistence.dao.interfaces.*;

import javax.inject.Singleton;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

@Singleton
public class ServerContentPopulator implements Migrator {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final StreamLocator streamLocator;
	private final SessionFactory sessionFactory;

	private final ServerXStreamUnmarshaller unmarshaller;

	@Inject
	public ServerContentPopulator(
			StreamLocator streamLocator,
			SpellDao spellDao,
			ItemTemplateDao itemTemplateDao,
			LootTemplateDao lootTemplateDao,
			CreatureTypeDao creatureTypeDao,
			MobTemplateDao mobTemplateDao,
			SessionFactory sessionFactory,
			FactionDao factionDao,
			BrainTemplateDao brainTemplateDao,
			SpawnPatternTemplateDao spawnPatternTemplateDao,
			CooldownTemplateDao cooldownTemplateDao,
			GraveyardTemplateDao graveyardTemplateDao, CurrencyDao currencyDao, AuraDao auraDao) {

		unmarshaller = new ServerXStreamUnmarshaller(spellDao,
				itemTemplateDao,
				lootTemplateDao,
				creatureTypeDao,
				mobTemplateDao,
				factionDao,
				brainTemplateDao,
				spawnPatternTemplateDao,
				cooldownTemplateDao,
				graveyardTemplateDao,
				currencyDao, auraDao);

		this.sessionFactory = sessionFactory;
		this.streamLocator = streamLocator;
	}


	@Override
	public void execute() {
		Transaction transaction = null;

		try {
			transaction = sessionFactory.getCurrentSession().beginTransaction();

			importFromXml("auras.xml", unmarshaller.getAuraDao());
			importFromXml("cooldowns.xml", unmarshaller.getCooldownTemplateDao());
			importFromXml("creaturetypes.xml", unmarshaller.getCreatureTypeDao());
			importFromXml("currencies.xml", unmarshaller.getCurrencyDao());
			importFromXml("factions.xml", unmarshaller.getFactionDao());
			importFromXml("spells.xml", unmarshaller.getSpellDao());
			importFromXml("itemtemplates.xml", unmarshaller.getItemTemplateDao());
			importFromXml("loottemplates.xml", unmarshaller.getLootTemplateDao());
			importFromXml("salvagetemplates.xml", unmarshaller.getLootTemplateDao());
			importFromXml("brains.xml", unmarshaller.getBrainTemplateDao());
			importFromXml("mobtemplates.xml", unmarshaller.getMobTemplateDao());
			importFromXml("spawnpatterns.xml", unmarshaller.getSpawnPatternTemplateDao());
			importFromXml("graveyards.xml", unmarshaller.getGraveyardTemplateDao());
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw new RuntimeException(e);
		}

	}

	// TODO: fix so Dao can be declared with Dao<? extends ExternalPersistableBase> here
	private void importFromXml(String fileName, Dao dao) {

		try {

			Supplier<? extends InputStream> supplier = streamLocator.getInputStreamSupplier(fileName);

			List<ExternalPersistableBase> list = (List<ExternalPersistableBase>) unmarshaller.getXStream().fromXML(supplier.get());

			for (ExternalPersistableBase persistable : list) {
				//log.debug("Persisted: {}", persistable);
				dao.persist(persistable);
			}

			log.info("Successful import of " + fileName);
		} catch (Exception e) {
			throw new RuntimeException("Import failed for " + fileName, e);
		}
	}


	public static void main(String[] args) {

		SessionFactory factory = MockUtil.deepMock(SessionFactory.class);
		StreamLocator locator = new FileStreamLocator(new File("server/src/main/resources"));

		ServerContentPopulator pop = new ServerContentPopulator(locator,
				new InMemorySpellDao(),
				new InMemoryItemTemplateDao(),
				new InMemoryLootTemplateDao(),
				new InMemoryCreatureTypeDao(),
				new InMemoryMobTemplateDao(),
				factory,
				new InMemoryFactionDao(),
				new InMemoryBrainTemplateDao(),
				new InMemorySpawnPatternTemplateDao(),
				new InMemoryCooldownTemplateDao(),
				new InMemoryGraveyardTemplateDao(),
				new InMemoryCurrencyDao(),
				new InMemoryAuraDao());
		pop.execute();
	}
}
