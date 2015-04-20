package se.spaced.spacedit.ardor;

import com.ardor3d.light.DirectionalLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.state.FogState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Sphere;

public class Sun {
	final DirectionalLight light = new DirectionalLight();
	final Quaternion q = new Quaternion(Quaternion.IDENTITY);
	final Vector3 axis = new Vector3(Vector3.UNIT_X);
	final Vector3 pos = new Vector3(Vector3.UNIT_Y);
	final Vector3 transformedPos = new Vector3(Vector3.UNIT_Y);
	double dayTime = 0;
	double daySpeed = 0;

	Node sunModelNode = new Node();
	private double sunModelDistance = 4000;

	public Sun() {
		light.setDiffuse(new ColorRGBA(0.82f, 0.77f, 0.68f, 0.94f));
		light.setAmbient(new ColorRGBA(0.39f, 0.32f, 0.59f, 0.78f));
		light.setDirection(new Vector3(0.f, 1.f, 0.f));
		light.setEnabled(true);

		setupBall();
	}

	private void setupBall() {
		final Sphere sunBall = new Sphere("The Sun", 15, 15, 250);
		final MaterialState material = new MaterialState();
		material.setEmissive(new ColorRGBA(1.0f, 0.98f, 0.6f, 1f));
		final FogState fog = new FogState();
		fog.setEnabled(false);
		sunModelNode.attachChild(sunBall);
		sunModelNode.setRenderState(fog);
		sunModelNode.setRenderState(material);
	}

	public void setPosition(Vector3 p) {
		pos.set(p);
	}

	public void setColors(ColorRGBA diffuse) {
		light.setDiffuse(diffuse);
	}

	public void setAmbient(ColorRGBA ambient) {
		light.setAmbient(ambient);
	}

	public void setAxis(ReadOnlyVector3 axis) {
		this.axis.set(axis).normalizeLocal();
	}

	public void setDaySpeed(double speed) {
		daySpeed = speed;
	}

	public Node getNode() {
		return sunModelNode;
	}

	DirectionalLight getLight() {
		return light;
	}

	public void update(double dt, Camera camera) {
		dayTime += dt * daySpeed;
		q.fromAngleAxis(dayTime, axis).apply(pos, transformedPos);
		light.setDirection(transformedPos.normalizeLocal());

		sunModelNode.setTranslation(transformedPos.multiplyLocal(sunModelDistance).addLocal(camera.getLocation()));
	}
}