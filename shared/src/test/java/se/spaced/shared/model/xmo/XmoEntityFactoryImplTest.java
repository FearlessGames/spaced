package se.spaced.shared.model.xmo;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.scenegraph.Node;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.ardortech.TextureManager;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.io.ClasspathIOLocator;
import se.fearless.common.mock.MockUtil;
import se.mockachino.annotations.Mock;
import se.mockachino.matchers.Matchers;
import se.spaced.shared.resources.XmoMaterialManager;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;


public class XmoEntityFactoryImplTest {
	private XmoEntityFactoryImpl factory;
	@Mock
	ColladaImporter importer;
	@Mock
	XmoMaterialManager materialManager;
	@Mock
	TextureManager textureManager;
	@Mock
	ColladaStorage colladaStorage;
	@Mock
	XmlIO xmlIO;
	@Mock
	ColladaContentLoader colladaContentLoader;

	@Before
	public void setUp() {
		setupMocks(this);
		when(colladaContentLoader.get(Matchers.any(String.class))).thenReturn(MockUtil.deepMock(ColladaContents.class));

		factory = new XmoEntityFactoryImpl(colladaContentLoader,
				materialManager,
				textureManager,
				new ClasspathIOLocator(getClass()),
				new XmoLoader(xmlIO, new CacheManager()));
	}

	@Test
	public void testMetaNodesExtractedWithNoEntityNameSupplied() throws XmlIOException, IOException {
		XmoRoot xmoRoot = new XmoRoot();
		Node expectedReturnNode = new Node();
		ExtendedMeshObject xmo = new ExtendedMeshObject();
		xmo.setColladaFile("borgpig.dae");
		xmo.setTextureFile("texture.png");
		xmo.setXmoMaterialFile("material.mat");

		XmoMetaNode impactMetaNode = new XmoMetaNode(new SpacedVector3(1, 2, 3), SpacedVector3.ZERO, SpacedVector3.ZERO);
		XmoMetaNode namePlateMetaNode = new XmoMetaNode(new SpacedVector3(3, 2, 1),
				SpacedVector3.ZERO,
				SpacedVector3.ZERO);
		List<MetaNode> metaNodes = new ArrayList<MetaNode>();
		metaNodes.add(impactMetaNode);
		metaNodes.add(namePlateMetaNode);

		xmoRoot.addChild(xmo);

		stubReturn(colladaStorage).on(importer).load(any(String.class));
		stubReturn(expectedReturnNode).on(colladaStorage).getScene();
		stubReturn(xmoRoot).on(xmlIO).load(XmoRoot.class, any(String.class));

		XmoEntity xmoEntity = factory.create("path to xmoRoot", "");
	}

	@Ignore
	public void testMetaNodesExtractedWithEntityNameSupplied() throws XmlIOException, IOException {
		XmoRoot xmoRoot = new XmoRoot();
		Node expectedReturnNode = new Node();
		ExtendedMeshObject xmo = new ExtendedMeshObject();
		xmo.setColladaFile("borgpig.dae");
		xmo.setTextureFile("texture.png");
		xmo.setXmoMaterialFile("material.mat");

		XmoMetaNode impactMetaNode = new XmoMetaNode(new SpacedVector3(1, 2, 3), SpacedVector3.ZERO, SpacedVector3.ZERO);
		XmoMetaNode namePlateMetaNode = new XmoMetaNode(new SpacedVector3(3, 2, 1),
				SpacedVector3.ZERO,
				SpacedVector3.ZERO);

		List<MetaNode> metaNodes = new ArrayList<MetaNode>();
		metaNodes.add(impactMetaNode);
		metaNodes.add(namePlateMetaNode);

		xmoRoot.addChild(xmo);

		stubReturn(colladaStorage).on(importer).load(any(String.class));
		stubReturn(expectedReturnNode).on(colladaStorage).getScene();
		stubReturn(xmoRoot).on(xmlIO).load(XmoRoot.class, any(String.class));

		XmoEntity xmoEntity = factory.create("pathToXmoRoot", "EntityName");
	}
}
