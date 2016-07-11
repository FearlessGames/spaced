package se.ardortech;

import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Spatial;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.io.IOLocator;
import se.mockachino.annotations.Mock;

import java.util.concurrent.ExecutorService;

import static se.mockachino.Mockachino.setupMocks;
import static se.mockachino.Mockachino.verifyOnce;
import static se.mockachino.matchers.Matchers.any;

public class XmoTextureManagerImplTest {
	private TextureManagerImpl xmoTextureManagerImpl;
	@Mock
	private IOLocator streamLocator;
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
