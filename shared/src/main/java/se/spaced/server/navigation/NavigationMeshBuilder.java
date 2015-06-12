package se.spaced.server.navigation;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.scenegraph.IndexBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Spatial;
import com.google.common.base.Preconditions;
import com.google.common.io.InputSupplier;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.critterai.nmgen.CleanNullRegionBorders;
import org.critterai.nmgen.ContourSet;
import org.critterai.nmgen.ContourSetBuilder;
import org.critterai.nmgen.DetailMeshBuilder;
import org.critterai.nmgen.FilterOutSmallRegions;
import org.critterai.nmgen.IContourAlgorithm;
import org.critterai.nmgen.IOpenHeightFieldAlgorithm;
import org.critterai.nmgen.MatchNullRegionEdges;
import org.critterai.nmgen.OpenHeightfield;
import org.critterai.nmgen.OpenHeightfieldBuilder;
import org.critterai.nmgen.PolyMeshField;
import org.critterai.nmgen.PolyMeshFieldBuilder;
import org.critterai.nmgen.SolidHeightfield;
import org.critterai.nmgen.SolidHeightfieldBuilder;
import org.critterai.nmgen.TriangleMesh;
import se.ardortech.NullTextureManager;
import se.ardortech.SpacedResourceLocator;
import se.ardortech.math.AABox;
import se.ardortech.math.Sphere;
import se.ardortech.meshgenerator.MeshDataGenerator;
import se.ardortech.meshgenerator.MeshFactory;
import se.fearless.common.io.FileStreamLocator;
import se.fearless.common.io.StreamLocator;
import se.spaced.client.environment.settings.EnvSettingsImpl;
import se.spaced.client.environment.settings.EnvironmentSettings;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.environment.time.GameTimeXStreamConverter;
import se.spaced.client.environment.time.HourMinuteGameTimeManager;
import se.spaced.client.model.Prop;
import se.spaced.client.resources.dae.CachingColladaContentLoader;
import se.spaced.client.resources.zone.ZoneXmlFileHandler;
import se.spaced.shared.model.xmo.AttachmentPointIdentifier;
import se.spaced.shared.model.xmo.Blending;
import se.spaced.shared.model.xmo.Material;
import se.spaced.shared.model.xmo.XmoAttachmentPoint;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.model.xmo.XmoEntityFactoryImpl;
import se.spaced.shared.model.xmo.XmoLoader;
import se.spaced.shared.model.xmo.XmoMetaNode;
import se.spaced.shared.resources.NullXmoMaterialManager;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.world.TimeSystemInfo;
import se.spaced.shared.xml.ColorRGBAConverter;
import se.spaced.shared.xml.QuaternionConverter;
import se.spaced.shared.xml.Vector3Converter;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIOException;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * See API here: http://www.critterai.org/javadoc/nmgen/index.html
 */
public class NavigationMeshBuilder {
	/**
	 * The size of the cells. (The grid that forms the base of the field.)
	 * This value represents the x and z-axis sampling resolution to use when generating voxels.
	 */
	private static final float CELL_SIZE = 1.0f;

	/**
	 * The height increment of the field.
	 * This value represents the y-axis sampling resolution to use when generating voxels.
	 */
	private static final float CELL_HEIGHT = 1.0f;

	/**
	 * Represents the minimum floor to ceiling height that will still allow the floor area to be considered walkable.
	 * Permits detection of overhangs in the geometry which make the geometry below become unwalkable.
	 * Constraints: > 0
	 */
	private static final int MIN_TRAVERSABLE_HEIGHT = 2;

	/**
	 * Represents the maximum ledge height that is considered to still be walkable.
	 * Prevents minor deviations in height from improperly showing as obstructions.
	 * Permits detection of stair-like structures, curbs, etc.
	 * Constraints: >= 0
	 */
	private static final int MAX_TRAVERSABLE_STEP = 1;

	/**
	 * The maximum slope that is considered traversable. (Degrees)
	 * Spans that are at or above this slope have the SpanFlags.WALKABLE flag removed.
	 * Constraints: 0 <= value <= 85
	 */
	private static final int MAX_TRAVERSABLE_SLOPE = 30;

