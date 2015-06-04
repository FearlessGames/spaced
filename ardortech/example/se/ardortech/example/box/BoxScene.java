package se.ardortech.example.box;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.scenegraph.shape.Box;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.example.BaseExampleScene;
import se.ardortech.render.DebugRender;
import se.ardortech.render.ScreenshotRender;

@Singleton
public class BoxScene extends BaseExampleScene {
	private Box box;

	@Inject
	public BoxScene(DebugRender debugRender, ScreenshotRender screenshotRender) {
		super(debugRender, screenshotRender);
	}

	@Override
	protected void setUp() {
		box = new Box("Box", new Vector3(0,0,0), 1, 1, 1);
		final MaterialState ms = new MaterialState();
        ms.setColorMaterial(ColorMaterial.Diffuse);
        box.setRenderState(ms);
		box.setTranslation(new Vector3(0,0,-10));
		box.setModelBound(new BoundingBox());
		box.updateWorldBound(true);
		box.setRandomColors();
		root.attachChild(box);
	}

	public Box getBox() {
		return box;
	}
}