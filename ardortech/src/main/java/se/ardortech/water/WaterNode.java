package se.ardortech.water;

import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.math.*;
import com.ardor3d.math.type.ReadOnlyMatrix4;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.*;
import com.ardor3d.renderer.Camera.ProjectionMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.*;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.TextureCombineMode;
import com.ardor3d.scenegraph.shape.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.io.IOLocator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The WaterNode handles rendering of a water effect on all of it's children. What is reflected in the water is
 * controlled through setReflectedScene/addReflectedScene. The skybox (if any) needs to be explicitly set through
 * setSkybox since it needs to be relocated when rendering the reflection. The water is treated as a plane no matter
 * what the geometry is, which is controlled through the water node plane equation settings.
 */
public class WaterNode extends Node {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final long serialVersionUID = 1L;

	protected Camera cam;
	protected double tpf;
	protected double reflectionThrottle = 0f;
	protected double refractionThrottle = 0f;
	protected double reflectionTime = 0;
	protected double refractionTime = 0;
	protected boolean useFadeToFogColor = false;

	protected TextureRenderer tRenderer;
	protected Texture2D textureReflect;
	protected Texture2D textureReflectBlur;
	protected Texture2D textureRefract;
	protected Texture2D textureDepth;

	protected List<Spatial> renderList;
	protected List<Texture> texArray = new ArrayList<Texture>();
	protected final Node skyBox;

	protected GLSLShaderObjectsState waterShader;
	protected CullState cullBackFace;
	protected TextureState textureState;
	protected TextureState fallbackTextureState;

	private final Texture normalmapTexture;
	private final Texture dudvTexture;
	private final Texture foamTexture;
	private final IOLocator ioLocator;
	private final Texture fallbackTexture;
	private Matrix4 fallbackTextureStateMatrix;

	protected BlendState as1;
	protected FogState noFog;

	protected Plane waterPlane;
	protected Vector3 tangent;
	protected Vector3 binormal;
	protected Vector3 calcVect = new Vector3();
	protected double clipBias;
	protected ColorRGBA waterColorStart;
	protected ColorRGBA waterColorEnd;
	protected double heightFalloffStart;
	protected double heightFalloffSpeed;
	protected double waterMaxAmplitude;
	protected double speedReflection;
	protected double speedRefraction;

	protected boolean aboveWater;
	protected double normalTranslation = 0.0;
	protected double refractionTranslation = 0.0;
	protected boolean supported = true;
	protected boolean useProjectedShader = false;
	protected boolean useRefraction = false;
	protected boolean useReflection = true;
	protected int renderScale;

	protected String simpleShaderStr = "com/ardor3d/extension/effect/water/flatwatershader";
	protected String simpleShaderRefractionStr = "com/ardor3d/extension/effect/water/flatwatershader_refraction";
	protected String projectedShaderStr = "com/ardor3d/extension/effect/water/projectedwatershader";
	protected String projectedShaderRefractionStr = "com/ardor3d/extension/effect/water/projectedwatershader_refraction";
	protected String currentShaderStr;

	private GLSLShaderObjectsState blurShaderVertical = null;
	private float blurSampleDistance = 0.002f;
	private Quad fullScreenQuad = null;
	private boolean doBlurReflection = true;

	private boolean initialized;

	/**
	 * Resets water parameters to default values
	 */
	public void resetParameters() {
		waterPlane = new Plane(new Vector3(0.0, 1.0, 0.0), 0.0);
		tangent = new Vector3(1.0, 0.0, 0.0);
		binormal = new Vector3(0.0, 0.0, 1.0);

		waterMaxAmplitude = 0.0;
		clipBias = 1.0;
		waterColorStart = new ColorRGBA(0.0f, 0.0f, 0.1f, 1.0f);
		waterColorEnd = new ColorRGBA(0.0f, 0.3f, 0.1f, 1.0f);
		heightFalloffStart = 400.0;
		heightFalloffSpeed = 500.0;
		speedReflection = 0.1;
		speedRefraction = -0.05;
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
		if (isSupported() && tRenderer != null) {
			tRenderer.cleanup();
		}
	}

