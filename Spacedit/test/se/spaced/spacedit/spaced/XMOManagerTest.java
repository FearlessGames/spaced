package se.spaced.spacedit.spaced;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.extension.model.collada.jdom.data.AssetData;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import com.google.common.collect.Iterators;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.fearlessgames.common.io.StreamLocator;
import se.mockachino.annotations.*;
import se.spaced.shared.model.xmo.ExtendedMeshObject;
import se.spaced.shared.model.xmo.XmoRoot;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;
import se.spaced.spacedit.ardor.DefaultScene;
import se.spaced.spacedit.state.RunningState;
import se.spaced.spacedit.state.StateManager;
import se.spaced.spacedit.ui.view.filechooser.FileChooserView;
import se.spaced.spacedit.ui.view.filechooser.SelectedFile;
import se.spaced.spacedit.xmo.XmoManager;
import se.spaced.spacedit.xmo.XmoManagerImpl;
import se.spaced.spacedit.xmo.XmoManagerListener;
import se.spaced.spacedit.xmo.model.WrappedExtendedMeshObject;
import se.spaced.spacedit.xmo.model.WrappedXmoContainerNode;
import se.spaced.spacedit.xmo.model.WrappedXmoRoot;
import se.spaced.spacedit.xmo.model.listeners.ExtendedMeshObjectPropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoContainerNodePropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoMetaNodePropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoRootPropertyListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;


