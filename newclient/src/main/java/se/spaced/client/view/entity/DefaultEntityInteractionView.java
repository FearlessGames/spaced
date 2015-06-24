package se.spaced.client.view.entity;

import com.ardor3d.intersection.PickResults;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.input.ClientMouseButton;
import se.ardortech.pick.Picker;
import se.fearless.common.util.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.shared.util.ListenerDispatcher;

@Singleton
public class DefaultEntityInteractionView implements EntityInteractionView {
	private final TimeProvider timeProvider;
	private final ListenerDispatcher<EntityViewListener> dispatcher;
	private final Picker picker;
	private final Node entityNode;

	private static final long CLICK_TIMEOUT = 200; // ms
	public static final long HOVER_FREQUENCE = 100; // ms

	private long timePressed;
	private UUID pressedUuid;

	private int mouseX;
	private int mouseY;

	private long lastHover;
	private boolean hoverActive = true;
	private boolean hasHover;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	public DefaultEntityInteractionView(
			TimeProvider timeProvider,
			Picker picker,
			@Named("entityNode") Node entityNode,
			ListenerDispatcher<EntityViewListener> dispatcher) {
		this.timeProvider = timeProvider;
		this.picker = picker;
		this.entityNode = entityNode;
		this.dispatcher = dispatcher;
	}

	@Override
	public void onMouseDown(final int x, final int y, ClientMouseButton mouseButton) {
		hoverActive = false;
		triggerHoverReset();

		PickResults pickResults = picker.pickWithBoundingBox(x, y, entityNode);

		if (pickResults.getNumber() > 0) {
			Spatial spatial = Picker.getSpatial(pickResults.getPickData(0));
			pressedUuid = uuidFromSpatial(spatial);
		} else {
			pressedUuid = null;
		}

		timePressed = timeProvider.now();
	}

	@Override
	public void onMouseUp(final int x, final int y, ClientMouseButton mouseButton) {
		hoverActive = true;

		if (!isClick(timeProvider.now())) {
			return;
		}

		PickResults pickResults = picker.pickWithBoundingBox(x, y, entityNode);

		if (isSamePick(pressedUuid, pickResults)) {
			if (mouseButton == ClientMouseButton.LEFT) {
				dispatcher.trigger().entityLeftClicked(pressedUuid);
			} else if (mouseButton == ClientMouseButton.RIGHT) {
				dispatcher.trigger().entityRightClicked(pressedUuid);
			}
			return;
		}

		if (pressedUuid == null) {
			if (mouseButton == ClientMouseButton.LEFT) {
				dispatcher.trigger().nothingLeftClicked();
			} else if (mouseButton == ClientMouseButton.RIGHT) {
				dispatcher.trigger().nothingRightClicked();
			}
		}

	}

	@Override
	public void onMouseMove(final int x, final int y) {
		mouseX = x;
		mouseY = y;
	}

	@Override
	public void update(double timePerFrame) {
		if (!hoverActive) {
			triggerHoverReset();
			return;
		}

		long now = timeProvider.now();
		if (now - lastHover < HOVER_FREQUENCE) {
			return;
		}
		lastHover = now;

		PickResults pickResults = picker.pickWithBoundingBox(mouseX, mouseY, entityNode);
		UUID uuid = (pickResults.getNumber() > 0) ? uuidFromSpatial(Picker.getSpatial(pickResults.getPickData(0))) : null;

		if (uuid != null) {
			dispatcher.trigger().entityHovered(uuid);
			hasHover = true;
		} else {
			triggerHoverReset();
		}
	}

	private void triggerHoverReset() {
		if (hasHover) {
			hasHover = false;
			dispatcher.trigger().hoverReset();
		}
	}

	private boolean isClick(final long timestamp) {
		return (timestamp - timePressed) < CLICK_TIMEOUT;
	}

	private boolean isSamePick(UUID pressed, PickResults pickResults) {
		if (pressed == null || pickResults.getNumber() == 0) {
			return false;
		}
		Spatial spatial = Picker.getSpatial(pickResults.getPickData(0));
		UUID uuid = uuidFromSpatial(spatial);
		return uuid != null && uuid.equals(pressed);
	}

	private UUID uuidFromSpatial(Spatial spatial) {
		if (spatial == null) {
			return null;
		}
		if (!(spatial.getUserData() instanceof UUID)) {
			return uuidFromSpatial(spatial.getParent());
		}
		UUID uuid = (UUID) spatial.getUserData();
		if (uuid != null) {
			return uuid;
		}

		return uuidFromSpatial(spatial.getParent());
	}
}