	public boolean isSupported() {
		return supported;
	}

	/**
	 * Creates a new WaterRenderPass
	 *
	 * @param cam					 main rendercam to use for reflection settings etc
	 * @param renderScale		  how many times smaller the reflection/refraction textures should be compared to the main display
	 * @param useProjectedShader true - use the projected setup for variable height water meshes, false - use the flast shader setup
	 * @param useRefraction		enable/disable rendering of refraction textures
	 * @param foamTexture
	 */
	public WaterNode(
			final Camera cam, final int renderScale, final boolean useProjectedShader,
			final boolean useRefraction,
			Texture normalMap,
			Texture dudv,
			Texture fallback,
			Node skyBox,
			Texture foamTexture,
			IOLocator ioLocator) {
		this.cam = cam;
		this.useProjectedShader = useProjectedShader;
		this.useRefraction = useRefraction;
		this.renderScale = renderScale;
		this.normalmapTexture = normalMap;
		this.dudvTexture = dudv;
		this.fallbackTexture = fallback;
		this.foamTexture = foamTexture;
		this.ioLocator = ioLocator;
		if (skyBox != null) {
			final ClipState skyboxClipState = new ClipState();
			skyboxClipState.setEnabled(false);
			skyBox.setRenderState(skyboxClipState);
		}
		this.skyBox = skyBox;


		resetParameters();

		waterShader = new GLSLShaderObjectsState();
		blurShaderVertical = new GLSLShaderObjectsState();

		cullBackFace = new CullState();
		cullBackFace.setEnabled(true);
		cullBackFace.setCullFace(CullState.Face.None);
	}

	/**
	 * Initialize texture renderers. Load water textures. Create shaders.
	 *
	 * @param r
	 */
	private void initialize(final Renderer r) {
		if (cam == null || initialized) {
			return;
		}
		initialized = true;

		final ContextCapabilities caps = ContextManager.getCurrentContext().getCapabilities();

		if (useRefraction && useProjectedShader && caps.getNumberOfFragmentTextureUnits() < 6 || useRefraction
				&& caps.getNumberOfFragmentTextureUnits() < 5) {
			useRefraction = false;
			log.info("Not enough textureunits, falling back to non refraction water");
		}

		if (!caps.isGLSLSupported()) {
			supported = false;
		}
		if (!(caps.isPbufferSupported() || caps.isFBOSupported())) {
			supported = false;
		}

		if (isSupported()) {
			tRenderer = TextureRendererFactory.INSTANCE.createTextureRenderer( //
					cam.getWidth() / renderScale, // width
					cam.getHeight() / renderScale, // height
					8, // Depth bits... TODO: Make configurable?
					0, // Samples... TODO: Make configurable?
					r, caps);

			// blurSampleDistance = 1f / ((float) cam.getHeight() / renderScale);

			tRenderer.setMultipleTargets(true);
			tRenderer.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
			tRenderer.getCamera().setFrustum(cam.getFrustumNear(), cam.getFrustumFar(), cam.getFrustumLeft(),
					cam.getFrustumRight(), cam.getFrustumTop(), cam.getFrustumBottom());

			textureState = new TextureState();
			textureState.setEnabled(true);

			setupTextures();

			fullScreenQuad = new Quad("FullScreenQuad", cam.getWidth() / 4, cam.getHeight() / 4);
			fullScreenQuad.setTranslation(cam.getWidth() / 2, cam.getHeight() / 2, 0);
			fullScreenQuad.getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
			fullScreenQuad.getSceneHints().setCullHint(CullHint.Never);
			fullScreenQuad.getSceneHints().setTextureCombineMode(TextureCombineMode.Replace);
			fullScreenQuad.getSceneHints().setLightCombineMode(LightCombineMode.Off);
			final TextureState ts = new TextureState();
			ts.setTexture(textureReflect);
			fullScreenQuad.setRenderState(ts);
			fullScreenQuad.setRenderState(blurShaderVertical);
			fullScreenQuad.updateWorldRenderStates(false);
		}

		if (!isSupported()) {
			createFallbackData();
		} else {
			noFog = new FogState();
			noFog.setEnabled(false);
		}

		getSceneHints().setCullHint(CullHint.Never);

		setWaterEffectOnSpatial(this);
	}

