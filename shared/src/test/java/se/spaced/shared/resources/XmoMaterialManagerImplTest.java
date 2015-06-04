package se.spaced.shared.resources;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.SceneHints;
import org.junit.Before;
import org.junit.Test;
import se.mockachino.annotations.*;
import se.spaced.shared.model.xmo.Blending;
import se.spaced.shared.model.xmo.Material;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

import static se.mockachino.Mockachino.*;


public class XmoMaterialManagerImplTest {
	@Mock
	Spatial spatial;
	@Mock
	XmlIO xmlIO;
	String materialResource = "materialResource";
	Material material;
	XmoMaterialManagerImpl xmoMaterialManager;
	@Mock
	private SceneHints sceneHints;

	@Before
	public void setup() {
		setupMocks(this);

		ColorRGBA color = new ColorRGBA(1, 1, 1, 1);
		material = new Material(color, color, color, color, 1.0f, CullState.Face.Back,
				new Blending(BlendState.SourceFunction.ConstantColor, BlendState.DestinationFunction.ConstantColor, true, 0));

		xmoMaterialManager = new XmoMaterialManagerImpl(xmlIO, new CacheManager());
	}

	@Test
	public void testApplyCorrectWithNotPreviouslyCachedMaterial() throws XmlIOException {
		stubReturn(material).on(xmlIO).load(Material.class, materialResource);
		stubReturn(sceneHints).on(spatial).getSceneHints();

		xmoMaterialManager.applyMaterial(materialResource, spatial);
		verifyOnce().on(xmlIO).load(Material.class, materialResource);
	}

	@Test
	public void testApplyCorrectWithCache() throws XmlIOException {
		stubReturn(material).on(xmlIO).load(Material.class, materialResource);
		stubReturn(sceneHints).on(spatial).getSceneHints();

		xmoMaterialManager.applyMaterial(materialResource, spatial);
		xmoMaterialManager.applyMaterial(materialResource, spatial);
		verifyOnce().on(xmlIO).load(Material.class, materialResource);
	}

	@Test
	public void testInvalidateCache() throws XmlIOException {
		stubReturn(material).on(xmlIO).load(Material.class, materialResource);
		stubReturn(sceneHints).on(spatial).getSceneHints();

		xmoMaterialManager.applyMaterial(materialResource, spatial);
		xmoMaterialManager.invalidateCache(materialResource);
		xmoMaterialManager.applyMaterial(materialResource, spatial);
		verifyExactly(2).on(xmlIO).load(Material.class, materialResource);
	}
}
