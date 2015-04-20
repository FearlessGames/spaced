package se.spaced.client.ardor.jops;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.FastMath;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.TextureCombineMode;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.geom.BufferUtils;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.ResourceSource;
import org.softmed.jops.Generator;
import org.softmed.jops.GeneratorBehaviour;
import org.softmed.jops.Particle;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class ParticleGeneratorMesh extends Mesh {
	private final Generator generator;
	private FloatBuffer verticeBuffer;
	private FloatBuffer textureBuffer;
	private FloatBuffer colorBuffer;
	private IntBuffer indexBuffer;
	private Camera camera;


	private final Vector3 position = new Vector3();
	private final Vector3 cameraUp = new Vector3();
	private final Vector3 cameraRight = new Vector3();
	private final Vector3 tmpUp = new Vector3();
	private final Vector3 tmpRight = new Vector3();
	private final Vector3 up = new Vector3();
	private final Vector3 right = new Vector3();
	private final Vector3 topLeft = new Vector3();
	private final Vector3 bottomLeft = new Vector3();
	private final Vector3 bottomRight = new Vector3();
	private final Vector2 textureBL = new Vector2();
	private final Vector2 textureBR = new Vector2();
	private final Vector2 textureTL = new Vector2();
	private final ColorRGBA color = new ColorRGBA();

	private final Quaternion worldRotation = new Quaternion();


	public ParticleGeneratorMesh(Generator generator) {
		super();
		this.generator = generator;
		init();
	}


	public ParticleGeneratorMesh(String name, Generator generator) {
		super(name);
		this.generator = generator;
		init();
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}


	public void updateGeometricState(final float pTime, final boolean pInitiator) {
		super.updateGeometricState(pTime, pInitiator);
		update(pTime);
	}


	// ===========================================================
	// Methods
	// ===========================================================

	private void init() {
		final BlendState bs = new BlendState();
		bs.setEnabled(true);
		bs.setBlendEnabled(true);
		bs.setSourceFunction(getSourceFunctionByFactor(generator.getRender().getSourceFactor()));
		bs.setDestinationFunction(getDestinationFunctionByFactor(generator.getRender().getDestinationFactor()));


		bs.setTestEnabled(true);
		bs.setReference(0.01f);
		bs.setTestFunction(BlendState.TestFunction.GreaterThan);
		setRenderState(bs);


		final TextureState ts = new TextureState();
		ts.setEnabled(true);
		ResourceSource textureSource = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, generator.getRender().getTextureName());

		final Texture tex = TextureManager.load(textureSource, Texture.MinificationFilter.BilinearNearestMipMap, TextureStoreFormat.GuessCompressedFormat, false);

		tex.setWrap(Texture.WrapMode.BorderClamp);


		ts.setTexture(tex);
		setRenderState(ts);


		final ZBufferState zs = new ZBufferState();
		zs.setEnabled(true);
		zs.setWritable(false);
		setRenderState(zs);

		getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
		getSceneHints().setLightCombineMode(LightCombineMode.Off);
		getSceneHints().setTextureCombineMode(TextureCombineMode.Replace);


		final GeneratorBehaviour generatorBehaviour = generator.getGb();
		final int numTriangles = generatorBehaviour.getNumber();
		final int numVertices = 3 * numTriangles;
		verticeBuffer = BufferUtils.createVector3Buffer(numVertices);
		textureBuffer = BufferUtils.createVector2Buffer(numVertices);
		colorBuffer = BufferUtils.createColorBuffer(numVertices);
		indexBuffer = BufferUtils.createIntBuffer(numTriangles * 3);


		this.getMeshData().setVertexBuffer(verticeBuffer);
		this.getMeshData().setTextureCoords(new FloatBufferData(textureBuffer, 2), 0);
		this.getMeshData().setColorBuffer(colorBuffer);
		this.getMeshData().setIndexBuffer(indexBuffer);
		for (int i = 0; i < numTriangles; i++) {
			indexBuffer.put(i * 3 + 2);
			indexBuffer.put(i * 3 + 1);
			indexBuffer.put(i * 3 + 0);
		}
		indexBuffer.flip();
		for (int i = 0; i < numVertices; i++) {
			verticeBuffer.put(0f).put(0f).put(0f);
			textureBuffer.put(0f).put(0f);
			colorBuffer.put(1f).put(1f).put(1f).put(1f);
		}
		verticeBuffer.flip();
		textureBuffer.flip();
		colorBuffer.flip();


		//updateRenderState(); //todo: needed in ardor?
	}

	private void update(final float pTpf) {
		buildTriangles();
	}

	private void buildTriangles() {
		final List<Particle> particles = generator.getParticles();
		Particle particle = null;
		int alive = 0;
		if (!generator.isAbsoluteParticleAngle()) {
			setCameraVectors();
		} // billboards
		for (int i = 0; i < particles.size(); i++) {
			particle = particles.get(i);
			if (!(particle.life < 0.0f && generator.isKillParticles())) {
				final int vIndex = alive * 3;
				buildTriangle(verticeBuffer, vIndex, particle);
				setColor(colorBuffer, vIndex, particle);
				setTextureCoords(textureBuffer, vIndex, particle);
				alive++;
			}
		}
		indexBuffer.limit(alive * 3);
		this.getMeshData().setIndexBuffer(indexBuffer);
	}


	private void setCameraVectors() {
		if (camera != null) {
			cameraUp.set(camera.getUp());
			cameraRight.set(camera.getLeft());
			cameraUp.normalizeLocal().multiplyLocal(0.5f);
			cameraRight.normalizeLocal().multiplyLocal(-0.5f);
		} else {
			cameraUp.set(0f, 0.5f, 0f);
			cameraRight.set(0.5f, 0f, 0f);
		}
		worldRotation.fromRotationMatrix(getWorldRotation());
		worldRotation.invertLocal();

		worldRotation.apply(cameraUp, cameraUp);
		worldRotation.apply(cameraRight, cameraRight);
	}

	private void buildTriangle(final FloatBuffer verticleBuffer, final int verticeIndex, final Particle particle) {
		if (generator.isAbsoluteParticleAngle()) {
			buildOrientedTriangle(verticleBuffer, verticeIndex, particle);
		} else {
			buildBillboardTriangle(verticleBuffer, verticeIndex, particle);
		}
	}

	private void buildOrientedTriangle(final FloatBuffer verticeBuffer, final int verticeIndex, final Particle particle) {
		final float size = particle.size;
		final float width = particle.width * 0.5f;
		final float height = particle.height * 0.5f;


		final double correctedVAngle = particle.angleV - MathUtils.HALF_PI;


		position.set(particle.position.getX(), particle.position.getY(), particle.position.getZ());


		final double sinCV = FastMath.sin(correctedVAngle);
		final double sinH = FastMath.sin(particle.angleH);
		final double sinV = FastMath.sin(particle.angleV);
		final double cosH = FastMath.cos(particle.angleH);


		tmpUp.setX(cosH * sinCV);
		tmpUp.setZ(sinH * sinCV);
		tmpUp.setY(FastMath.cos(correctedVAngle));


		tmpRight.setX(cosH * sinV);
		tmpRight.setZ(sinH * sinV);
		tmpRight.setY(FastMath.cos(particle.angleV));


		tmpRight.crossLocal(tmpUp);


		constructTriangle(verticeBuffer, verticeIndex, size, width, height);


	}


	private void buildBillboardTriangle(final FloatBuffer pVerticeBuffer, final int pVerticeIndex, final Particle pParticle) {
		final float size = pParticle.size;
		final float width = pParticle.width * 0.5f;
		final float height = pParticle.height * 0.5f;


		position.set(pParticle.position.getX(), pParticle.position.getY(), pParticle.position.getZ());


		tmpUp.set(cameraUp);
		tmpRight.set(cameraRight);


		constructTriangle(pVerticeBuffer, pVerticeIndex, size, width, height);
	}


	private void constructTriangle(final FloatBuffer verticeBuffer, final int verticeIndex, final float size, final float width, final float height) {

		tmpUp.multiplyLocal(height);
		tmpRight.multiplyLocal(width);

		up.set(tmpUp);
		up.multiplyLocal(-0.5f);
		right.set(tmpRight);
		right.multiplyLocal(-0.5f);

		up.addLocal(right);

		topLeft.set(tmpUp).subtractLocal(tmpRight).subtractLocal(up);
		tmpUp.multiplyLocal(-1f);
		bottomLeft.set(tmpUp).subtractLocal(tmpRight).subtractLocal(up);
		bottomRight.set(tmpUp).addLocal(tmpRight).subtractLocal(up);


		topLeft.multiplyLocal(size).addLocal(position);
		bottomLeft.multiplyLocal(size).addLocal(position);
		bottomRight.multiplyLocal(size).addLocal(position);

		BufferUtils.setInBuffer(bottomLeft, verticeBuffer, verticeIndex);
		BufferUtils.setInBuffer(bottomRight, verticeBuffer, verticeIndex + 1);
		BufferUtils.setInBuffer(topLeft, verticeBuffer, verticeIndex + 2);
	}

	private void setColor(final FloatBuffer pColorBuffer, final int pVIndex, final Particle pParticle) {
		color.set(pParticle.color.getRed(), pParticle.color.getGreen(), pParticle.color.getBlue(), pParticle.alpha); // RGBA
		BufferUtils.setInBuffer(color, pColorBuffer, pVIndex);
		BufferUtils.setInBuffer(color, pColorBuffer, pVIndex + 1);
		BufferUtils.setInBuffer(color, pColorBuffer, pVIndex + 2);
	}

	private void setTextureCoords(final FloatBuffer tb, int vIndex, Particle particle) {
		final float texWidth = 1f / particle.texWidth;
		final float texHeight = 1f / particle.texHeight;


		textureBL.set(-0.5f, -0.5f);
		textureBR.set(+1.5f, -0.5f);
		textureTL.set(-0.5f, +1.5f);


		rotate2f(textureBL, textureBR, textureTL, particle.angle);


		final float bias = 0.5f;


		textureBL.set(textureBL.getX() * texWidth + bias, textureBL.getY() * texHeight + bias);
		textureBR.set(textureBR.getX() * texWidth + bias, textureBR.getY() * texHeight + bias);
		textureTL.set(textureTL.getX() * texWidth + bias, textureTL.getY() * texHeight + bias);

		BufferUtils.setInBuffer(textureBL, tb, vIndex);
		BufferUtils.setInBuffer(textureBR, tb, vIndex + 1);
		BufferUtils.setInBuffer(textureTL, tb, vIndex + 2);
	}


	private void rotate2f(final Vector2 pVector1, final Vector2 pVector2, final Vector2 pVector3, final float pAngle) {
		final double cosAngle = FastMath.cos(pAngle);
		final double sinAngle = FastMath.sin(pAngle);
		pVector1.set(pVector1.getX() * cosAngle - pVector1.getY() * sinAngle, pVector1.getY() * cosAngle + pVector1.getX() * sinAngle);

		pVector2.set(pVector2.getX() * cosAngle - pVector2.getY() * sinAngle, pVector2.getY() * cosAngle + pVector2.getX() * sinAngle);

		pVector3.set(pVector3.getX() * cosAngle - pVector3.getY() * sinAngle, pVector3.getY() * cosAngle + pVector3.getX() * sinAngle);
	}


	protected BlendState.SourceFunction getSourceFunctionByFactor(final int glBlendFunction) {
		switch (glBlendFunction) {
			case 1:
				return BlendState.SourceFunction.One;
			case 0:
				return BlendState.SourceFunction.Zero;
			case 772:
				return BlendState.SourceFunction.DestinationAlpha;
			case 774:
				return BlendState.SourceFunction.DestinationColor;
			case 770:
				return BlendState.SourceFunction.SourceAlpha;
			case 768:
				return BlendState.SourceFunction.DestinationColor;
			case 773:
				return BlendState.SourceFunction.OneMinusDestinationAlpha;
			case 775:
				return BlendState.SourceFunction.OneMinusDestinationColor;
			case 771:
				return BlendState.SourceFunction.OneMinusSourceAlpha;
			case 776:
				return BlendState.SourceFunction.SourceAlphaSaturate;
			case 769:
				return BlendState.SourceFunction.OneMinusDestinationColor;


			default:
				throw (new RuntimeException("GL source blend mode not recognized : " + glBlendFunction));
		}
	}


	protected BlendState.DestinationFunction getDestinationFunctionByFactor(final int glBlendFactor) {
		switch (glBlendFactor) {
			case 1:
				return BlendState.DestinationFunction.One;
			case 0:
				return BlendState.DestinationFunction.Zero;
			case 772:
				return BlendState.DestinationFunction.DestinationAlpha;
			case 774:
				return BlendState.DestinationFunction.SourceColor; // AlphaState.SB_DST_COLOR;
			case 770:
				return BlendState.DestinationFunction.SourceAlpha;
			case 768:
				return BlendState.DestinationFunction.SourceColor; // AlphaState.SB_DST_COLOR;
			case 773:
				return BlendState.DestinationFunction.OneMinusDestinationAlpha;
			case 775:
				return BlendState.DestinationFunction.OneMinusSourceColor; // AlphaState.SB_ONE_MINUS_DST_COLOR;
			case 771:
				return BlendState.DestinationFunction.OneMinusSourceAlpha;
			case 776:
				return BlendState.DestinationFunction.SourceAlpha; // AlphaState.SB_SRC_ALPHA_SATURATE;
			case 769:
				return BlendState.DestinationFunction.OneMinusSourceColor; // AlphaState.SB_ONE_MINUS_DST_COLOR;
			default:
				throw (new RuntimeException("GL source blend mode not recognized : " + glBlendFactor));
		}
	}
}