	/**
	 * Load water textures.
	 */
	protected void setupTextures() {
		textureReflect = new Texture2D();
		textureReflect.setWrap(Texture.WrapMode.EdgeClamp);
		textureReflect.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		Matrix4 matrix = new Matrix4();
		matrix.setValue(0, 0, -1.0);
		matrix.setValue(3, 0, 1.0);
		textureReflect.setTextureMatrix(matrix);
		tRenderer.setupTexture(textureReflect);

		textureState.setTexture(normalmapTexture, 0);
		normalmapTexture.setWrap(Texture.WrapMode.Repeat);

		textureReflectBlur = new Texture2D();
		textureReflectBlur.setWrap(Texture.WrapMode.EdgeClamp);
		textureReflectBlur.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		textureReflectBlur.setTextureMatrix(matrix);
		tRenderer.setupTexture(textureReflectBlur);

		textureState.setTexture(textureReflectBlur, 1);

		matrix = new Matrix4();
		matrix.setValue(0, 0, 0.8);
		matrix.setValue(1, 1, 0.8);
		dudvTexture.setTextureMatrix(matrix);
		textureState.setTexture(dudvTexture, 2);
		dudvTexture.setWrap(Texture.WrapMode.Repeat);

		if (useRefraction) {
			textureRefract = new Texture2D();
			textureRefract.setWrap(Texture.WrapMode.EdgeClamp);
			textureRefract.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
			tRenderer.setupTexture(textureRefract);

			textureDepth = new Texture2D();
			textureDepth.setWrap(Texture.WrapMode.EdgeClamp);
			textureDepth.setMagnificationFilter(Texture.MagnificationFilter.NearestNeighbor);
			textureDepth.setTextureStoreFormat(TextureStoreFormat.Depth24);
			tRenderer.setupTexture(textureDepth);

			textureState.setTexture(textureRefract, 3);
			textureState.setTexture(textureDepth, 4);
		}

		if (useProjectedShader) {
			if (useRefraction) {
				textureState.setTexture(foamTexture, 5);
			} else {
				textureState.setTexture(foamTexture, 3);
			}
			foamTexture.setWrap(Texture.WrapMode.Repeat);
		}

		reloadShader();
	}

	/**
	 * Create setup to use as fallback if fancy water is not supported.
	 */
	private void createFallbackData() {
		fallbackTextureState = new TextureState();
		fallbackTextureState.setEnabled(true);


		fallbackTextureState.setTexture(fallbackTexture, 0);
		fallbackTexture.setWrap(Texture.WrapMode.Repeat);

		fallbackTextureStateMatrix = new Matrix4();

		as1 = new BlendState();
		as1.setBlendEnabled(true);
		as1.setTestEnabled(true);
		as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as1.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		as1.setEnabled(true);
	}

	public void update(final double tpf) {
		this.tpf = tpf;
	}

