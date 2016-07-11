package se.spaced.client.ardor;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.input.GrabbedState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseManager;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.lua.bindings.LuaKeyBindings;
import se.ardortech.input.ClientMouseButton;
import se.ardortech.input.KeyListener;
import se.ardortech.input.MouseListener;
import se.ardortech.render.ScreenshotRender;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.model.control.CharacterControlLuaHandler;
import se.spaced.client.model.control.CharacterControlProvider;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.view.PropView;
import se.spaced.client.view.entity.EntityInteractionView;

import java.util.EnumMap;

public class GameInputListener implements MouseListener, KeyListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final SpacedScene scene;
	private final ScreenshotRender screenshotRender;
	private final EntityInteractionView entityInteraction;
	private final LuaKeyBindings luaKeyBindings;
	private final CharacterControlProvider characterControlProvider;
	private final PropView propView;
	private final SpacedGui spacedGui;
	private final ServerConnection serverConnection;
	private final CharacterControlLuaHandler characterControl;
	private final MouseManager mouseManager;
	private final EnumMap<ClientMouseButton, Boolean> buttonPressedMap = Maps.newEnumMap(ClientMouseButton.class);
	private final MouseWindowEdgeWorkaround mouseWindowEdgeWorkaround;

	@Inject
	public GameInputListener(
			ScreenshotRender screenshotRender, SpacedScene scene, LuaKeyBindings luaKeyBindings,
			CharacterControlProvider characterControlProvider,
			PropView propView,
			SpacedGui spacedGui,
			ServerConnection serverConnection,
			EntityInteractionView entityInteraction,
			CharacterControlLuaHandler characterControl,
			MouseManager mouseManager,
			DisplaySettings displaySettings) {
		this.screenshotRender = screenshotRender;
		this.scene = scene;
		this.luaKeyBindings = luaKeyBindings;
		this.characterControlProvider = characterControlProvider;
		this.propView = propView;
		this.spacedGui = spacedGui;
		this.serverConnection = serverConnection;
		this.entityInteraction = entityInteraction;
		this.characterControl = characterControl;
		this.mouseManager = mouseManager;

		mouseWindowEdgeWorkaround = new MouseWindowEdgeWorkaround(mouseManager,
				displaySettings);

		buttonPressedMap.put(ClientMouseButton.LEFT, false);
		buttonPressedMap.put(ClientMouseButton.RIGHT, false);
	}

	private boolean mouseDown(ClientMouseButton clientMouseButton) {
		return buttonPressedMap.get(clientMouseButton);
	}

	@Override
	public boolean onButton(ClientMouseButton clientMouseButton, final boolean pressed, final int x, final int y) {
		buttonPressedMap.put(clientMouseButton, pressed);

		if (mouseDown(ClientMouseButton.LEFT) && mouseDown(ClientMouseButton.RIGHT)) {
			characterControl.mouseMoveForward();
		} else {
			characterControl.mouseMoveForwardStop();
		}

		if (mouseDown(ClientMouseButton.LEFT) || mouseDown(ClientMouseButton.RIGHT)) {
			entityInteraction.onMouseDown(x, y, clientMouseButton);
			propView.onMouseDown(x, y);
			mouseManager.setGrabbed(GrabbedState.GRABBED);

		}

		if (!mouseDown(ClientMouseButton.LEFT) && !mouseDown(ClientMouseButton.RIGHT)) {
			entityInteraction.onMouseUp(x, y, clientMouseButton);
			propView.onMouseUp(x, y);
			mouseManager.setGrabbed(GrabbedState.NOT_GRABBED);
		}

		characterControl.onMouseButton(clientMouseButton, pressed, x, y);
		characterControlProvider.onMouseButton(clientMouseButton, pressed, x, y);

		return false;
	}

	@Override
	public void onMove(int deltaX, int deltaY, final int newX, final int newY) {
		log.debug("onMove ({}, {}) - delta {}, {}", newX, newY, deltaX, deltaY);
		if (mouseDown(ClientMouseButton.LEFT) || mouseDown(ClientMouseButton.RIGHT)) {
			//mouseWindowEdgeWorkaround.grab(newX, newY);
			//deltaX = mouseWindowEdgeWorkaround.getLastDeltaX();
			//deltaY = mouseWindowEdgeWorkaround.getLastDeltaY();
			characterControlProvider.getCurrentControl().onMouseMove(-deltaX,
					-deltaY,
					mouseDown(ClientMouseButton.LEFT),
					mouseDown(ClientMouseButton.RIGHT));
		} else {
			//mouseWindowEdgeWorkaround.release();
			propView.onMouseMove(newX, newY);
			entityInteraction.onMouseMove(newX, newY);
		}
	}

	@Override
	public void onWheel(final int wheelDelta, final int x, final int y) {
		characterControlProvider.getCurrentControl().onMouseWheel(wheelDelta);
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed) {
			// TODO: move many of these binds to lua!
			if (keyCode == Key.ESCAPE) {
				serverConnection.getReceiver().connection().logout();
			} else if (keyCode == Key.F12) {
				screenshotRender.takeScreenShot();
			} else if (keyCode == Key.L) {
				scene.toggleLight();
			} else if (keyCode == Key.T) {
				scene.toggleWireframe();
			} else if (keyCode == Key.F3) {
				spacedGui.reload();
			} else if (keyCode == Key.F11) {
				spacedGui.toggle();
			} else if (luaKeyBindings.onKey(character, keyCode, pressed)) {
				return true;
			}
		} else {
			if (luaKeyBindings.onKey(character, keyCode, pressed)) {
				return true;
			}
		}
		return false;
	}


}