package se.spaced.spacedit.ui.presenter.display;

import com.ardor3d.framework.Canvas;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;

// TODO: Refactor copy-paste code
public class CameraHandler {
	private static final float MOVE_SPEED = 50.0f;
	private static final double TURN_SPEED = 0.5;
	private final Matrix3 rotationMatrix = new Matrix3();

	public void turn(final Canvas canvas, final double speed) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();

		final Vector3 temp = Vector3.fetchTempInstance();
		rotationMatrix.fromAngleNormalAxis(speed * -TURN_SPEED, camera.getUp());

		rotationMatrix.applyPost(camera.getLeft(), temp);
		camera.setLeft(temp);

		rotationMatrix.applyPost(camera.getDirection(), temp);
		camera.setDirection(temp);

		rotationMatrix.applyPost(camera.getUp(), temp);
		camera.setUp(temp);
		Vector3.releaseTempInstance(temp);

		camera.normalize();
	}

	public void rotateUpDown(final Canvas canvas, final double speed) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();

		final Vector3 temp = Vector3.fetchTempInstance();
		rotationMatrix.fromAngleNormalAxis(speed * -TURN_SPEED, camera.getLeft());

		rotationMatrix.applyPost(camera.getLeft(), temp);
		camera.setLeft(temp);

		rotationMatrix.applyPost(camera.getDirection(), temp);
		camera.setDirection(temp);

		rotationMatrix.applyPost(camera.getUp(), temp);
		camera.setUp(temp);

		Vector3.releaseTempInstance(temp);

		camera.normalize();

	}

	public void moveDown(Canvas canvas, double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance().set(camera.getUp());
		dir.multiplyLocal(-MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}

	public void moveUp(Canvas canvas, double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance().set(camera.getUp());
		dir.multiplyLocal(MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}

	private boolean isParallelProjection(Camera camera) {
		return camera.getProjectionMode() == Camera.ProjectionMode.Parallel;
	}
	
	public void moveForward(final Canvas canvas, final double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance();
		if (!isParallelProjection(camera)) {
			dir.set(camera.getDirection());
		} else {
			// move up if in parallel mode
			dir.set(camera.getUp());
		}
		dir.multiplyLocal(MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}

	public void moveBack(final Canvas canvas, final double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance();
		if (!isParallelProjection(camera)) {
			dir.set(camera.getDirection());
		} else {
			// move up if in parallel mode
			dir.set(camera.getUp());
		}
		dir.multiplyLocal(-MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}

	public void moveLeft(final Canvas canvas, final double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance();

		dir.set(camera.getLeft());

		dir.multiplyLocal(MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}

	public void moveRight(final Canvas canvas, final double tpf) {
		final Camera camera = canvas.getCanvasRenderer().getCamera();
		final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
		final Vector3 dir = Vector3.fetchTempInstance();

		dir.set(camera.getLeft());

		dir.multiplyLocal(-MOVE_SPEED * tpf);
		loc.addLocal(dir);
		camera.setLocation(loc);
		Vector3.releaseTempInstance(loc);
		Vector3.releaseTempInstance(dir);
	}
}