	@Override
	public void draw(final Renderer r) {
		initialize(r);

		updateTranslations();

		final double camWaterDist = waterPlane.pseudoDistance(cam.getLocation());
		aboveWater = camWaterDist >= 0;

		if (isSupported()) {
			waterShader.setUniform("tangent", tangent);
			waterShader.setUniform("binormal", binormal);
			waterShader.setUniform("useFadeToFogColor", useFadeToFogColor);
			waterShader.setUniform("waterColor", waterColorStart);
			waterShader.setUniform("waterColorEnd", waterColorEnd);
			waterShader.setUniform("normalTranslation", (float) normalTranslation);
			waterShader.setUniform("refractionTranslation", (float) refractionTranslation);
			waterShader.setUniform("abovewater", aboveWater);
			if (useProjectedShader) {
				waterShader.setUniform("cameraPos", cam.getLocation());
				waterShader.setUniform("waterHeight", (float) waterPlane.getConstant());
				waterShader.setUniform("amplitude", (float) waterMaxAmplitude);
				waterShader.setUniform("heightFalloffStart", (float) heightFalloffStart);
				waterShader.setUniform("heightFalloffSpeed", (float) heightFalloffSpeed);
			}

			final double heightTotal = clipBias + waterMaxAmplitude - waterPlane.getConstant();
			final Vector4 clipPlane = Vector4.fetchTempInstance();

			if (useReflection) {
				clipPlane.set(waterPlane.getNormal().getX(), waterPlane.getNormal().getY(), waterPlane.getNormal()
						.getZ(), heightTotal);
				renderReflection(clipPlane);
			}

			if (useRefraction && aboveWater) {
				clipPlane.set(-waterPlane.getNormal().getX(), -waterPlane.getNormal().getY(), -waterPlane.getNormal()
						.getZ(), -waterPlane.getConstant());
				renderRefraction(clipPlane);
			}
		}

		if (fallbackTextureState != null) {
			fallbackTextureStateMatrix.setValue(3, 1, normalTranslation);
			fallbackTexture.setTextureMatrix(fallbackTextureStateMatrix);
		}

		super.draw(r);
	}

	protected void updateTranslations() {
		normalTranslation += speedReflection * tpf;
		refractionTranslation += speedRefraction * tpf;
	}

	public void reloadShader() {
		if (useProjectedShader) {
			if (useRefraction) {
				currentShaderStr = projectedShaderRefractionStr;
			} else {
				currentShaderStr = projectedShaderStr;
			}
		} else {
			if (useRefraction) {
				currentShaderStr = simpleShaderRefractionStr;
			} else {
				currentShaderStr = simpleShaderStr;
			}
		}

		try {
			log.info("loading " + currentShaderStr);
			waterShader.setVertexShader(ioLocator.getByteSource(currentShaderStr + ".vert").openBufferedStream());
			waterShader.setFragmentShader(ioLocator.getByteSource(currentShaderStr + ".frag").openBufferedStream());
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load water shader");
			//logger.log(Level.WARNING, "Error loading shader", e);
			//return;
		}

		waterShader.setUniform("normalMap", 0);
		waterShader.setUniform("reflection", 1);
		waterShader.setUniform("dudvMap", 2);
		if (useRefraction) {
			waterShader.setUniform("refraction", 3);
			waterShader.setUniform("depthMap", 4);
		}
		if (useProjectedShader) {
			if (useRefraction) {
				waterShader.setUniform("foamMap", 5);
			} else {
				waterShader.setUniform("foamMap", 3);
			}
		}

		waterShader._needSendShader = true;

		try {
			blurShaderVertical.setVertexShader(WaterNode.class.getClassLoader().getResourceAsStream(
					"com/ardor3d/extension/effect/bloom/bloom_blur.vert"));
			blurShaderVertical.setFragmentShader(WaterNode.class.getClassLoader().getResourceAsStream(
					"com/ardor3d/extension/effect/bloom/bloom_blur_vertical5_down.frag"));
		} catch (final IOException ex) {
			log.warn("Could not load shaders.", ex);
		}
		blurShaderVertical.setUniform("RT", 0);
		blurShaderVertical.setUniform("sampleDist", blurSampleDistance);
		blurShaderVertical._needSendShader = true;

		log.info("Shader reloaded...");
	}

