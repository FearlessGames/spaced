package se.spaced.shared.model.xmo;

import com.ardor3d.extension.animation.skeletal.clip.AnimationClip;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.extension.model.collada.jdom.data.SkinData;
import com.ardor3d.scenegraph.Node;

public class ColladaContents {
	private final ColladaStorage colladaStorage;

	public ColladaContents(ColladaStorage colladaStorage) {
		this.colladaStorage = colladaStorage;
	}

	public Node getScene() {
		if (colladaStorage == null) {
			return null;
		}

		return colladaStorage.getScene();
	}

	public SkinData getSkin() {
		if (colladaStorage == null) {
			return null;
		}

		if (!colladaStorage.getSkins().isEmpty()) {
			return colladaStorage.getSkins().get(0);
		}
		return null;
	}

	public ColladaStorage getColladaStorage() {
		return colladaStorage;
	}

	public AnimationClip getAnimationClip(String name) {
		if (colladaStorage == null) {
			return null;
		}
		return colladaStorage.extractChannelsAsClip(name);
	}
}
