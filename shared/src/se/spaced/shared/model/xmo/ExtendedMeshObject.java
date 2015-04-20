package se.spaced.shared.model.xmo;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;


public class ExtendedMeshObject {
	private String colladaFile;
	private String textureFile;
	private String physicsFile;
	private String xmoMaterialFile;
	private Vector3 position = new Vector3();
	private Quaternion rotation = new Quaternion();
	private Vector3 scale = new Vector3(1, 1, 1);

	public String getPhysicsFile() {
		return physicsFile;
	}

	public void setPhysicsFile(String physicsFile) {
		this.physicsFile = physicsFile;
	}

	public String getTextureFile() {
		return textureFile;
	}

	public void setTextureFile(String textureFile) {
		this.textureFile = textureFile;
	}


	public String getXmoMaterialFile() {
		return xmoMaterialFile;
	}

	public void setXmoMaterialFile(String xmoMaterialFile) {
		this.xmoMaterialFile = xmoMaterialFile;
	}

	public String getColladaFile() {
		return colladaFile;
	}

	public void setColladaFile(String colladaFile) {
		this.colladaFile = colladaFile;
	}

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

	@Override
	public String toString() {
		return colladaFile;
	}
}