	/**
	 * Sets a spatial up for being rendered with the watereffect
	 *
	 * @param spatial Spatial to use as base for the watereffect
	 */
	public void setWaterEffectOnSpatial(final Spatial spatial) {
		spatial.setRenderState(cullBackFace);
		if (isSupported()) {
			// spatial.setRenderBucketType(RenderBucketType.Skip);
			spatial.setRenderState(waterShader);
			spatial.setRenderState(textureState);
		} else {
			spatial.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
			spatial.getSceneHints().setLightCombineMode(LightCombineMode.Off);
			spatial.setRenderState(fallbackTextureState);
			spatial.setRenderState(as1);
		}
	}

	// temporary vectors for mem opt.
	private final Vector3 tmpLocation = new Vector3();
	private final Vector3 camReflectPos = new Vector3();
	private final Vector3 camReflectDir = new Vector3();
	private final Vector3 camReflectUp = new Vector3();
	private final Vector3 camReflectLeft = new Vector3();
	private final Vector3 camLocation = new Vector3();

	/**
	 * Render water reflection RTT
	 */
	private void renderReflection(final Vector4 clipPlane) {
		if (renderList == null || renderList.isEmpty()) {
			return;
		}

		reflectionTime += tpf;
		if (reflectionTime < reflectionThrottle) {
			return;
		}
		reflectionTime = 0;

		if (aboveWater) {
			camLocation.set(cam.getLocation());

			double planeDistance = waterPlane.pseudoDistance(camLocation);
			calcVect.set(waterPlane.getNormal()).multiplyLocal(planeDistance * 2.0f);
			camReflectPos.set(camLocation.subtractLocal(calcVect));

			camLocation.set(cam.getLocation()).addLocal(cam.getDirection());
			planeDistance = waterPlane.pseudoDistance(camLocation);
			calcVect.set(waterPlane.getNormal()).multiplyLocal(planeDistance * 2.0f);
			camReflectDir.set(camLocation.subtractLocal(calcVect)).subtractLocal(camReflectPos).normalizeLocal();

			camLocation.set(cam.getLocation()).addLocal(cam.getUp());
			planeDistance = waterPlane.pseudoDistance(camLocation);
			calcVect.set(waterPlane.getNormal()).multiplyLocal(planeDistance * 2.0f);
			camReflectUp.set(camLocation.subtractLocal(calcVect)).subtractLocal(camReflectPos).normalizeLocal();

			camReflectLeft.set(camReflectUp).crossLocal(camReflectDir).normalizeLocal();

			tRenderer.getCamera().setLocation(camReflectPos);
			tRenderer.getCamera().setDirection(camReflectDir);
			tRenderer.getCamera().setUp(camReflectUp);
			tRenderer.getCamera().setLeft(camReflectLeft);
		} else {
			tRenderer.getCamera().setLocation(cam.getLocation());
			tRenderer.getCamera().setDirection(cam.getDirection());
			tRenderer.getCamera().setUp(cam.getUp());
			tRenderer.getCamera().setLeft(cam.getLeft());
		}

		if (skyBox != null) {
			tmpLocation.set(skyBox.getTranslation());
			skyBox.setTranslation(tRenderer.getCamera().getLocation());
			skyBox.updateGeometricState(0.0f);
			skyBox.getSceneHints().setCullHint(CullHint.Never);
		}

		texArray.clear();
		if (doBlurReflection) {
			texArray.add(textureReflect);
		} else {
			texArray.add(textureReflectBlur);
		}

		tRenderer.getCamera().setProjectionMode(ProjectionMode.Custom);
		tRenderer.getCamera().setProjectionMatrix(cam.getProjectionMatrix());
		tRenderer.render(skyBox, texArray, Renderer.BUFFER_COLOR_AND_DEPTH);

		if (skyBox != null) {
			skyBox.getSceneHints().setCullHint(CullHint.Always);
		}

		modifyProjectionMatrix(clipPlane);

		tRenderer.render(renderList, texArray, Renderer.BUFFER_NONE);

		if (doBlurReflection) {
			blurReflectionTexture();
		}

		if (skyBox != null) {
			skyBox.setTranslation(tmpLocation);
			skyBox.updateGeometricState(0.0f);
			skyBox.getSceneHints().setCullHint(CullHint.Never);
		}
	}

