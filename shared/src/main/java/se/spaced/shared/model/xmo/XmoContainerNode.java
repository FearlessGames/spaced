package se.spaced.shared.model.xmo;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class XmoContainerNode {
	private Vector3 position = new Vector3(0, 0, 0);
	private Quaternion rotation = new Quaternion(0, 0, 0, 0);
	private Vector3 scale = new Vector3(1, 1, 1);
	private List<ExtendedMeshObject> extendedMeshObjects = new ArrayList<ExtendedMeshObject>();
	private List<XmoContainerNode> containerNodes = new ArrayList<XmoContainerNode>();

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	public void setScale(Vector3 scale) {
		this.scale = scale;
	}

	public List<ExtendedMeshObject> getExtendedMeshObjects() {
		return extendedMeshObjects;
	}

	public void setExtendedMeshObjects(List<ExtendedMeshObject> extendedMeshObjects) {
		this.extendedMeshObjects = extendedMeshObjects;
	}

	public List<XmoContainerNode> getContainerNodes() {
		return containerNodes;
	}

	public void setContainerNodes(List<XmoContainerNode> containerNodes) {
		this.containerNodes = containerNodes;
	}


}
