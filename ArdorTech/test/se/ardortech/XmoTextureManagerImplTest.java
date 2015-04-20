package se.ardortech;

import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Spatial;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.io.StreamLocator;
import se.mockachino.annotations.*;

import java.util.concurrent.ExecutorService;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class XmoTextureManagerImplTest {
	private TextureManagerImpl xmoTextureManagerImpl;
	@Mock
	private StreamLocator streamLocator;
	@Mock
	private Spatial node;
	@Mock
	private ExecutorService executorService;

	@Before
	public void setup() {
		setupMocks(this);
		xmoTextureManagerImpl = new TextureManagerImpl(streamLocator, executorService);
	}

	@Test
	public void testOne() {
		String textureFile = "textures/gear.png";
		xmoTextureManagerImpl.applyTexture(textureFile, node);

		verifyOnce().on(node).setRenderState(any(TextureState.class));
		//not sure how to testMetaNodesExtractedWithNoEntityNameSupplied anything else due to the staticness of TextureManager
	}

}