	private void blurReflectionTexture() {
		tRenderer.render(fullScreenQuad, textureReflectBlur, Renderer.BUFFER_NONE);
	}

	/**
	 * Render water refraction RTT
	 */
	private void renderRefraction(final Vector4 clipPlane) {
		if (renderList.isEmpty()) {
			return;
		}

		refractionTime += tpf;
		if (refractionTime < refractionThrottle) {
			return;
		}
		refractionTime = 0;

		// tRenderer.getCamera().set(cam);
		tRenderer.getCamera().setLocation(cam.getLocation());
		tRenderer.getCamera().setDirection(cam.getDirection());
		tRenderer.getCamera().setUp(cam.getUp());
		tRenderer.getCamera().setLeft(cam.getLeft());

		CullHint cullMode = CullHint.Dynamic;
		if (skyBox != null) {
			cullMode = skyBox.getSceneHints().getCullHint();
			skyBox.getSceneHints().setCullHint(CullHint.Always);
		}

		tRenderer.getCamera().setProjectionMatrix(cam.getProjectionMatrix());

		texArray.clear();
		texArray.add(textureRefract);
		texArray.add(textureDepth);

		tRenderer.getCamera().update();
		tRenderer.getCamera().getModelViewMatrix();
		tRenderer.getCamera().getProjectionMatrix();

		tRenderer.render(renderList, texArray, Renderer.BUFFER_COLOR_AND_DEPTH);

		if (skyBox != null) {
			skyBox.getSceneHints().setCullHint(cullMode);
		}
	}

	private double sign(final double a) {
		if (a > 0.0) {
			return 1.0;
		}
		if (a < 0.0) {
			return -1.0;
		}
		return 0.0;
	}

	private double projectionMatrix[] = new double[16];
	private final Vector4 cornerPoint = new Vector4();
	private final Matrix4 tmpMatrix = new Matrix4();

	private void modifyProjectionMatrix(final Vector4 clipPlane) {
		// Get the current projection matrix
		projectionMatrix = cam.getProjectionMatrix().toArray(projectionMatrix);

		// Get the inverse transpose of the current modelview matrix
		final ReadOnlyMatrix4 modelViewMatrixInvTrans = tRenderer.getCamera().getModelViewMatrix().invert(tmpMatrix)
				.transposeLocal();
		modelViewMatrixInvTrans.applyPre(clipPlane, clipPlane);

		// Calculate the clip-space corner point opposite the clipping plane
		// as (sgn(clipPlane.x), sgn(clipPlane.y), 1, 1) and
		// transform it into camera space by multiplying it
		// by the inverse of the projection matrix
		cornerPoint.setX((sign(clipPlane.getX()) + projectionMatrix[8]) / projectionMatrix[0]);
		cornerPoint.setY((sign(clipPlane.getY()) + projectionMatrix[9]) / projectionMatrix[5]);
		cornerPoint.setZ(-1.0);
		cornerPoint.setW((1.0 + projectionMatrix[10]) / projectionMatrix[14]);

		// Calculate the scaled plane vector
		final Vector4 scaledPlaneVector = clipPlane.multiply((2.0 / clipPlane.dot(cornerPoint)), cornerPoint);

		// Replace the third row of the projection matrix
		projectionMatrix[2] = scaledPlaneVector.getX();
		projectionMatrix[6] = scaledPlaneVector.getY();
		projectionMatrix[10] = scaledPlaneVector.getZ() + 1.0;
		projectionMatrix[14] = scaledPlaneVector.getW();

		// Load it back into OpenGL
		final Matrix4 newProjectionMatrix = tmpMatrix.fromArray(projectionMatrix);
		tRenderer.getCamera().setProjectionMatrix(newProjectionMatrix);
	}