	/**
	 * Indicates whether ledges should be marked as unwalkable. I.e. The SpanFlags.WALKABLE flag will be removed.
	 * A ledge is a normally traversable span that has one or more accessible neighbors with a an un-steppable
	 * drop from span top to span top.
	 * E.g. If an agent using the navmesh were to travel down from the ledge span to its neighbor span,
	 * it would result in the maximum traversable step distance being violated.
	 * The agent cannot legally "step down" from a ledge to its neighbor.
	 */
	private static final boolean CLIP_LEDGES = true;

	/**
	 * Represents the closest any part of the navmesh can get to an obstruction in the source mesh.
	 * Usually set to the maximum bounding radius of entities utilizing the navmesh for navigation decisions.
	 */
	private static final int TRAVERSABLE_AREA_BORDER_SIZE = 2;

	/**
	 * The amount of smoothing to be performed when generating the distance field.
	 * This value impacts region formation and border detection.
	 * A higher value results in generally larger regions and larger border sizes.
	 * A value of zero will disable smoothing.
	 * Constraints: 0 <= value <= 4
	 */
	private static final int SMOOTHING_THRESHOLD = 4;

	/**
	 * The flags used to determine which spans from the source SolidHeightfield should be used to build the OpenHeightfield.
	 * Only those spans which whose flags exactly match the filter flag will be considered for inclusion in the generated open field.
	 * Note: Spans from the source field which do not match the filter flags are still taken into account as height obstructions.
	 */
	private static final int FILTER_FLAGS = 0;

	/**
	 * Applies extra algorithms to regions to help prevent poorly formed regions from forming.
	 * If the navigation mesh is missing sections that should be present,
	 * then enabling this feature will likely fix the problem.
	 * Enabling this feature significantly increased processing cost.
	 */
	private static final boolean USE_CONSERVATIVE_EXPANSION = false;

	/**
	 * The minimum region size for unconnected (island) regions. (Voxels)
	 * Any generated regions that are not connected to any other region and are smaller than this size will be
	 * culled before final navmesh generation. I.e. No longer considered traversable.
	 * Constraints: > 0
	 */
	private static final int MIN_UNCONNECTED_REGION_SIZE = 1;

	/**
	 * Any regions smaller than this size will, if possible, be merged with larger regions. (Voxels)
	 * Helps reduce the number of unnecessarily small regions that can be formed.
	 * This is especially an issue in diagonal path regions where inherent faults
	 * in the region generation algorithm can result in unnecessarily small regions.
	 * If a region cannot be legally merged with a neighbor region, then it will be left alone.
	 * Constraints: >= 0
	 */
	private static final int MERGE_REGION_SIZE = 1;
	private static final ArrayList<IOpenHeightFieldAlgorithm> REGION_ALGORITHMS = new ArrayList<IOpenHeightFieldAlgorithm>(
			Arrays.asList(
					new FilterOutSmallRegions(MIN_UNCONNECTED_REGION_SIZE, MERGE_REGION_SIZE),
					new CleanNullRegionBorders(true))
	);

	private static final ArrayList<IContourAlgorithm> CONTOUR_ALGORITHMS = new ArrayList<IContourAlgorithm>(
			Arrays.asList(
					new MatchNullRegionEdges(0.5f)
			)
	);

	/**
	 * The maximum vertices per polygon. The builder will not create polygons with more than this number of vertices.
	 */
	private static final int MAX_VERTS_PER_POLY = 100;

	/**
	 * Sets the sampling distance to use when matching the final mesh to the surface defined by the OpenHeightfield.
	 * Impacts how well the final mesh conforms to surface data in the OpenHeightfield Higher values,
	 * closer conforming, higher final triangle count.
	 * Setting this argument to zero will disable this functionality.
	 * Constraints: >= 0
	 */
	private static final float CONTOUR_SAMPLE_DISTANCE = 0;

