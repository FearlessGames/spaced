package se.spaced.server.persistence.migrator;

import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshaller;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.internal.PersistentSortedMap;
import org.hibernate.collection.internal.PersistentSortedSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Singleton
public final class ExportServerContent extends SpacedXStreamBase {
	@Inject
	public ExportServerContent(
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
				cooldownTemplateDao, graveyardTemplateDao, currencyDao, auraDao);

	}

	@Override
	protected void setupXStream() {
		xStream.setMarshallingStrategy(new XStreamMarshallingStrategy(XStreamMarshallingStrategy.ABSOLUTE));
		xStream.registerConverter(new HibernateCollectionConverter(xStream.getConverterLookup()));
		xStream.addDefaultImplementation(PersistentBag.class, List.class);
		xStream.addDefaultImplementation(PersistentList.class, List.class);
		xStream.addDefaultImplementation(PersistentMap.class, Map.class);
		xStream.addDefaultImplementation(PersistentSet.class, Set.class);
		xStream.addDefaultImplementation(PersistentSortedMap.class, TreeMap.class);
		xStream.addDefaultImplementation(PersistentSortedSet.class, TreeSet.class);
		xStream.setMode(XStream.NO_REFERENCES);
	}

	public String genereateXmlFor(List<?> items) {
		return xStream.toXML(items);
	}


	public static class XStreamMarshallingStrategy extends ReferenceByXPathMarshallingStrategy {
		public XStreamMarshallingStrategy(int mode) {
			super(mode);
		}

		@Override
		protected TreeMarshaller createMarshallingContext(
				HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
			return new HibernateProxyXPathMarshaller(writer, converterLookup, mapper, RELATIVE);
		}
	}

	public static class HibernateProxyXPathMarshaller extends ReferenceByXPathMarshaller {

		public HibernateProxyXPathMarshaller(
				HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper, int mode) {
			super(writer, converterLookup, mapper, mode);

		}

		@Override
		public void convertAnother(Object item, Converter converter) {
			Object toConvert;
			if (HibernateProxy.class.isAssignableFrom(item.getClass())) {
				toConvert = ((HibernateProxy) item).getHibernateLazyInitializer().getImplementation();
			} else {
				toConvert = item;
			}
			super.convertAnother(toConvert, converter);
		}


	}

	public static class HibernateCollectionConverter implements Converter {
		private final Converter listSetConverter;
		private final Converter mapConverter;
		private final Converter treeMapConverter;
		private final Converter treeSetConverter;
		private final Converter defaultConverter;

		public HibernateCollectionConverter(ConverterLookup converterLookup) {
			listSetConverter = converterLookup.lookupConverterForType(ArrayList.class);
			mapConverter = converterLookup.lookupConverterForType(HashMap.class);
			treeMapConverter = converterLookup.lookupConverterForType(TreeMap.class);
			treeSetConverter = converterLookup.lookupConverterForType(TreeSet.class);
			defaultConverter = converterLookup.lookupConverterForType(Object.class);
		}


		@Override
		public boolean canConvert(Class type) {
			return PersistentCollection.class.isAssignableFrom(type);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			Object collection = source;

			if (source instanceof PersistentCollection) {
				PersistentCollection col = (PersistentCollection) source;
				col.forceInitialization();

				collection = col.getStoredSnapshot();
			}

			// the set is returned as a map by Hibernate (unclear why exactly)
			if (source instanceof PersistentSet) {

				collection = new HashSet(((HashMap) collection).values());
			}

			// delegate the collection to the approapriate converter
			if (listSetConverter.canConvert(collection.getClass())) {
				listSetConverter.marshal(collection, writer, context);
				return;
			}
			if (mapConverter.canConvert(collection.getClass())) {
				mapConverter.marshal(collection, writer, context);
				return;
			}
			if (treeMapConverter.canConvert(collection.getClass())) {
				treeMapConverter.marshal(collection, writer, context);
				return;
			}
			if (treeSetConverter.canConvert(collection.getClass())) {
				treeSetConverter.marshal(collection, writer, context);
				return;
			}

			defaultConverter.marshal(collection, writer, context);
		}


		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return null;
		}
	}
}
