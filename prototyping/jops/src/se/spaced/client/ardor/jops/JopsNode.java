package se.spaced.client.ardor.jops;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Node;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Quaternion4f;
import org.softmed.jops.Generator;
import org.softmed.jops.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

public class JopsNode extends Node {
	/**
	 * The node holding the particles
	 */
	private Node particleNode;
	private ArrayList<ParticleGeneratorMesh> generators;
	private ParticleSystem particleSystem;
	private Camera camera;


	private final Quaternion4f maliQuat = new Quaternion4f();
	private final double[] position = new double[3];
	private final Vector3 worldTranslation = new Vector3();
	private final Quaternion worldRotation = new Quaternion();

	public JopsNode() {
		init();
	}

	public JopsNode(String name) {
		super(name);
		init();
	}


	/**
	 * This is the node containing the particles, if you attach it to
	 * this node you get a relatively oriented particle system.
	 * e.g. jopsNode.attachChild(jopsNode.getParticleNode());
	 * <p/>
	 * Attach it to another node to get an absolute system.
	 * e.g. rootNode.attachChild(jopsNode.getParticleNode());
	 *
	 * @return the particleNode
	 */
	public Node getParticleNode() {
		return particleNode;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}


	public ParticleSystem getParticleSystem() {
		return particleSystem;
	}


	public void setParticleSystem(final ParticleSystem pParticleSystem) {
		this.particleSystem = pParticleSystem;
		if (pParticleSystem.getRotation() == null) {
			pParticleSystem.setRotation(new Matrix4f(
					new float[]{
							1f, 0f, 0f, 0f,
							0f, 1f, 0f, 0f,
							0f, 0f, 1f, 0f,
							0f, 0f, 0f, 1f}));
		}

		if (pParticleSystem.getPosition() == null) {
			pParticleSystem.setPosition(new Point3f());
		}

		installGenerators();
	}

	@Override
	public void updateGeometricState(double time, boolean b) {
		super.updateGeometricState(time, b);
		update(time);
	}


	// ===========================================================
	// Methods
	// ===========================================================

	private void init() {
		particleNode = new Node(getName() + ":ParticleNode");
		generators = new ArrayList<ParticleGeneratorMesh>();
	}


	public void update(final double pTpf) {
		if (particleSystem != null && particleNode.getParent() != this) { // relative or absolute positioning of particles
			// Get this jopsNode's world transformation
			worldTranslation.set(getWorldTranslation());
			worldRotation.set(worldRotation.fromRotationMatrix(getWorldRotation()));

			// update particle node's localScale to be the same as the jopsNode's
			// Necessary since there is no way to scale the JOPS ParticleSystem
			particleNode.setScale(getScale());
			worldTranslation.divideLocal(getWorldScale());

			// update system rotation to correspond to this node's rotation
			maliQuat.set((float) worldRotation.getX(), (float) worldRotation.getY(), (float) worldRotation.getZ(), (float) worldRotation.getW());
			particleSystem.getRotation().set(maliQuat);

			// update system position to correspond to this node's position
			// This trickery is necessary since JOPS internally applies position before rotation, so we need to cancel that out
			worldRotation.invertLocal();

			worldRotation.apply(worldTranslation, worldTranslation);
			worldTranslation.toArray(position);
			particleSystem.getPosition().set((float) position[0], (float) position[1], (float) position[2]);
		}
	}


	private ParticleGeneratorMesh createGenerator(final Generator pGenerator) {
		ParticleGeneratorMesh particleGenerator = new ParticleGeneratorMesh(pGenerator.getName(), pGenerator);
		particleNode.attachChild(particleGenerator);
		particleGenerator.setCamera(camera);
		return particleGenerator;
	}

	private void installGenerators() {
		final List<Generator> generators = particleSystem.getGenerators();
		for (Generator generator : generators) {
			createGenerator(generator);
		}
	}

	@SuppressWarnings("unused")
	private void clearGenerators() {
		for (ParticleGeneratorMesh mGenerator : generators) {
			particleNode.detachChild(mGenerator);
		}
		generators.clear();
	}
}
