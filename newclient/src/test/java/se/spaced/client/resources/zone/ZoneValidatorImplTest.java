package se.spaced.client.resources.zone;

import com.google.common.collect.Iterators;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.fearless.common.io.IOLocator;
import se.mockachino.annotations.Mock;
import se.spaced.client.model.Prop;
import se.spaced.shared.resources.zone.Zone;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;


public class ZoneValidatorImplTest {
	private ZoneValidatorImpl test;

	@Mock
	File file;
	@Mock
	File file2;
	@Mock
	private ByteSource byteSource;
	@Mock
	private ByteArrayInputStream bais;

	@Before
	public void setup() {
		setupMocks(this);
		test = new ZoneValidatorImpl(streamLocator);
	}

	@Test
	public void testValidatingPropOk() throws IOException {
		Prop p = new Prop("brajja.xmo", null, null, null);
		stubReturn(bais).on(byteSource).openStream();
		assertTrue(test.validateProp(p));
	}

	@Test
	public void testValidatingPropNotFound() throws IOException {
		Prop p = new Prop("p1.xmo", null, null, null);
		stubThrow(new IOException("CANNOT FIND YOUWWWW")).on(byteSource).openStream();
		assertFalse(test.validateProp(p));
	}

	@Test
	public void validateZoneOk() throws IOException {
		Zone z = new Zone("DarkForest", new Sphere(new SpacedVector3(100, 200, 300), 500));
		Prop p1 = new Prop("p1.xmo", null, null, null);
		Prop p2 = new Prop("p2.xmo", null, null, null);
		z.addProp(p1);
		z.addProp(p2);
		stubReturn(bais).on(byteSource).openStream();
		assertTrue(test.validateZone(z));
	}

	@Test
	public void validateZoneNok() throws IOException {
		Zone z = new Zone("DarkForest", new Sphere(new SpacedVector3(100, 200, 300), 500));
		Prop p1 = new Prop("p1.xmo", null, null, null);
		Prop p2 = new Prop("p2.xmo", null, null, null);
		z.addProp(p1);
		z.addProp(p2);
		stubThrow(new IOException("CANNOT FIND YOUWWWW")).on(byteSource).openStream();
		assertFalse(test.validateZone(z));
	}


	IOLocator streamLocator = new IOLocator() {
		@Override
		public ByteSink getByteSink(String s) {
			return null;
		}

		@Override
		public ByteSource getByteSource(String key) {
			return byteSource;
		}


		@Override
		public Iterator<String> listKeys() {
			return Iterators.emptyIterator();
		}
	};
}
