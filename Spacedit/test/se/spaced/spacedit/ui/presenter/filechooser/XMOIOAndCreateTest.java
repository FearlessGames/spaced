package se.spaced.spacedit.ui.presenter.filechooser;

import com.ardor3d.scenegraph.Node;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.mockachino.annotations.*;
import se.spaced.shared.model.xmo.ExtendedMeshObject;
import se.spaced.shared.xml.XmlIO;
import se.spaced.spacedit.ardor.DefaultScene;
import se.spaced.spacedit.state.StateManager;
import se.spaced.spacedit.ui.view.filechooser.FileChooserView;
import se.spaced.spacedit.ui.view.filechooser.SelectedFile;
import se.spaced.spacedit.xmo.XmoCreatorImpl;
import se.spaced.spacedit.xmo.XmoManager;
import se.spaced.spacedit.xmo.model.WrappedXmoRoot;
import se.spaced.spacedit.xmo.model.listeners.ExtendedMeshObjectPropertyListener;

import java.io.File;
import java.io.IOException;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class XMOIOAndCreateTest {
	private XmlIO testIO;
	private XmoCreatorImpl testCreator;

	@Mock
	private FileChooserView fileChooserView;
	@Mock
	private XmoManager xmoManager;
	@Mock
	private StateManager stateManager;
	@Mock
	private ExtendedMeshObjectPropertyListener listener;
	@Mock
	private DefaultScene scene;
	@Mock
	private SelectedFile selectedFile;
	@Mock
	private Node rootNode;
	@Mock
	private Node loadedColladaForChildNode;
	@Mock
	private ExtendedMeshObject xmoRoot;
	@Mock
	private ExtendedMeshObject xmoChild;
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();


	@Before
	public void setUp() throws IOException {
		setupMocks(this);
		folder.create();
		testCreator = new XmoCreatorImpl(xmoManager);
	}

	@Test
	public void testCreateNew() throws IOException {
		File xmoFile = folder.newFile("test.xmo");
		stubReturn(rootNode).on(scene).getRootNode();
		testCreator.create(xmoFile);

		verifyOnce().on(xmoManager).initXmoRoot(any(WrappedXmoRoot.class));

	}


}