	public void removeReflectedScene(final Spatial renderNode) {
		if (renderList != null) {
			log.info("Removed reflected scene: " + renderList.remove(renderNode));
		}
	}

	public void clearReflectedScene() {
		if (renderList != null) {
			renderList.clear();
		}
	}

	/**
	 * Adds a spatial to the list of spatials used as reflection in the water
	 *
	 * @param renderNode Spatial to add to the list of objects used as reflection in the water
	 */
	public void addReflectedScene(final Spatial renderNode) {
		if (renderNode == null) {
			return;
		}

		if (renderList == null) {
			renderList = new ArrayList<Spatial>();
		}
		if (!renderList.contains(renderNode)) {
			renderList.add(renderNode);
		}
	}

	public Camera getCam() {
		return cam;
	}

	public void setCam(final Camera cam) {
		this.cam = cam;
	}

	public ColorRGBA getWaterColorStart() {
		return waterColorStart;
	}

	/**
	 * Color to use when the incident angle to the surface is low
	 */
	public void setWaterColorStart(final ColorRGBA waterColorStart) {
		this.waterColorStart = waterColorStart;
	}

	public ColorRGBA getWaterColorEnd() {
		return waterColorEnd;
	}

	/**
	 * Color to use when the incident angle to the surface is high
	 */
	public void setWaterColorEnd(final ColorRGBA waterColorEnd) {
		this.waterColorEnd = waterColorEnd;
	}

	public double getHeightFalloffStart() {
		return heightFalloffStart;
	}

	/**
	 * Set at what distance the waveheights should start to fade out(for projected water only)
	 *
	 * @param heightFalloffStart
	 */
	public void setHeightFalloffStart(final double heightFalloffStart) {
		this.heightFalloffStart = heightFalloffStart;
	}

	public double getHeightFalloffSpeed() {
		return heightFalloffSpeed;
	}

	/**
	 * Set the fadeout length of the waveheights, when over falloff start(for projected water only)
	 */
	public void setHeightFalloffSpeed(final double heightFalloffSpeed) {
		this.heightFalloffSpeed = heightFalloffSpeed;
	}

	public double getWaterHeight() {
		return waterPlane.getConstant();
	}

	/**
	 * Set base height of the waterplane(Used for reflecting the camera for rendering reflection)
	 *
	 * @param waterHeight Waterplane height
	 */
	public void setWaterHeight(final double waterHeight) {
		waterPlane.setConstant(waterHeight);
	}

	public ReadOnlyVector3 getNormal() {
		return waterPlane.getNormal();
	}

	/**
	 * Set the normal of the waterplane(Used for reflecting the camera for rendering reflection)
	 *
	 * @param normal Waterplane normal
	 */
	public void setNormal(final Vector3 normal) {
		waterPlane.setNormal(normal);
	}

	public double getSpeedReflection() {
		return speedReflection;
	}

	/**
	 * Set the movement speed of the reflectiontexture
	 *
	 * @param speedReflection Speed of reflectiontexture
	 */
	public void setSpeedReflection(final double speedReflection) {
		this.speedReflection = speedReflection;
	}

	public double getSpeedRefraction() {
		return speedRefraction;
	}

	/**
	 * Set the movement speed of the refractiontexture
	 *
	 * @param speedRefraction Speed of refractiontexture
	 */
	public void setSpeedRefraction(final double speedRefraction) {
		this.speedRefraction = speedRefraction;
	}

	public double getWaterMaxAmplitude() {
		return waterMaxAmplitude;
	}

	/**
	 * Maximum amplitude of the water, used for clipping correctly(projected water only)
	 *
	 * @param waterMaxAmplitude Maximum amplitude
	 */
	public void setWaterMaxAmplitude(final double waterMaxAmplitude) {
		this.waterMaxAmplitude = waterMaxAmplitude;
	}

