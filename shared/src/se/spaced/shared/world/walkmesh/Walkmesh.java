package se.spaced.shared.world.walkmesh;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.area.PolygonGraph;
import se.spaced.shared.xml.SharedXStreamRegistry;

import java.util.List;
import java.util.Set;

public class Walkmesh {
	private final List<Polygon> polygons = Lists.newArrayList();
	private final List<WalkmeshConnection> connections = Lists.newArrayList();

	public static void main(String[] args) {

		XStream xStream = new XStream(new DomDriver());
		xStream.setMode(XStream.NO_REFERENCES);
		SharedXStreamRegistry xStreamRegistry = new SharedXStreamRegistry();
		xStreamRegistry.registerDefaultsOn(xStream);

		Polygon floor1 = new Polygon(UUID.fromString("00578990-ead8-4d74-b016-a01302c0dcfe"),
				Lists.newArrayList(
						new SpacedVector3(0, 0, 0),
						new SpacedVector3(0, 0, 10),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(0, 0, 0)
				)
		);

		Polygon ramp = new Polygon(UUID.fromString("63e5db53-0076-4fa3-920a-a01302c0eb95"),
				Lists.newArrayList(
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(20, 5, 10),
						new SpacedVector3(20, 5, 0),
						new SpacedVector3(10, 0, 0)
				)
		);

		Polygon floor2a = new Polygon(UUID.fromString("101ddf18-87f0-4717-a8ce-a01302c0fc2d"),
				Lists.newArrayList(
						new SpacedVector3(20, 5, 0),
						new SpacedVector3(20, 5, 10),
						new SpacedVector3(20, 5, 20),
						new SpacedVector3(30, 5, 20),
						new SpacedVector3(30, 5, 0),
						new SpacedVector3(20, 5, 0)
				
				)
		);
		
		Polygon floor2b = new Polygon(UUID.fromString("b669a0b2-fbbd-43e2-88d6-a01302c108df"),
				Lists.newArrayList(
						new SpacedVector3(20, 5, 10),
						new SpacedVector3(20, 5, 20),
						new SpacedVector3(0, 5, 10),
						new SpacedVector3(0, 5, 0),
						new SpacedVector3(10, 5, 0),
						new SpacedVector3(20, 5, 10)
				)
		);

		Walkmesh walkmesh = new Walkmesh();
		walkmesh.polygons.add(floor1);
		walkmesh.polygons.add(ramp);
		walkmesh.polygons.add(floor2a);
		walkmesh.polygons.add(floor2b);

		walkmesh.addBidirectionalConnection(floor1, ramp, new SpacedVector3(10, 0, 0), new SpacedVector3(10, 0, 10));
		walkmesh.addBidirectionalConnection(ramp, floor2a, new SpacedVector3(20, 5, 10), new SpacedVector3(20, 5, 0));
		walkmesh.addBidirectionalConnection(floor2a, floor2b, new SpacedVector3(20, 5, 10), new SpacedVector3(20, 5, 20));

		String xml = xStream.toXML(walkmesh);
		System.out.println(xml);
	}

	private void addBidirectionalConnection(
			Polygon polygon1,
			Polygon polygon2,
			SpacedVector3 point1,
			SpacedVector3 point2) {
		Preconditions.checkState(Iterables.contains(polygon1.getPoints(), point1), "Polygon1 doesn't contain " + point1);
		Preconditions.checkState(Iterables.contains(polygon1.getPoints(), point2), "Polygon1 doesn't contain " + point2);
		Preconditions.checkState(Iterables.contains(polygon2.getPoints(), point1), "Polygon2 doesn't contain " + point1);
		Preconditions.checkState(Iterables.contains(polygon2.getPoints(), point2), "Polygon2 doesn't contain " + point2);

		connections.add(new WalkmeshConnection(polygon1.getId(), polygon2.getId(), point1, point2, Direction.BIDIRECTIONAL));
	}

	public void addPolygon(Polygon polygon) {
		polygons.add(polygon);
	}

	public void addConnection(WalkmeshConnection walkmeshConnection) {
		connections.add(walkmeshConnection);
	}

	public PolygonGraph addToPolygonGraph(SpacedVector3 offset, SpacedRotation rotation, PolygonGraph polygonGraph) {
		Set<Polygon> transformedPolygons = Sets.newHashSet();
		for (Polygon polygon : polygons) {
			ImmutableList<SpacedVector3> points = polygon.getPoints();
			Polygon movedPolygon = new Polygon(polygon.getId());
			for (SpacedVector3 point : points) {
				movedPolygon.add(transformPoint(offset, rotation, point));
			}
			movedPolygon.close();
			transformedPolygons.add(movedPolygon);
			polygonGraph.addPolygon(movedPolygon);
		}
		ImmutableMap<UUID,Polygon> polygonMap = Maps.uniqueIndex(transformedPolygons,
				new Function<Polygon, UUID>() {
					@Override
					public UUID apply(Polygon polygon) {
						return polygon.getId();
					}
				});

		for (WalkmeshConnection connection : connections) {
			switch (connection.getDirection()) {
				case BIDIRECTIONAL:
					SpacedVector3 point1 = transformPoint(offset, rotation, connection.getAreaPoints().get(0).getPoint());
					SpacedVector3 point2 = transformPoint(offset, rotation, connection.getAreaPoints().get(1).getPoint());

					polygonGraph.addConnection(polygonMap.get(connection.getFrom()),
							polygonMap.get(connection.getTo()),
							point1, point2);
					polygonGraph.addConnection(polygonMap.get(connection.getTo()), polygonMap.get(connection.getFrom()),
							point2, point1);
					break;
				case UNIDIRECTIONAL:
					polygonGraph.addNaturalConnection(polygonMap.get(connection.getFrom()), polygonMap.get(connection.getTo()));
					break;
			}
		}
		return polygonGraph;
	}

	private SpacedVector3 transformPoint(SpacedVector3 offset, SpacedRotation rotation, SpacedVector3 point) {
		SpacedVector3 rotated = rotation.applyTo(point);
		return rotated.add(offset);
	}

	public ImmutableSet<Polygon> getPolygons() {
		return ImmutableSet.copyOf(polygons);
	}

	public ImmutableSet<WalkmeshConnection> getConnections() {
		return ImmutableSet.copyOf(connections);
	}
}
