package se.spaced.shared.world.terrain;

import com.google.common.collect.Table;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RawDataHeightMapExporter implements HeightMapExporter {

	public static final int HEADER_MARKER = 0x3197;

	@Override
	public void export(HeightMap heightMap, OutputStream out) throws IOException {
		int unsignedShortMax = Short.MAX_VALUE * 2;
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeShort(HEADER_MARKER);
		dos.writeShort(heightMap.getSize());
		Table<Integer,Integer,DataPoint> heightData = heightMap.getHeightData();
		for (int y = 0; y < heightMap.getSize(); y++) {
			for (int x = 0; x < heightMap.getSize(); x++) {
				double value = heightData.get(x, y).getHeight() * unsignedShortMax;
				int roundedValue = (int) Math.round(value);
				dos.writeByte((roundedValue >> 8) & 0xFF);
				dos.writeByte(roundedValue & 0xFF);
			}
		}
	}
}
