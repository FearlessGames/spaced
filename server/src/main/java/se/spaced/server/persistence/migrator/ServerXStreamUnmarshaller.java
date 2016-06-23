package se.spaced.server.persistence.migrator;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.XStream;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.cooldown.CooldownSetTemplate;
import se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.interfaces.*;
import se.spaced.server.persistence.migrator.converters.DefaultField;
import se.spaced.server.persistence.migrator.converters.PostMarshallerConverter;
import se.spaced.server.persistence.migrator.converters.PostUnmarshal;
import se.spaced.server.persistence.migrator.converters.ReferenceOrDefinitionConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerXStreamUnmarshaller extends SpacedXStreamBase {
	public ServerXStreamUnmarshaller(
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
		super(
				spellDao,
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

	}

	@Override
	protected void setupXStream() {
		cachingBaseConverter.addSubConveter(ReferenceOrDefinitionConverter.create(spellDao,
				baseConverter,
				namedObjectCache,
				ServerSpell.class,
				new ServerSpellPostMarshaler()));

		Supplier<AtomicInteger> atomicIntSupplier = new Supplier<AtomicInteger>() {
			@Override
			public AtomicInteger get() {
				return new AtomicInteger(0);
			}
		};
		xStream.registerConverter(new PostMarshallerConverter<SinglePointSpawnArea>(SinglePointSpawnArea.class,
				baseConverter, new DefaultField("spawnCount", atomicIntSupplier)));

		xStream.registerConverter(new PostMarshallerConverter<PolygonSpaceSpawnArea>(PolygonSpaceSpawnArea.class,
				baseConverter, new DefaultField("spawnCount", atomicIntSupplier)));

		xStream.registerConverter(new PostMarshallerConverter<PersistedAppearanceData>(PersistedAppearanceData.class,
				baseConverter,
				new DefaultField("scale", new Supplier<SpacedVector3>() {
					@Override
					public SpacedVector3 get() {
						return new SpacedVector3(1, 1, 1);
					}
				})));


		xStream.addDefaultImplementation(ArrayList.class, Collection.class);

	}

	public XStream getXStream() {
		return xStream;
	}

	private static class ServerSpellPostMarshaler implements PostUnmarshal {
		@Override
		public void postUnmarshal(Object unmarshaledObject) {
			new DefaultField("cooldowns", new Supplier<Object>() {
				@Override
				public Object get() {
					return new CooldownSetTemplate();
				}
			}).postUnmarshal(unmarshaledObject);
			new DefaultField("requiredAuras", new Supplier<Object>() {
				@Override
				public Object get() {
					return Sets.newHashSet();
				}
			}).postUnmarshal(unmarshaledObject);
			ServerSpell spell = (ServerSpell) unmarshaledObject;
			if (spell.getRanges().size() <= 0) {
				throw new IllegalStateException("Range of spell must be > 0: " + spell.getPk());
			}
		}
	}

}
