package se.ardorgui.components.listeners;

/**
 * ComponentInputListeners contains listeners to events fired by GuiInputManager.
 * The listeners can be obtained and added from the Component.
 */
public class ComponentInputListeners {
	private final ComponentMouseListeners mouseListeners;
	private final ComponentMouseMotionListeners mouseMotionListeners;
	private final ComponentMouseWheelListeners mouseWheelListeners;
	private final ComponentKeyListeners keyListeners;
	private final ComponentFocusListeners focusListeners;
	private final ComponentDraggedListeners draggedListeners;
	private final ComponentDragReceiverListeners dragReceiverListeners;

	public ComponentInputListeners() {
		mouseListeners = new ComponentMouseListeners();
		mouseMotionListeners = new ComponentMouseMotionListeners();
		keyListeners = new ComponentKeyListeners();
		focusListeners = new ComponentFocusListeners();
		mouseWheelListeners = new ComponentMouseWheelListeners();
		draggedListeners = new ComponentDraggedListeners();
		dragReceiverListeners = new ComponentDragReceiverListeners();
	}

	public ComponentKeyListeners getKeyListeners() {
		return keyListeners;
	}

	public ComponentMouseListeners getMouseListeners() {
		return mouseListeners;
	}

	public ComponentMouseMotionListeners getMouseMotionListeners() {
		return mouseMotionListeners;
	}

	public ComponentFocusListeners getFocusListeners() {
		return focusListeners;
	}

	public ComponentMouseWheelListeners getMouseWheelListeners() {
		return mouseWheelListeners;
	}

	public ComponentDraggedListeners getDraggedListeners() {
		return draggedListeners;
	}

	public ComponentDragReceiverListeners getDragReceiverListeners() {
		return dragReceiverListeners;
	}

	public void clear() {
		mouseListeners.clear();
		mouseMotionListeners.clear();
		keyListeners.clear();
		focusListeners.clear();
		mouseWheelListeners.clear();
		draggedListeners.clear();
		dragReceiverListeners.clear();
	}
}