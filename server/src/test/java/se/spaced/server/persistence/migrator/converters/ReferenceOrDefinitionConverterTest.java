package se.spaced.server.persistence.migrator.converters;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.impl.inmemory.FindableInMemoryDao;
import se.spaced.server.persistence.dao.interfaces.FindableDao;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;
import se.spaced.shared.xml.UUIDConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ReferenceOrDefinitionConverterTest {

	private ReferenceOrDefinitionConverter<ReferenceData> converter;
	private Converter baseConverter;
	private FindableDao<ReferenceData> dao;
	private XStream xStream;
	private UUIDFactory uuidFactory;
	private NamedObjectCache<String> namedObjectCache;

	@Before
	public void setUp() throws Exception {

		uuidFactory = new UUIDMockFactory();
		dao = new FindableInMemoryDao<ReferenceData>(uuidFactory);
		xStream = new XStream(new DomDriver());
		baseConverter = xStream.getConverterLookup().lookupConverterForType(Object.class);
		namedObjectCache = new NamedObjectCacheImpl<String>();

		converter = ReferenceOrDefinitionConverter.create(dao, baseConverter, namedObjectCache, ReferenceData.class);
		xStream.registerConverter(converter);
		xStream.registerConverter(new UUIDConverter());
		xStream.setMode(XStream.NO_REFERENCES);
		xStream.alias("refdata", ReferenceData.class);
	}

	@Test
	public void marshalWithName() throws Exception {
		ReferenceData data = new ReferenceData(uuidFactory.combUUID(), "foo");
		String asString = xStream.toXML(data);
		assertEquals("<refdata reference=\"foo\"/>", asString);
	}

	@Test
	public void marshalWithNullName() throws Exception {
		ReferenceData data = new ReferenceData(uuidFactory.combUUID(), null);
		try {
			xStream.toXML(data);
			fail();
		} catch (NullPointerException e) {
		}
	}


	@Test
	public void unmarshalWithNameAndExistsInDatabase() throws Exception {
		ReferenceData data0 = createAndStoreData("foo");
		ReferenceData data1 = createAndStoreData("foobar");
		String xml = "<refdata reference=\"foo\"/>";
		ReferenceData fromXml = (ReferenceData) xStream.fromXML(xml);
		assertEquals(data0, fromXml);
	}


	@Test
	public void unmarshalWithNoReference() throws Exception {
		String idString = "49e38175-f562-45e6-8af7-a08dc0ac211e";
		UUID id = UUID.fromString(idString);
		String xml = "<refdata><pk>" + idString + "</pk><name>foo</name></refdata>";
		ReferenceData fromXml = (ReferenceData) xStream.fromXML(xml);
		assertEquals(id, fromXml.getPk());
		assertEquals("foo", fromXml.getName());
	}

	@Test
	public void unmarshalWithNameNotInDatabase() throws Exception {
		ReferenceData data0 = new ReferenceData(uuidFactory.combUUID(), "foo");
		ReferenceData data1 = createAndStoreData("foobar");

		try {
			String xml = "<refdata reference=\"foo\"/>";
			xStream.fromXML(xml);
			fail();
		} catch (ConversionException e) {
		}
	}

	@Test
	public void unmarshalWithPkAndExistsInDatabase() throws Exception {
		ReferenceData data0 = createAndStoreData("foo");
		ReferenceData data1 = createAndStoreData("foobar");
		String xml = "<refdata reference=\"" + data0.getPk().toString() + "\"/>";
		ReferenceData fromXml = (ReferenceData) xStream.fromXML(xml);
		assertEquals(data0, fromXml);
	}


	private ReferenceData createAndStoreData(String name) {
		ReferenceData data = new ReferenceData(uuidFactory.combUUID(), name);
		dao.persist(data);
		return data;
	}


	private static class ReferenceData extends ExternalPersistableBase implements NamedPersistable {

		private final String name;

		private ReferenceData(UUID pk, String name) {
			super(pk);
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
