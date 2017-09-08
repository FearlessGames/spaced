package se.spaced.server.tools.spawnpattern.presenter;

import com.google.inject.Inject;
import se.ardortech.math.SpacedRotation;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea;
import se.spaced.server.model.spawn.area.RandomSpaceSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.shared.world.area.Cube;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.area.SinglePoint;

import java.util.HashMap;
import java.util.Map;

public class SpawnAreaFactory {
	private final UUIDFactory uuidFactory = UUIDFactoryImpl.INSTANCE;

	private final Map<Class<?>, Factory> factoryMap;
	private final GeometryFactory geometryFactory;

	@Inject
	public SpawnAreaFactory(GeometryFactory geometryFactory) {
		this.geometryFactory = geometryFactory;


		factoryMap = new HashMap<Class<?>, Factory>();
		factoryMap.put(RandomSpaceSpawnArea.class, new RandomSpaceSpawnAreaFactory());
		factoryMap.put(SinglePointSpawnArea.class, new SinglePointSpawnAreaFactory());
		factoryMap.put(PolygonSpaceSpawnArea.class, new PolygonSpaceSpawnAreaFactory());
	}

	public SpawnArea createArea(Class<?> selectedType, String areaContent) throws SpawnAreaFactoryException {
		if (areaContent == null) {
			throw new SpawnAreaFactoryException("Xml content is null");
		}

		try {
			Factory factory = factoryMap.get(selectedType);
			if (factory == null) {
				throw new SpawnAreaFactoryException("Unknown area type");
			}
			return factory.create(areaContent);
		} catch (GeometryException e) {
			throw new SpawnAreaFactoryException(e);
		}
	}

	private interface Factory {
		SpawnArea create(String areaContent) throws GeometryException;
	}

	private class RandomSpaceSpawnAreaFactory implements Factory {
		@Override
		public SpawnArea create(String areaContent) throws GeometryException {
			Cube cube = geometryFactory.getGeometryFromContent(areaContent, Cube.class);
			return new RandomSpaceSpawnArea(uuidFactory.combUUID(), cube);
		}
	}

	private class SinglePointSpawnAreaFactory implements Factory {

		@Override
		public SpawnArea create(String areaContent) throws GeometryException {
			SinglePoint singlePoint = geometryFactory.getGeometryFromContent(areaContent, SinglePoint.class);
			return new SinglePointSpawnArea(uuidFactory.combUUID(), singlePoint);
		}
	}

	private class PolygonSpaceSpawnAreaFactory implements Factory {

		@Override
		public SpawnArea create(String areaContent) throws GeometryException {
			Polygon polygon = geometryFactory.getGeometryFromContent(areaContent, Polygon.class);
			return new PolygonSpaceSpawnArea(uuidFactory.combUUID(),
					polygon,
					new SpacedRotation(1, 2, 3, 4),
					null); //todo: get rotation from somewhere
		}
	}

}
