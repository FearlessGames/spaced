package se.ardorgui.lua.wrappers;

import com.ardor3d.extension.ui.UIHud;
import com.ardor3d.extension.ui.UIPanel;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.renderer.Camera;
import org.junit.Before;
import org.junit.Test;
import se.ardorgui.FixedAnchorLayout;
import se.ardorgui.components.area.AnchorPoint;
import se.ardorgui.lua.ArdorUIPrimitives;
import se.ardortech.TextureManager;
import se.fearless.common.lua.LuaVm;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.mock;

public class GuiComponentTest {
	private UIPanel parent;
	private UIPanel componentPanel;

	private ArdorUIPrimitives ardor;

	@Before
	public void setUp() throws Exception {
		parent = new UIPanel();
		parent.setLayout(new FixedAnchorLayout());
		parent.setContentSize(1000, 500);
		componentPanel = new UIPanel();
		componentPanel.setContentSize(200, 100);
		parent.add(componentPanel);
		ardor = new ArdorUIPrimitives(1000, 500, mock(UIHud.class), mock(TextureManager.class), mock(LuaVm.class), mock(DisplaySettings.class),
				mock(NativeCanvas.class), mock(Camera.class));
	}

	@Test
	public void testAdd() throws Exception {
		assertEquals(0, componentPanel.getLocalX());
		assertEquals(0, componentPanel.getLocalY());
	}

	@Test
	public void testBottomLeftToBottomLeft() throws Exception {
		ardor.setPoint(componentPanel, AnchorPoint.BOTTOMLEFT, parent, AnchorPoint.BOTTOMLEFT, 0, 0);
		assertEquals(0, componentPanel.getLocalX());
		assertEquals(0, componentPanel.getLocalY());
	}

	@Test
	public void testTopRightToMidCenter() throws Exception {
		ardor.setPoint(componentPanel, AnchorPoint.TOPRIGHT, parent, AnchorPoint.MIDCENTER, 0, 0);
		assertEquals(300, componentPanel.getLocalX());
		assertEquals(150, componentPanel.getLocalY());
	}

	@Test
	public void testRelativToNonParent() throws Exception {
		UIPanel otherComponentPanel = new UIPanel();
		otherComponentPanel.setContentSize(100, 100);
		parent.add(otherComponentPanel);


		ardor.setPoint(otherComponentPanel, AnchorPoint.BOTTOMLEFT, componentPanel, AnchorPoint.TOPRIGHT, 0, 0);
		assertEquals(200, otherComponentPanel.getLocalX());
		assertEquals(100, otherComponentPanel.getLocalY());
	}

	@Test
	public void testRelativToNonParentTopRightToMidCenter() throws Exception {
		UIPanel otherComponentPanel = new UIPanel();
		otherComponentPanel.setContentSize(100, 100);
		parent.add(otherComponentPanel);

		ardor.setPoint(otherComponentPanel, AnchorPoint.TOPRIGHT, componentPanel, AnchorPoint.MIDCENTER, 0, 0);
		assertEquals(0, otherComponentPanel.getLocalX());
		assertEquals(-50, otherComponentPanel.getLocalY());
	}


	@Test
	public void testRelativToNonParentMovedRelativ() throws Exception {
		UIPanel otherComponentPanel = new UIPanel();
		otherComponentPanel.setContentSize(100, 100);
		parent.add(otherComponentPanel);

		ardor.setPoint(componentPanel, AnchorPoint.BOTTOMRIGHT, parent, AnchorPoint.BOTTOMRIGHT, 0, 0);

		assertEquals(800, componentPanel.getLocalX());
		assertEquals(0, componentPanel.getLocalY());

		ardor.setPoint(otherComponentPanel, AnchorPoint.BOTTOMRIGHT, componentPanel, AnchorPoint.TOPLEFT, 0, 0);
		assertEquals(700, otherComponentPanel.getLocalX());
		assertEquals(100, otherComponentPanel.getLocalY());
	}

}