	/**
	 * The maximum distance the surface of the navmesh may deviate from the surface data in the OpenHeightfield.
	 * The accuracy of the algorithm which uses this value is impacted by the value of the contour sample distance argument.
	 * The value of this argument has no meaning if the contour sample distance argument is set to zero.
	 * Setting the value to zero is not recommended since it can result in a large increase
	 * in the number of triangles in the final navmesh at a high processing cost.
	 * Constraints: >= 0
	 */
	private static final float CONTOUR_MAX_DEVIATION = 1.0f;
	private final SolidHeightfieldBuilder solidHeightfieldBuilder;
	private final OpenHeightfieldBuilder openHeightfieldBuilder;
	private final ContourSetBuilder contourSetBuilder;
	private final PolyMeshFieldBuilder polyMeshFieldBuilder;
	private final DetailMeshBuilder detailMeshBuilder;

	private SolidHeightfield solidHeightfield;
	private OpenHeightfield openHeightfield;
	private ContourSet contourSet;
	private PolyMeshField polyMeshField;
	private TriangleMesh triangleMesh;

	private final List<Float> vertexList;
	private final List<Integer> indexList;

	public NavigationMeshBuilder() {
		solidHeightfieldBuilder = new SolidHeightfieldBuilder(CELL_SIZE,
				CELL_HEIGHT,
				MIN_TRAVERSABLE_HEIGHT,
				MAX_TRAVERSABLE_STEP,
				MAX_TRAVERSABLE_SLOPE,
				CLIP_LEDGES);

		openHeightfieldBuilder = new OpenHeightfieldBuilder(MIN_TRAVERSABLE_HEIGHT,
				MAX_TRAVERSABLE_STEP,
				TRAVERSABLE_AREA_BORDER_SIZE,
				SMOOTHING_THRESHOLD,
				FILTER_FLAGS,
				USE_CONSERVATIVE_EXPANSION,
				REGION_ALGORITHMS);

		contourSetBuilder = new ContourSetBuilder(CONTOUR_ALGORITHMS);
		polyMeshFieldBuilder = new PolyMeshFieldBuilder(MAX_VERTS_PER_POLY);
		detailMeshBuilder = new DetailMeshBuilder(CONTOUR_SAMPLE_DISTANCE, CONTOUR_MAX_DEVIATION);

		vertexList = new ArrayList<Float>();
		indexList = new ArrayList<Integer>();
	}

	private static <T> T notNull(T obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		return obj;
	}

	public void build() {
		//debugPrintVertices();

		System.out.printf(new Date() + " Starting build: Number of vertices: %d, Number of indices: %d\n",
				vertexList.size(),
				indexList.size());

		solidHeightfield = notNull(solidHeightfieldBuilder.build(Floats.toArray(vertexList), Ints.toArray(indexList)));
		System.out.println(new Date() + " Done building solid height field");

		openHeightfield = notNull(openHeightfieldBuilder.build(solidHeightfield, true));
		System.out.println(new Date() + " Done building open height field with regions: " + openHeightfield.regionCount());

		contourSet = notNull(contourSetBuilder.build(openHeightfield));
		System.out.println(new Date() + " Done building contour set of size: " + contourSet.size());

		polyMeshField = notNull(polyMeshFieldBuilder.build(contourSet));
		System.out.println(new Date() + " Done building poly mesh field");

		triangleMesh = notNull(detailMeshBuilder.build(polyMeshField, openHeightfield));
		System.out.println(new Date() + " Done building triangle mesh");
	}

	private void debugPrintVertices() {
		Iterator<Float> iterator = vertexList.iterator();
		while (iterator.hasNext()) {
			System.out.printf("Vertex: %f %f %f\n", iterator.next(), iterator.next(), iterator.next());
		}
	}

	private void addZone(Zone zone, XmoEntityFactoryImpl xmoEntityFactory) throws XmlIOException {
		for (Prop prop : zone.getProps()) {
			XmoEntity xmoEntity = xmoEntityFactory.create(prop.getXmoFile(), null);
			prop.setXmoEntity(xmoEntity);
			prop.transformProp(xmoEntity);
			addProp(prop);
		}
		for (Zone subzone : zone.getSubzones()) {
			addZone(subzone, xmoEntityFactory);
		}
	}

	private void addProp(Prop prop) {
		XmoEntity xmoEntity = prop.getXmoEntity();
		if (xmoEntity == null) {
			throw new RuntimeException(prop + " lacks xmo entity");
		}
		for (Mesh physicsMesh : xmoEntity.getPhysicsMeshes()) {
			try {
				// Find the top node so we can update the worldTransform correctly
				Spatial top = getTop(physicsMesh);
				top.updateWorldTransform(true);

				// Convert mesh
				indexToWalkMesh(physicsMesh, xmoEntity.getModel().getWorldTransform());
			} catch (Exception e) {
				throw new RuntimeException("Failed to build physics: " + prop + " - " + physicsMesh.getName(), e);
			}
		}
	}

