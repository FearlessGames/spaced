package se.spaced.client.environment.components;

import com.ardor3d.light.DirectionalLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.state.FogState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Sphere;
import com.google.inject.Inject;
import se.spaced.client.environment.settings.SunSetting;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.environment.time.GameTimeManager;

public class Sun {
	private final DirectionalLight light = new DirectionalLight();
	private final Quaternion q = new Quaternion(Quaternion.IDENTITY);
	private final Vector3 axis = new Vector3(Vector3.UNIT_X);
	private final Vector3 pos = new Vector3(Vector3.UNIT_Y);
	private final Vector3 transformedPos = new Vector3(Vector3.UNIT_Y);

	private final Node sunModelNode = new Node();
	private final double sunModelDistance = 9000;
	private final GameTimeManager gameTimeManager;

	@Inject
	public Sun(ReadOnlyColorRGBA diffuseLight, ReadOnlyColorRGBA ambient, ReadOnlyVector3 direction, GameTimeManager gameTimeManager) {
		this.gameTimeManager = gameTimeManager;
		light.setDiffuse(diffuseLight);
		light.setAmbient(ambient);
		light.setDirection(direction);
		light.setEnabled(true);
		setupBall();
	}

	public ReadOnlyColorRGBA getCurrentDiffuse() {
		return light.getDiffuse();
	}

	public ReadOnlyColorRGBA getCurrentAmbient() {
		return light.getAmbient();
	}

	public ReadOnlyColorRGBA getCurrentEmissive() {
		MaterialState ms = (MaterialState) sunModelNode.getLocalRenderState(RenderState.StateType.Material);
		return ms.getEmissive();
	}


	private void setupBall() {
		final Sphere sunBall = new Sphere("The Sun", 25, 35, 700);
		final MaterialState material = new MaterialState();
		material.setEmissive(new ColorRGBA(1.0f, 0.98f, 0.6f, 1f));
		sunModelNode.attachChild(sunBall);
		FogState fogState = new FogState();
		fogState.setEnabled(false);
		sunModelNode.setRenderState(fogState);
		sunModelNode.setRenderState(material);
	}

	public void setCurrentSettings(SunSetting settings) {
		this.setDiffuse(settings.getDiffuseColor());
		this.setAmbient(settings.getAmbientColor());
		this.setEmissive(settings.getEmissiveColor());
	}


	public void setPosition(Vector3 p) {
		pos.set(p);
	}

	public void setDiffuse(ColorRGBA diffuse) {
		light.setDiffuse(diffuse);
	}

	public void setAmbient(ColorRGBA ambient) {
		light.setAmbient(ambient);
	}

	public void setEmissive(ColorRGBA emissiveColor) {
		MaterialState ms = (MaterialState) sunModelNode.getLocalRenderState(RenderState.StateType.Material);
		ms.setEmissive(emissiveColor);
		ms.needsRefresh();
	}

	public void setAxis(ReadOnlyVector3 axis) {
		this.axis.set(axis).normalizeLocal();
	}

	public Node getNode() {
		return sunModelNode;
	}

	public DirectionalLight getLight() {
		return light;
	}

	public void init(Node root) {
		root.attachChild(getNode());
		LightState lightState = new LightState();
		lightState.setEnabled(true);
		lightState.attach(getLight());
		root.setRenderState(lightState);
	}

	public void update(Camera camera, GameTime t) {
		double dayTime = gameTimeManager.getDayFraction(t);
		q.fromAngleAxis(dayTime * Math.PI * 2, axis).apply(pos, transformedPos);
		light.setDirection(transformedPos.normalizeLocal().multiplyLocal(-1));
		sunModelNode.setTranslation(transformedPos.multiplyLocal(-sunModelDistance).addLocal(camera.getLocation()));
	}
}