public class XMOManagerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	@Mock
	private XmoManagerListener xmoManagerListener;

	@Mock
	private XmoRootPropertyListener xmoRootPropertyListener;

	@Mock
	private XmlIO testIO;

	@Mock
	private InputSupplier<FileInputStream> inputSupplier;

	@Mock
	private FileChooserView fileChooserView;
	private XmoManager xmoManager;
	@Mock
	private StateManager stateManager;
	@Mock
	private ExtendedMeshObjectPropertyListener xmoPropertyListener;

	@Mock
	private XmoMetaNodePropertyListener xmoMetaNodePropertyListener;

	@Mock
	private XmoContainerNodePropertyListener xmoContainerNodePropertyListener;

	@Mock
	private DefaultScene scene;
	@Mock
	private SelectedFile selectedFile;
	@Mock
	private Node rootNode;
	@Mock
	InputStream inputStream;

	@Mock
	HashMap mapp;

	@Mock
	private ColladaImporter colladaImporter;

	@Before
	public void setUp() throws IOException {
		setupMocks(this);
		stubReturn(new Node()).on(scene).getRootNode();

		xmoManager = new XmoManagerImpl(testIO, streamLocator, stateManager, xmoRootPropertyListener,
				xmoPropertyListener, xmoContainerNodePropertyListener, xmoMetaNodePropertyListener, scene, colladaImporter, "collada", null);
		folder.create();
	}


	@Test
	public void testThatASavedFileIsMarshalledAndCreatedOk() throws IOException, XmlIOException {
		File file = folder.newFile("xmofile");

		WrappedXmoRoot xmoRoot = new WrappedXmoRoot(new XmoRoot(), file.getAbsolutePath());
		xmoManager.initXmoRoot(xmoRoot);
		xmoManager.saveXmoRoot();
		verifyOnce().on(testIO).save(xmoRoot.getXmoRoot(), file.getAbsolutePath());
	}


	@Ignore
	@Test
	public void testLoadToScene() throws IOException, XmlIOException {
		File xmoFile = folder.newFile("test.xmo");
		XmoRoot xmoRoot = new XmoRoot();
		xmoRoot.setName("test root");

		ExtendedMeshObject xmo = new ExtendedMeshObject();
		xmoRoot.addChild(xmo);

		xmo.setColladaFile("child.dae");
		xmo.setPosition(new Vector3(1, 1, 1));

		stubReturn(xmoRoot).on(testIO).load(XmoRoot.class, xmoFile.getName());
		stubReturn(rootNode).on(scene).getRootNode();

		xmoManager.loadXmoRoot(xmoFile.getName());

		verifyOnce().on(rootNode).attachChild(any(Node.class));

		verifyOnce().on(stateManager).switchState(RunningState.XMO_IN_CONTEXT);


	}


	@Test
	public void testAddContainerNode() {
		WrappedXmoRoot root = new WrappedXmoRoot(new XmoRoot(), "root.xmo");
		xmoManager.initXmoRoot(root);
		xmoManager.selectObject(root);
		assertSame(root, xmoManager.getCurrentlySelectedObject());
		xmoManager.createNewContainerNode();
		assertEquals(root.getContainerNodes().size(), 1);

		WrappedXmoContainerNode containerNode = root.getContainerNodes().get(0);
		xmoManager.selectObject(containerNode);
		assertSame(containerNode, xmoManager.getCurrentlySelectedObject());

		xmoManager.createNewContainerNode();
		assertEquals(root.getContainerNodes().size(), 1);
		assertEquals(containerNode.getContainerNodes().size(), 1);
	}

	@Test
	public void testAddXmoNode() throws IOException {
		File coladaMockFile = new File("mockfile.dae");
		ColladaStorage mockStorage = new ColladaStorage();
		mockStorage.setScene(new Node());
		mockStorage.setAssetData(new AssetData());
		mockStorage.getAssetData().setUpAxis(new Vector3(0, 0, 0));
		stubReturn(mockStorage).on(colladaImporter).load(any(String.class));

		WrappedXmoRoot root = new WrappedXmoRoot(new XmoRoot(), "root.xmo");
		xmoManager.initXmoRoot(root);
		xmoManager.selectObject(root);

		xmoManager.addColladaFileAsXmo(coladaMockFile.getName());
		assertEquals(root.getExtendedMeshObjects().size(), 1);

		xmoManager.selectXmoRoot();
		xmoManager.createNewContainerNode();
		assertEquals(root.getContainerNodes().size(), 1);

		WrappedXmoContainerNode containerNode = root.getContainerNodes().get(0);
		xmoManager.selectObject(containerNode);
		assertSame(containerNode, xmoManager.getCurrentlySelectedObject());

		xmoManager.addColladaFileAsXmo(coladaMockFile.getName());
		assertEquals(root.getExtendedMeshObjects().size(), 1);
		assertEquals(containerNode.getExtendedMeshObjects().size(), 1);

	}

	@Test
	public void testAddXmoMetaNode() throws IOException {
		File coladaMockFile = new File("mockfile.dae");
		ColladaStorage mockStorage = new ColladaStorage();
		mockStorage.setScene(new Node());
		mockStorage.setAssetData(new AssetData());
		mockStorage.getAssetData().setUpAxis(new Vector3(0, 0, 0));
		stubReturn(mockStorage).on(colladaImporter).load(any(String.class));

		WrappedXmoRoot root = new WrappedXmoRoot(new XmoRoot(), "root.xmo");
		xmoManager.initXmoRoot(root);
		xmoManager.selectObject(root);

		xmoManager.addColladaFileAsXmo(coladaMockFile.getName());
		assertEquals(root.getExtendedMeshObjects().size(), 1);

		WrappedExtendedMeshObject meshObject = root.getExtendedMeshObjects().get(0);
		xmoManager.selectObject(meshObject);
		assertSame(meshObject, xmoManager.getCurrentlySelectedObject());

		xmoManager.createNewMetaNode();
	}

	final ByteArrayInputStream bais = new ByteArrayInputStream(new byte[]{});
	StreamLocator streamLocator = new StreamLocator() {
		@Override
		public InputSupplier<? extends InputStream> getInputSupplier(String key) {
			return new InputSupplier<InputStream>() {
				@Override
				public InputStream getInput() throws IOException {
					return bais;
				}
			};
		}

		@Override
		public OutputSupplier<? extends OutputStream> getOutputSupplier(String key) {
			return null;
		}

		@Override
		public Iterator<String> listKeys() {
			return Iterators.emptyIterator();
		}
	};
}