	private Spatial getTop(Spatial top) {
		while (top.getParent() != null) {
			top = top.getParent();
		}
		return top;
	}

	private void indexToWalkMesh(Mesh physicsMesh, ReadOnlyTransform xmoWorldTransform) {
		IntBuffer indexIntBuffer = physicsMesh.getMeshData().getIndices().asIntBuffer().duplicate();
		FloatBuffer vertexFloatBuffer = physicsMesh.getMeshData().getVertexBuffer().duplicate();

		indexIntBuffer.rewind();
		vertexFloatBuffer.rewind();

		if (vertexList.size() % 3 != 0) {
			throw new IllegalArgumentException("Mesh had an illegal number of vertices: " + indexIntBuffer.remaining());
		}
		int offset = vertexList.size() / 3;
		if (indexIntBuffer.remaining() % 3 != 0) {
			throw new IllegalArgumentException("Mesh had an illegal number of indices: " + indexIntBuffer.remaining());
		}
		int remaining = indexIntBuffer.remaining();
		for (int i = 0; i < remaining; i++) {
			indexList.add(offset + indexIntBuffer.get());
		}
		Transform t = new Transform();
		xmoWorldTransform.multiply(physicsMesh.getWorldTransform(), t);
		int totalVertices = vertexFloatBuffer.remaining() / 3;
		for (int i = 0; i < totalVertices; ++i) {
			Vector3 localVertex = new Vector3(vertexFloatBuffer.get(), vertexFloatBuffer.get(), vertexFloatBuffer.get());
			Vector3 globalVertex = t.applyForward(localVertex);
			vertexList.add(globalVertex.getXf());
			vertexList.add(globalVertex.getYf());
			vertexList.add(globalVertex.getZf());
		}
	}

	public static void main(String[] args) throws XmlIOException, IOException {
		HourMinuteGameTimeManager gameTimeManager = new HourMinuteGameTimeManager(new TimeSystemInfo(24, 60, 60, 1));

		XStream xStream = new XStream(new DomDriver());
		xStream.registerConverter(new ColorRGBAConverter());
		xStream.processAnnotations(Sphere.class);
		xStream.processAnnotations(AABox.class);
		xStream.processAnnotations(XmoMetaNode.class);
		xStream.processAnnotations(XmoAttachmentPoint.class);
		xStream.processAnnotations(AttachmentPointIdentifier.class);
		xStream.processAnnotations(GameTime.class);
		xStream.processAnnotations(EnvironmentSettings.class);
		xStream.processAnnotations(EnvSettingsImpl.class);
		xStream.processAnnotations(Material.class);
		xStream.processAnnotations(Blending.class);

		xStream.registerConverter(new GameTimeXStreamConverter(gameTimeManager));

		xStream.registerConverter(new Vector3Converter());
		xStream.registerConverter(new QuaternionConverter());


		FileStreamLocator streamLocator = new FileStreamLocator(new File("newclient/resources"));
		XStreamIO xmlIO = new XStreamIO(xStream, streamLocator);
		ZoneXmlFileHandler zoneXmlFileHandler = new ZoneXmlFileHandler(xmlIO
		);
		Zone rootZone = zoneXmlFileHandler.loadRootZone("zone/devPlanet/devPlanet.zone");
		//Zone rootZone = zoneXmlFileHandler.loadRootZone("/zone/devPlanet/mainland/settlers_landing/settlers_landing_bay/SettlersLandingBay.zone", null);

		ColladaImporter colladaImporter = new ColladaImporter().
				setModelLocator(new SpacedResourceLocator(streamLocator)).
				setLoadTextures(false);

		XmoEntityFactoryImpl xmoEntityFactory = new XmoEntityFactoryImpl(new CachingColladaContentLoader(new CacheManager(),
				colladaImporter), NullXmoMaterialManager.INSTANCE, NullTextureManager.INSTANCE, streamLocator,
				new XmoLoader(xmlIO, new CacheManager()));
		NavigationMeshBuilder meshBuilder = new NavigationMeshBuilder();
		meshBuilder.addZone(rootZone, xmoEntityFactory);
		meshBuilder.build();
		meshBuilder.writeToFile("newclient/resources/devPlanet.navmesh");
	}

