package se.spaced.client.resources.zone;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.FogState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.fearless.common.io.IOLocator;
import se.spaced.server.navigation.NavigationMeshBuilder;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class NavigationMeshDebugShape {
	private boolean enabled;
	private Node debugNode = null;
	private final Node rootNode;

	private static final ColorRGBA DIFFUSE = new ColorRGBA(0.4f, 0.8f, 0.8f, 1.0f);
	private static final ColorRGBA AMBIENT = new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f);
	private final IOLocator locator;

	@Inject
	public NavigationMeshDebugShape(@Named("rootNode") Node rootNode, IOLocator locator) {
		this.rootNode = rootNode;
		this.locator = locator;
	}

	public void setState(boolean enabled) {
		if (enabled == this.enabled) {
			return;
		}
		if (enabled) {
			if (debugNode == null) {
				debugNode = createNode();
			}
			rootNode.attachChild(debugNode);
		} else {
			rootNode.detachChild(debugNode);
		}
		this.enabled = enabled;
	}

	private Node createNode() {
		Node node = new Node();
		try {
			Mesh mesh = NavigationMeshBuilder.buildArdorFromNavMesh(locator, "devPlanet.navmesh");
			node.attachChild(mesh);

			MaterialState ms = new MaterialState();
			ms.setDiffuse(DIFFUSE);
			ms.setAmbient(AMBIENT);
			mesh.setRenderState(ms);

		} catch (IOException e) {
			e.printStackTrace();
			return node;
		}


		BlendState bs = new BlendState();
		bs.setBlendEnabled(true);
		bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		bs.setDestinationFunction(BlendState.DestinationFunction.One);
		node.setRenderState(bs);

		CullState cs = new CullState();
		cs.setCullFace(CullState.Face.Back);
		node.setRenderState(cs);

		FogState fs = new FogState();
		fs.setDensity(0);
		node.setRenderState(fs);

		node.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);

		return node;
	}
}
