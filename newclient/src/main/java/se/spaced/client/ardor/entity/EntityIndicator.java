package se.spaced.client.ardor.entity;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import se.ardortech.math.AABox;
import se.ardortech.math.Box;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Vectors;
import se.ardortech.meshgenerator.MeshUtil;
import se.ardortech.meshgenerator.shapes.PanelMeshDataGenerator;
import se.spaced.client.core.states.Updatable;
import se.spaced.client.view.entity.VisualEntities;
import se.spaced.client.view.entity.VisualEntity;

public class EntityIndicator implements Updatable {
	private final Mesh indicatorMesh;
	private final PanelMeshDataGenerator generator;
	private final Camera camera;
	private final Vector3 center = new Vector3();

	private VisualEntity targetedEntity = VisualEntities.EMPTY_ENTITY;
	private SpacedVector3 offset = SpacedVector3.ZERO;
	private SpacedVector3 size = SpacedVector3.ZERO;

	public EntityIndicator(Mesh mesh, Camera camera, PanelMeshDataGenerator generator) {
		this.indicatorMesh = mesh;
		this.camera = camera;
		this.generator = generator;
	}

	public void show(final VisualEntity entity, final Node entityNode, final ReadOnlyColorRGBA color) {
		targetedEntity = entity;
		offset = entity.getMetaNodePosition("selection");
		size = entity.getSize();
		indicatorMesh.setDefaultColor(color);

		update(0);

		entityNode.attachChild(indicatorMesh);
	}

	@Override
	public void update(double timePerFrame) {
		targetedEntity.getNode().getWorldTransform().applyForward(offset, center);

		// TODO: Use a cylinder instead of a Box
		SpacedVector3 v1 = Vectors.fromArdor(camera.getScreenCoordinates(new Vector3(center.getX() - size.getX(),
				center.getY() - size.getY(),
				center.getZ() - size.getZ())));
		Box box = new AABox(v1, v1);
		box.expand(Vectors.fromArdor(camera.getScreenCoordinates(new Vector3(center.getX() - size.getX(),
				center.getY() - size.getY(),
				center.getZ() + size.getZ()))));
		box.expand(Vectors.fromArdor(camera.getScreenCoordinates(new Vector3(center.getX() - size.getX(),
				center.getY() + size.getY(),
				center.getZ() - size.getZ()))));
		box.expand(Vectors.fromArdor(camera.getScreenCoordinates(new Vector3(center.getX() - size.getX(),
				center.getY() + size.getY(),
				center.getZ() + size.getZ()))));
		box.expand(Vectors.fromArdor(camera.getScreenCoordinates(new Vector3(center.getX() + size.getX(),
				center.getY() - size.getY(),
				center.getZ() - size.getZ()))));
		box.expand(Vectors.fromArdor(camera.getScreenCoordinates(new Vector3(center.getX() + size.getX(),
				center.getY() - size.getY(),
				center.getZ() + size.getZ()))));
		box.expand(Vectors.fromArdor(camera.getScreenCoordinates(new Vector3(center.getX() + size.getX(),
				center.getY() + size.getY(),
				center.getZ() - size.getZ()))));
		box.expand(Vectors.fromArdor(camera.getScreenCoordinates(new Vector3(center.getX() + size.getX(),
				center.getY() + size.getY(),
				center.getZ() + size.getZ()))));

		generator.setSize((int) box.getSize().getX() + generator.getMinWidth(),
				(int) box.getSize().getY() + generator.getMinHeight());
		MeshUtil.rewind(indicatorMesh.getMeshData());
		generator.getData(indicatorMesh.getMeshData());
		indicatorMesh.setTranslation(box.getCenter());
	}

	public void hide() {
		indicatorMesh.removeFromParent();
	}
}