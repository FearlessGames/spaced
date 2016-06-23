package se.spaced.shared.world.terrain;

import org.junit.Test;

import java.io.*;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.when;

public class RawDataHeightMapFileFormatTest {

	private static final double EPSILON = 1e-4;

	@Test
	public void writeSimple() throws Exception {
		RawDataHeightMapExporter exporter = new RawDataHeightMapExporter();
		HeightMap map = HeightMap.fromArray(4, 100, 100, new double[] {
				0.0, 0.5, 0.9, 1.0,
				0.0, 0.2, 0.6, 0.9,
				0.8, 0.1, 0.3, 0.7,
				0.5, 0.1, 0.2, 0.6
		});
		ByteArrayOutputStream out = new ByteArrayOutputStream(100);
		exporter.export(map, out);
		byte[] bytes = out.toByteArray();

		assertEquals(36, bytes.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);

		// Header marker
		assertEquals(RawDataHeightMapExporter.HEADER_MARKER, dis.readUnsignedShort());

		// Number of data points
		assertEquals(4, dis.readUnsignedShort());

		assertEquals(0, dis.readUnsignedShort());
		assertEquals(32767, dis.readUnsignedShort());
		assertEquals(58981,  dis.readUnsignedShort());
		assertEquals(65534, dis.readUnsignedShort());

		assertEquals(0,  dis.readUnsignedShort());
		assertEquals(13107, dis.readUnsignedShort());
		assertEquals(39320, dis.readUnsignedShort());
		assertEquals(58981, dis.readUnsignedShort());

		assertEquals(52427,  dis.readUnsignedShort());
		assertEquals(6553, dis.readUnsignedShort());
		assertEquals(19660, dis.readUnsignedShort());
		assertEquals(45874, dis.readUnsignedShort());

		assertEquals(32767,  dis.readUnsignedShort());
		assertEquals(6553, dis.readUnsignedShort());
		assertEquals(13107, dis.readUnsignedShort());
		assertEquals(39320, dis.readUnsignedShort());

	}

	@Test
	public void readSimple() throws Exception {
		final byte[] data = new byte[] {49, -105, 0, 4, 0, 0, 127, -1, -26, 101, -1, -2, 0, 0, 51, 51, -103, -104, -26, 101, -52, -53, 25, -103, 76, -52, -77, 50, 127, -1, 25, -103, 51, 51, -103, -104};
		HeightmapLoader loader = new RawHeightMapLoader(8, 1, new Supplier<InputStream>() {
			@Override
			public InputStream get() {
				return new ByteArrayInputStream(data);
			}
		});
		HeightMap map = loader.loadHeightMap();

		assertNotNull(map);

		assertEquals(4, map.getSize());

		assertEquals(0.0, map.getRawHeight(1, 1), EPSILON);
		assertEquals(0.5, map.getRawHeight(3, 1), EPSILON);
		assertEquals(0.9, map.getRawHeight(5, 1), EPSILON);
		assertEquals(1.0, map.getRawHeight(7, 1), EPSILON);

		assertEquals(0.0, map.getRawHeight(1, 3), EPSILON);
		assertEquals(0.2, map.getRawHeight(3, 3), EPSILON);
		assertEquals(0.6, map.getRawHeight(5, 3), EPSILON);
		assertEquals(0.9, map.getRawHeight(7, 3), EPSILON);

		assertEquals(0.8, map.getRawHeight(1, 5), EPSILON);
		assertEquals(0.1, map.getRawHeight(3, 5), EPSILON);
		assertEquals(0.3, map.getRawHeight(5, 5), EPSILON);
		assertEquals(0.7, map.getRawHeight(7, 5), EPSILON);

		assertEquals(0.5, map.getRawHeight(1, 7), EPSILON);
		assertEquals(0.1, map.getRawHeight(3, 7), EPSILON);
		assertEquals(0.2, map.getRawHeight(5, 7), EPSILON);
		assertEquals(0.6, map.getRawHeight(7, 7), EPSILON);
	}

	@Test
	public void failToRead() throws Exception {
		HeightmapLoader loader = new RawHeightMapLoader(8, 1, new Supplier<InputStream>() {
			@Override
			public InputStream get() {

				InputStream is = mock(InputStream.class);
				try {
					when(is.read()).thenReturn(49, 151, 0, 8, 1, 2, 3).thenThrow(new IOException("Fail"));
				} catch (IOException e) {
					fail("This should not be possible");
				}
				return is;
			}
		});
		try {
			loader.loadHeightMap();
			fail();
		} catch (HeightMapLoadException e) {
			
		}
	}

	@Test
	public void badHeaderMarker() throws Exception {
		final byte[] data = new byte[] {49, 31, 0, 4, 0, 0, 127, -1, -26, 101, -1, -2, 0, 0, 51, 51, -103, -104, -26, 101, -52, -53, 25, -103, 76, -52, -77, 50, 127, -1, 25, -103, 51, 51, -103, -104};
		HeightmapLoader loader = new RawHeightMapLoader(8, 1, new Supplier<InputStream>() {
			@Override
			public InputStream get() {
				return new ByteArrayInputStream(data);
			}
		});
		try {
			loader.loadHeightMap();
			fail();
		} catch (HeightMapLoadException e) {
			
		}
	}
}
