package se.spaced.client.tools.areacreator.impl;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Arrow;
import com.ardor3d.scenegraph.shape.Cylinder;
import com.ardor3d.scenegraph.shape.Sphere;
import org.poly2tri.Poly2Tri;
import org.poly2tri.polygon.ardor3d.ArdorPolygon;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.ardortech.math.Vectors;
import se.spaced.shared.world.AreaPoint;

import java.util.ArrayList;

public class ShapeFactory {
	private final MaterialState cylinderMaterial;
	private final MaterialState indicatorMaterial;
	private final MaterialState planeMaterial;
	private final double cylinderHeight;
	private final double cylinderRadius;
	private final SpacedVector3 UP = new SpacedVector3(0, 1, 0);

	public ShapeFactory(ReadOnlyColorRGBA cylinderColor, ReadOnlyColorRGBA indicatorColor, double cylinderHeight, double cylinderRadius) {
		this.cylinderHeight = cylinderHeight;
		this.cylinderRadius = cylinderRadius;
		cylinderMaterial = new MaterialState();
		cylinderMaterial.setAmbient(cylinderColor);
		cylinderMaterial.setEmissive(cylinderColor);

		indicatorMaterial = new MaterialState();
		indicatorMaterial.setAmbient(indicatorColor);
		indicatorMaterial.setEmissive(indicatorColor);

		planeMaterial = new MaterialState();
		planeMaterial.setAmbient(MaterialState.MaterialFace.FrontAndBack, ColorRGBA.BLUE);
		planeMaterial.setEmissive(MaterialState.MaterialFace.FrontAndBack, ColorRGBA.BLUE);

	}

	public Spatial createCylinder(AreaPoint point) {
		Cylinder cylinder = new Cylinder("Point", 8, 8, cylinderRadius, cylinderHeight, true);
		cylinder.setRotation(new SpacedRotation(new SpacedVector3(-1, 0, 0), Math.PI / 2).toQuaternion());
		cylinder.setTranslation(point.getPoint().add(cylinderHeight / 2d, UP));
		cylinder.setRenderState(cylinderMaterial);
		return cylinder;
	}

	public Spatial createConnector(AreaPoint startPoint, AreaPoint endPoint) {
		double distance = startPoint.getPoint().distance(endPoint.getPoint());
		Cylinder cylinder = new Cylinder("Bound", 8, 8, 0.1d, distance, true);
		SpacedVector3 direction = VectorMath.getDirection(startPoint.getPoint(), endPoint.getPoint());
		SpacedRotation spacedRotation = VectorMath.lookAt(direction, UP);
		cylinder.setRotation(spacedRotation.toQuaternion());
		SpacedVector3 translation = startPoint.getPoint().
				add(distance / 2d, direction).
				add(cylinderHeight / 2, UP);
		cylinder.setTranslation(translation);
		cylinder.setRenderState(cylinderMaterial);
		return cylinder;
	}

	public Spatial createArrow(AreaPoint areaPoint) {
		Node arrowNode = new Node();
		Arrow arrow = new Arrow("indicator arrow", cylinderHeight / 2d, 0.2d);
		SpacedRotation flipRotation = new SpacedRotation(new SpacedVector3(-1, 0, 0), Math.PI / 2);
		SpacedRotation finalRotation = areaPoint.getRotation().applyTo(flipRotation);

		arrow.setRotation(finalRotation.toQuaternion());
		SpacedVector3 finalLocation = areaPoint.getPoint().add(UP);
		arrow.setTranslation(finalLocation);
		Cylinder cylinder = new Cylinder("Point", 8, 8, 0.2d, cylinderHeight, true);
		cylinder.setRotation(finalRotation.toQuaternion());
		cylinder.setTranslation(finalLocation);

		arrow.setRenderState(cylinderMaterial);
		cylinder.setRenderState(cylinderMaterial);
		arrowNode.attachChild(arrow);
		arrowNode.attachChild(cylinder);
		return arrowNode;
	}


	public Spatial createPolygonPlane(Iterable<AreaPoint> areaPoints) {
		ArrayList<Vector3> points = new ArrayList<Vector3>();
		for (AreaPoint areaPoint : areaPoints) {
			Vector3 point = Vectors.fromSpaced(areaPoint.getPoint().add(cylinderHeight / 2d, UP));
			points.add(point);
		}

		ArdorPolygon polygon = new ArdorPolygon(points);
		Mesh plane = new Mesh();


		Poly2Tri.triangulate(polygon);
		ArdorMeshMapper.updateTriangleMesh(plane, polygon.getTriangles());


		plane.setRenderState(planeMaterial);

		return plane;
	}

	public Spatial createIndicator(AreaPoint currentPoint) {
		SpacedVector3 center = currentPoint.getPoint().add(cylinderHeight + 0.4d, UP);
		Sphere sphere = new Sphere("indicator", center, 5, 5, 0.2d);
		sphere.setRenderState(indicatorMaterial);
		return sphere;
	}
}
