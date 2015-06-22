package se.spaced.server.persistence.migrator;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.cooldown.CooldownTemplate;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplate;
import se.spaced.server.persistence.dao.interfaces.AuraDao;
import se.spaced.server.persistence.dao.interfaces.BrainTemplateDao;
import se.spaced.server.persistence.dao.interfaces.CooldownTemplateDao;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;
import se.spaced.server.persistence.dao.interfaces.CurrencyDao;
import se.spaced.server.persistence.dao.interfaces.FactionDao;
import se.spaced.server.persistence.dao.interfaces.GraveyardTemplateDao;
import se.spaced.server.persistence.dao.interfaces.ItemTemplateDao;
import se.spaced.server.persistence.dao.interfaces.LootTemplateDao;
import se.spaced.server.persistence.dao.interfaces.MobTemplateDao;
import se.spaced.server.persistence.dao.interfaces.SpawnPatternTemplateDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.server.persistence.migrator.converters.CachingBaseConverter;
import se.spaced.server.persistence.migrator.converters.DefaultField;
import se.spaced.server.persistence.migrator.converters.NamedObjectCache;
import se.spaced.server.persistence.migrator.converters.NamedObjectCacheImpl;
import se.spaced.server.persistence.migrator.converters.PostMarshallerConverter;
import se.spaced.server.persistence.migrator.converters.ReferenceOrDefinitionConverter;
import se.spaced.server.persistence.util.ServerXStreamRegistry;
import se.spaced.shared.util.math.interval.IntervalInt;

public abstract class SpacedXStreamBase {
	protected final XStream xStream;
	protected final SpellDao spellDao;
	protected final ItemTemplateDao itemTemplateDao;
	protected final LootTemplateDao lootTemplateDao;
	protected final CreatureTypeDao creatureTypeDao;
	protected final MobTemplateDao mobTemplateDao;
	protected final FactionDao factionDao;
	protected final BrainTemplateDao brainTemplateDao;
	protected final SpawnPatternTemplateDao spawnPatternTemplateDao;
	protected final CooldownTemplateDao cooldownTemplateDao;
	protected final GraveyardTemplateDao graveyardTemplateDao;
	protected final CurrencyDao currencyDao;
	protected final AuraDao auraDao;

	protected Converter baseConverter;
	protected CachingBaseConverter cachingBaseConverter;
	protected NamedObjectCache<String> namedObjectCache;

	protected SpacedXStreamBase(
			SpellDao spellDao,
			ItemTemplateDao itemTemplateDao,
			LootTemplateDao lootTemplateDao,
			CreatureTypeDao creatureTypeDao,
			MobTemplateDao mobTemplateDao,
			FactionDao factionDao,
			BrainTemplateDao brainTemplateDao,
			SpawnPatternTemplateDao spawnPatternTemplateDao,
			CooldownTemplateDao cooldownTemplateDao,
			GraveyardTemplateDao graveyardTemplateDao, CurrencyDao currencyDao, AuraDao auraDao) {

		this.spellDao = spellDao;
		this.itemTemplateDao = itemTemplateDao;
		this.lootTemplateDao = lootTemplateDao;
		this.creatureTypeDao = creatureTypeDao;
		this.mobTemplateDao = mobTemplateDao;
		this.factionDao = factionDao;
		this.brainTemplateDao = brainTemplateDao;
		this.spawnPatternTemplateDao = spawnPatternTemplateDao;
		this.cooldownTemplateDao = cooldownTemplateDao;
		this.graveyardTemplateDao = graveyardTemplateDao;
		this.currencyDao = currencyDao;
		this.auraDao = auraDao;

		this.xStream = new XStream(new DomDriver());


		baseSetupXStream();
	}

	protected abstract void setupXStream();

	private void baseSetupXStream() {
		ServerXStreamRegistry serverXStreamRegistry = new ServerXStreamRegistry();
		serverXStreamRegistry.registerDefaultsOn(xStream);

		xStream.setMode(XStream.NO_REFERENCES);

		baseConverter = xStream.getConverterLookup().lookupConverterForType(Object.class);
		namedObjectCache = new NamedObjectCacheImpl<String>();
		cachingBaseConverter = new CachingBaseConverter(baseConverter, namedObjectCache);

		xStream.registerConverter(cachingBaseConverter);

		cachingBaseConverter.addSubConveter(new HibernateProxyConverter());

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(itemTemplateDao,
						baseConverter,
						namedObjectCache,
						ServerItemTemplate.class,
						new DefaultField("sellsFor", Suppliers.ofInstance(PersistedMoney.ZERO)),
						new DefaultField("maxStackSize", Suppliers.ofInstance(1))));

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(lootTemplateDao,
						baseConverter,
						namedObjectCache, PersistableLootTemplate.class));

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(factionDao,
						baseConverter,
						namedObjectCache, PersistedFaction.class));

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(graveyardTemplateDao,
						baseConverter,
						namedObjectCache, GraveyardTemplate.class));

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(creatureTypeDao,
						baseConverter,
						namedObjectCache, PersistedCreatureType.class));

		Supplier<Boolean> trueSupplier = Suppliers.ofInstance(Boolean.TRUE);
		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(mobTemplateDao,
						baseConverter,
						namedObjectCache,
						MobTemplate.class,
						new DefaultField("moveToTarget", trueSupplier),
						new DefaultField("lookAtTarget", trueSupplier),
						new DefaultField("proximityAggroDistance", Suppliers.ofInstance(30)),
						new DefaultField("socialAggroDistance", Suppliers.ofInstance(0))));

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(brainTemplateDao,
						baseConverter, namedObjectCache, BrainTemplate.class));

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(cooldownTemplateDao,
						baseConverter,
						namedObjectCache, CooldownTemplate.class));

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(auraDao,
						baseConverter,
						namedObjectCache, ServerAura.class));

		cachingBaseConverter.addSubConveter(
				ReferenceOrDefinitionConverter.create(currencyDao,
						baseConverter,
						namedObjectCache, PersistedCurrency.class));

		Supplier<IntervalInt> emptyIntervalSupplier = new Supplier<IntervalInt>() {
			@Override
			public IntervalInt get() {
				return new IntervalInt(0, 0);
			}
		};
		cachingBaseConverter.addSubConveter(new PostMarshallerConverter<MobSpawnTemplate>(MobSpawnTemplate.class,
				baseConverter,
				new DefaultField("timePausAtPoints", emptyIntervalSupplier)));

		setupXStream();
	}

	public SpellDao getSpellDao() {
		return spellDao;
	}

	public ItemTemplateDao getItemTemplateDao() {
		return itemTemplateDao;
	}

	public LootTemplateDao getLootTemplateDao() {
		return lootTemplateDao;
	}

	public CreatureTypeDao getCreatureTypeDao() {
		return creatureTypeDao;
	}

	public MobTemplateDao getMobTemplateDao() {
		return mobTemplateDao;
	}

	public FactionDao getFactionDao() {
		return factionDao;
	}

	public BrainTemplateDao getBrainTemplateDao() {
		return brainTemplateDao;
	}

	public SpawnPatternTemplateDao getSpawnPatternTemplateDao() {
		return spawnPatternTemplateDao;
	}

	public CooldownTemplateDao getCooldownTemplateDao() {
		return cooldownTemplateDao;
	}

	public GraveyardTemplateDao getGraveyardTemplateDao() {
		return graveyardTemplateDao;
	}

	public CurrencyDao getCurrencyDao() {
		return currencyDao;
	}

	public AuraDao getAuraDao() {
		return auraDao;
	}

}
