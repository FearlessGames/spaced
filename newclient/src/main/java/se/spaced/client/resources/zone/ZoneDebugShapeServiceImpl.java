package se.spaced.client.resources.zone;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.FogState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.spaced.shared.resources.zone.Zone;

import java.util.Map;

public class ZoneDebugShapeServiceImpl implements ZoneDebugShapeService, ZoneActivationListener {
	private boolean enabled;
	private final Map<Zone, Spatial> zones = Maps.newHashMap();
	private final Node debugNode = new Node();
	private final Node rootNode;

	private static final ColorRGBA[] DIFFUSE_LIST = new ColorRGBA[]{
			new ColorRGBA(0.1f, 0.0f, 0.4f, 0.6f),
	};
	private static final ColorRGBA[] AMBIENT_LIST = new ColorRGBA[]{
			new ColorRGBA(0.0f, 0.0f, 0.5f, 0.6f),
			new ColorRGBA(0.0f, 0.5f, 0.0f, 0.6f),
			new ColorRGBA(0.5f, 0.0f, 0.0f, 0.6f),
			new ColorRGBA(0.0f, 0.5f, 0.5f, 0.6f),
			new ColorRGBA(0.5f, 0.5f, 0.0f, 0.6f),
			new ColorRGBA(0.5f, 0.0f, 0.5f, 0.6f),
	};

	@Inject
	public ZoneDebugShapeServiceImpl(@Named("rootNode")Node rootNode) {
		this.rootNode = rootNode;

		BlendState bs = new BlendState();
		bs.setBlendEnabled(true);
		bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		bs.setDestinationFunction(BlendState.DestinationFunction.One);
		debugNode.setRenderState(bs);

		CullState cs = new CullState();
		cs.setCullFace(CullState.Face.Back);
		debugNode.setRenderState(cs);

		FogState fs = new FogState();
		fs.setDensity(0);
		debugNode.setRenderState(fs);

		debugNode.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
	}

	@Override
	public void setState(boolean enabled) {
		if (enabled == this.enabled) {
			return;
		}
		if (enabled) {
			rootNode.attachChild(debugNode);
		} else {
			rootNode.detachChild(debugNode);
		}
		this.enabled = enabled;
	}

	@Override
	public void zoneWasLoaded(Zone zone) {
		Spatial debugShape = zone.getShape().getDebugShape();
		MaterialState ms = new MaterialState();

		final int depth = zone.getDepth();
		ms.setDiffuse(DIFFUSE_LIST[(depth % DIFFUSE_LIST.length)]);
		ms.setAmbient(AMBIENT_LIST[(depth % AMBIENT_LIST.length)]);
		debugShape.setRenderState(ms);
		zones.put(zone, debugShape);
		debugNode.attachChild(debugShape);
	}

	@Override
	public void zoneWasUnloaded(Zone zone) {
		Spatial debugShape = zones.remove(zone);
		if (debugShape != null) {
			debugNode.detachChild(debugShape);
		}
	}
}
