package se.spaced.shared.model.xmo;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "xmoattachmentpoint")
public class XmoAttachmentPoint {
	private String jointName;
	private Vector3 offset = new Vector3();
	private Quaternion rotation = new Quaternion();
	private Vector3 scale = new Vector3(1, 1, 1);

	public String getJointName() {
		return jointName;
	}

	public void setJointName(String jointName) {
		this.jointName = jointName;
	}

	public Vector3 getOffset() {
		return offset;
	}

	public void setOffset(Vector3 offset) {
		this.offset = offset;
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
}
