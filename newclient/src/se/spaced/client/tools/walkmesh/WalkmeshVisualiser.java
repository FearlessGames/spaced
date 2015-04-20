package se.spaced.client.tools.walkmesh;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.tools.areacreator.AreaSpatialFactory;
import se.spaced.client.tools.areacreator.impl.PolygonSpatialFactory;
import se.spaced.client.tools.areacreator.impl.ShapeFactory;
import se.spaced.shared.world.area.Gate;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.area.PolygonGraph;
import se.spaced.shared.world.walkmesh.Walkmesh;
import se.spaced.shared.world.walkmesh.WalkmeshConnection;

public class WalkmeshVisualiser {
	private final Node walkmeshRootNode;
	private final AreaSpatialFactory spatialFactory;
	private final AreaSpatialFactory connectionFactory;
	private PolygonGraph polygonGraph = new PolygonGraph();

	@Inject
	public WalkmeshVisualiser(@Named("rootNode") Node rootNode) {
		this.walkmeshRootNode = new Node("Walkmesh");
		this.spatialFactory = new PolygonSpatialFactory(new ShapeFactory(ColorRGBA.MAGENTA, ColorRGBA.PINK, 1d, 0.2));
		connectionFactory = new PolygonSpatialFactory(new ShapeFactory(ColorRGBA.BLUE, ColorRGBA.BLUE, 1.1, 0.1));
		rootNode.attachChild(walkmeshRootNode);
	}

	public void visualizeWalkmesh(Walkmesh walkmesh, SpacedVector3 translation, SpacedRotation rotation) {
		polygonGraph = new PolygonGraph();
		walkmesh.addToPolygonGraph(translation, rotation, polygonGraph);
		walkmeshRootNode.detachAllChildren();
		for (Polygon walkmeshPolygon : polygonGraph.getAllPolygons()) {
			Spatial spatial = spatialFactory.create(walkmeshPolygon);
			walkmeshRootNode.attachChild(spatial);
		}
		for (WalkmeshConnection connection : walkmesh.getConnections()) {
			Gate gate = polygonGraph.getGate(polygonGraph.getPolygon(connection.getFrom()),
					polygonGraph.getPolygon(connection.getTo()));
			Spatial spatial = connectionFactory.create(gate);
			walkmeshRootNode.attachChild(spatial);
		}
	}

	public Polygon getPolygon(SpacedVector3 point) {
		return polygonGraph.getPolygon(point);
	}
}