	public double getClipBias() {
		return clipBias;
	}

	public void setClipBias(final double clipBias) {
		this.clipBias = clipBias;
	}

	public Plane getWaterPlane() {
		return waterPlane;
	}

	public void setWaterPlane(final Plane waterPlane) {
		this.waterPlane = waterPlane;
	}

	public Vector3 getTangent() {
		return tangent;
	}

	public void setTangent(final Vector3 tangent) {
		this.tangent = tangent;
	}

	public Vector3 getBinormal() {
		return binormal;
	}

	public void setBinormal(final Vector3 binormal) {
		this.binormal = binormal;
	}

	public Texture getTextureReflect() {
		return textureReflect;
	}

	public Texture getTextureRefract() {
		return textureRefract;
	}

	public Texture getTextureDepth() {
		return textureDepth;
	}

	/**
	 * If true, fade to fogcolor. If false, fade to 100% reflective surface
	 *
	 * @param value
	 */
	public void useFadeToFogColor(final boolean value) {
		useFadeToFogColor = value;
	}

	public boolean isUseFadeToFogColor() {
		return useFadeToFogColor;
	}

	public boolean isUseReflection() {
		return useReflection;
	}

	/**
	 * Turn reflection on and off
	 *
	 * @param useReflection
	 */
	public void setUseReflection(final boolean useReflection) {
		if (useReflection == this.useReflection) {
			return;
		}
		this.useReflection = useReflection;
		reloadShader();
	}

	public boolean isUseRefraction() {
		return useRefraction;
	}

	/**
	 * Turn refraction on and off
	 *
	 * @param useRefraction
	 */
	public void setUseRefraction(final boolean useRefraction) {
		if (useRefraction == this.useRefraction) {
			return;
		}
		this.useRefraction = useRefraction;
		reloadShader();
	}

	public int getRenderScale() {
		return renderScale;
	}

	public void setRenderScale(final int renderScale) {
		this.renderScale = renderScale;
	}

	public boolean isUseProjectedShader() {
		return useProjectedShader;
	}

	public void setUseProjectedShader(final boolean useProjectedShader) {
		if (useProjectedShader == this.useProjectedShader) {
			return;
		}
		this.useProjectedShader = useProjectedShader;
		reloadShader();
	}

	public double getReflectionThrottle() {
		return reflectionThrottle;
	}

	public void setReflectionThrottle(final double reflectionThrottle) {
		this.reflectionThrottle = reflectionThrottle;
	}

	public double getRefractionThrottle() {
		return refractionThrottle;
	}

	public void setRefractionThrottle(final double refractionThrottle) {
		this.refractionThrottle = refractionThrottle;
	}

	public TextureState getTextureState() {
		return textureState;
	}

	public void setTextureState(final TextureState textureState) {
		this.textureState = textureState;
	}

	public void updateCamera() {
		if (isSupported()) {
			tRenderer.getCamera().setFrustum(cam.getFrustumNear(), cam.getFrustumFar(), cam.getFrustumLeft(),
					cam.getFrustumRight(), cam.getFrustumTop(), cam.getFrustumBottom());
		}
	}

	public Texture getFallbackTexture() {
		return fallbackTexture;
	}


	public Texture getNormalmapTexture() {
		return normalmapTexture;
	}

	public Texture getDudvTexture() {
		return dudvTexture;
	}

	public Texture getFoamTexture() {
		return foamTexture;
	}


	public boolean isDoBlurReflection() {
		return doBlurReflection;
	}

	public void setDoBlurReflection(final boolean doBlurReflection) {
		this.doBlurReflection = doBlurReflection;
	}

	public float getBlurSampleDistance() {
		return blurSampleDistance;
	}

	public void setBlurSampleDistance(final float blurSampleDistance) {
		this.blurSampleDistance = blurSampleDistance;
	}
}