	private void writeToFile(String filename) throws IOException {
		if (triangleMesh == null) {
			throw new IllegalStateException("triangle mesh was not generated");
		}
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));

		dataOutputStream.writeInt(triangleMesh.vertices.length);
		dataOutputStream.writeInt(triangleMesh.indices.length);
		dataOutputStream.writeInt(triangleMesh.triangleRegions.length);
		dataOutputStream.writeInt(polyMeshField.verts.length);
		dataOutputStream.writeInt(polyMeshField.polys.length);
		dataOutputStream.writeInt(polyMeshField.polyRegions.length);


		for (int i = 0; i < triangleMesh.vertices.length; i++) {
			dataOutputStream.writeFloat(triangleMesh.vertices[i]);
		}
		writeIntArray(dataOutputStream, triangleMesh.indices);
		writeIntArray(dataOutputStream, triangleMesh.triangleRegions);

		writeIntArray(dataOutputStream, polyMeshField.verts);
		writeIntArray(dataOutputStream, polyMeshField.polys);
		writeIntArray(dataOutputStream, polyMeshField.polyRegions);
		dataOutputStream.close();
	}

	private void writeIntArray(DataOutputStream dataOutputStream, int[] array) throws IOException {
		for (int i = 0; i < array.length; i++) {
			dataOutputStream.writeInt(array[i]);
		}
	}

	public static Mesh buildArdorFromNavMesh(StreamLocator streamLocator, String filename) throws IOException {
		InputSupplier<? extends InputStream> inputSupplier = streamLocator.getInputSupplier(filename);

		InputStream input = inputSupplier.getInput();
		Preconditions.checkNotNull(input, "No input stream for navigation mesh " + filename);
		DataInputStream stream = new DataInputStream(input);
		final int triangleMeshVerticesLen = stream.readInt();
		final int triangleMeshIndicesLen = stream.readInt();
		int triangleMeshTriangleRegionsLen = stream.readInt();
		int polyMeshFieldVertsLen = stream.readInt();
		int polyMeshFieldPolyLen = stream.readInt();
		int polyMeshFieldPolyRegionsLen = stream.readInt();

		final float[] vertices = new float[triangleMeshVerticesLen];
		for (int i = 0; i < triangleMeshVerticesLen; i++) {
			vertices[i] = stream.readFloat();
		}
		final int[] indices = new int[triangleMeshIndicesLen];
		for (int i = 0; i < triangleMeshIndicesLen; i++) {
			indices[i] = stream.readInt();
		}

		Mesh mesh = new MeshFactory().createMesh(new MeshDataGenerator() {
			@Override
			public int getNumVertices() {
				return triangleMeshVerticesLen;
			}

			@Override
			public int getNumIndices() {
				return triangleMeshIndicesLen;
			}

			@Override
			public int getNumNormals() {
				return 0;
			}

			@Override
			public int getNumTextureCoords() {
				return 0;
			}

			@Override
			public void getVertexData(FloatBuffer vertexBuffer) {
				for (int i = 0; i < triangleMeshVerticesLen; i++) {
					vertexBuffer.put(vertices[i] + 0.1f);
				}
			}

			@Override
			public void getNormalData(FloatBuffer normalBuffer) {
			}

			@Override
			public void getTextureData(FloatBuffer textureBuffer) {
			}

			@Override
			public void getIndexData(IndexBufferData indexBuffer, int offset) {
				for (int i = 0; i < triangleMeshIndicesLen; i++) {
					indexBuffer.put(indices[i]);
				}
			}

			@Override
			public void getData(MeshData meshData) {
				getIndexData(meshData.getIndices(), meshData.getVertexBuffer().position() / 3);
				getVertexData(meshData.getVertexBuffer());
				getNormalData(meshData.getNormalBuffer());
				getTextureData(meshData.getTextureBuffer(0));
			}
		});

		return mesh;
	}
}
