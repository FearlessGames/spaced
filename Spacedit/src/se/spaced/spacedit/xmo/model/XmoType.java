package se.spaced.spacedit.xmo.model;

public enum XmoType {
	XmoRoot,
	ExtendedMeshObject,
	XmoMetaNode,
	XmoContainerNode,
	Unknown;

	public static XmoType getType(Object object) {
		if (object == null) {
			return Unknown;
		}

		if (object instanceof NodeHolder) {
			return ((NodeHolder) object).getXmoType();
		}

		return Unknown;
	}

}
