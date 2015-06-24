package se.ardorgui.view.util;

import com.ardor3d.image.Texture;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.SpatialController;
import com.ardor3d.util.TextureKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.view.animation.SpatialAnimator;

import java.util.HashMap;

public class ResourceDisposer {
	private static final Logger log = LoggerFactory.getLogger(ResourceDisposer.class);

	private static final HashMap<TextureKey, Integer> textureRefCount = new HashMap<TextureKey, Integer>();

	public static void disposeSpatial(Spatial spatial, boolean freeTextures) {
		if (spatial == null) {
			return;
		}

		disposeSpatialNoRemove(spatial, freeTextures);

		//Remove from scenegraph
		spatial.removeFromParent();
	}

	public static void disposeSpatialNoRemove(Spatial spatial, boolean freeTextures) {
		if (spatial == null) {
			return;
		}

		if (spatial instanceof Mesh) {
			disposeGeometry((Mesh) spatial);
		} else if (spatial instanceof Node) {
			disposeNode((Node) spatial);
		}

		//Clear all renderstates
		clearRenderStates(spatial, freeTextures);

		for (SpatialController<?> controller : spatial.getControllers()) {
			if (controller instanceof SpatialAnimator) {
				((SpatialAnimator) controller).abort();
			}
		}
		spatial.clearControllers();
	}

	public static void clearRenderStates(Spatial spatial, boolean removeFromCache) {
		if (spatial == null) {
			return;
		}

		for (StateType stateType : StateType.values()) {
			RenderState rs = spatial.getLocalRenderState(stateType);

			boolean remove = true;
//			if (spatial instanceof JmeComponent && !((JmeComponent) spatial).isReleasable()) {
//				remove = false;
//			}
			if (rs instanceof TextureState && remove) {
				releaseTextureState((TextureState) rs, removeFromCache);
			}

			spatial.clearRenderState(stateType);
		}
	}

	public static void disposeNodeRecursive(Node node, boolean freeTextures) {
		if (node == null) {
			return;
		}

		if (node.getChildren() != null) {
			for (Spatial spatial : node.getChildren()) {
				if (spatial instanceof Node) {
					disposeNodeRecursive((Node) spatial, freeTextures);
				} else {
					disposeSpatialNoRemove(spatial, freeTextures);
				}
			}
		}

		for (SpatialController<?> controller : node.getControllers()) {
			if (controller instanceof SpatialAnimator) {
				((SpatialAnimator) controller).abort();
			}
		}

		node.detachAllChildren();
	}

	private static void disposeGeometry(Mesh geom) {
		if (geom == null) {
			return;
		}
	}

	private static void disposeNode(Node node) {
	}

	private static void countUsedTextures(TextureState ts) {
		if (ts != null) {
			int nTextures = ts.getNumberOfSetTextures();
			for (int j = 0; j < nTextures; j++) {
				Texture texture = ts.getTexture(j);
				if (texture != null) {
					addTextureUse(texture);
				}
			}
		}
	}

	public static void countUsedTextures(Node root) {
		if (root.getChildren() != null) {
			for (Spatial spatial : root.getChildren()) {
				if (spatial instanceof Node) {
					countUsedTextures((Node) spatial);
				} else {
					countUsedTextures((TextureState) spatial.getLocalRenderState(StateType.Texture));
				}
			}
		}
	}

	public static Texture addTextureUse(Texture texture) {
		TextureKey tKey = texture.getTextureKey();
		if (!textureRefCount.containsKey(tKey)) {
			textureRefCount.put(tKey, 1);
		} else {
			textureRefCount.put(tKey, textureRefCount.get(tKey) + 1);
		}
		return texture;
	}

	public static void releaseTexture(Texture texture, TextureState textureState, boolean removeFromCache) {
		if (texture == null) {
			log.warn("Tried to free a null texture");
			return;
		}

		TextureKey tKey = texture.getTextureKey();
		if (!textureRefCount.containsKey(tKey)) {
			if (removeFromCache) {
//				TextureManager.releaseTexture(tKey);
			}
			return;
		}

		int refCount = textureRefCount.get(tKey);
		refCount--;
		if (refCount <= 0) {
//			textureState.deleteTextureId(texture.getTextureId());
			textureState.removeTexture(texture);
			if (removeFromCache) {
//				TextureManager.releaseTexture(tKey);
			}
			textureRefCount.remove(tKey);
		} else {
			textureRefCount.put(tKey, refCount);
		}
	}

	public static void releaseTextureState(TextureState textureState, boolean removeFromCache) {
		int nTextures = textureState.getNumberOfSetTextures();
		for (int j = 0; j < nTextures; j++) {
			Texture texture = textureState.getTexture(j);
			if (texture != null) {
				releaseTexture(texture, textureState, removeFromCache);
			}
		}
	}

}
