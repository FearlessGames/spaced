package se.spaced.shared.model.xmo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import se.ardortech.math.SpacedVector3;

import java.util.List;
import java.util.Map;

public class XmoRoot {
	private String name;
	private SpacedVector3 size = SpacedVector3.ZERO;
	private List<ExtendedMeshObject> extendedMeshObjects = Lists.newArrayList();
	private List<XmoContainerNode> containerNodes = Lists.newArrayList();
	private final Map<String, MetaNode> metaNodes = Maps.newHashMap();
	private final Map<AttachmentPointIdentifier, XmoAttachmentPoint> attachmentPoints = Maps.newHashMap();
	private AnimationData animationData;
	private String walkmeshFile;

	public Map<AttachmentPointIdentifier, XmoAttachmentPoint> getXmoAttachmentPoints() {
		return attachmentPoints;
	}

	public Map<String, MetaNode> getMetaNodes() {
		return metaNodes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ExtendedMeshObject> getExtendedMeshObjects() {
		return extendedMeshObjects;
	}

	public void setExtendedMeshObjects(List<ExtendedMeshObject> extendedMeshObjects) {
		this.extendedMeshObjects = extendedMeshObjects;
	}

	public SpacedVector3 getSize() {
		return size;
	}

	public void setSize(SpacedVector3 size) {
		this.size = size;
	}

	public void addChild(ExtendedMeshObject child) {
		extendedMeshObjects.add(child);
	}

	public List<XmoContainerNode> getContainerNodes() {
		return containerNodes;
	}

	public void setContainerNodes(List<XmoContainerNode> containerNodes) {
		this.containerNodes = containerNodes;
	}


	public AnimationData getAnimationData() {
		return animationData;
	}

	public void setAnimationData(AnimationData animationData) {
		this.animationData = animationData;
	}

	public String getWalkmeshFile() {
		return walkmeshFile;
	}
}
