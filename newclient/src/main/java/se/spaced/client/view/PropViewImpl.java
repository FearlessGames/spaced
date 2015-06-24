package se.spaced.client.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.time.TimeProvider;
import se.spaced.client.model.Prop;
import se.spaced.shared.util.ListenerDispatcher;

@Singleton
public class PropViewImpl implements PropView, ActivePropProvider {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final Prop NULL_PROP = new Prop("NullProp", SpacedVector3.ZERO, new SpacedVector3(1, 1, 1), SpacedRotation.IDENTITY);

	static final long CLICK_TIMEOUT = 200; // ms

	private final TimeProvider timeProvider;
	private final ListenerDispatcher<PropSelectionListener> dispatcher;
	private final PropsPicker propsPicker;

	private long timePressed;
	private Prop mousePressedProp = NULL_PROP;
	private Prop activeProp = NULL_PROP;

	@Inject
	public PropViewImpl(TimeProvider timeProvider, ListenerDispatcher<PropSelectionListener> dispatcher, PropsPicker propsPicker) {
		this.timeProvider = timeProvider;
		this.dispatcher = dispatcher;
		this.propsPicker = propsPicker;
	}

	@Override
	public void onMouseDown(int x, int y) {
		timePressed = timeProvider.now();
		PropsPicker.PickResult pick = propsPicker.pick(x, y);
		if (pick.hasResult()) {
			mousePressedProp = pick.getProp();
		}
	}

	@Override
	public void onMouseMove(int newX, int newY) {
		// Ignored
	}

	@Override
	public void onMouseUp(int x, int y) {
		long now = timeProvider.now();
		if (!isClick(now)) {
			return;
		}
		log.debug("mouse up was real click");
		PropsPicker.PickResult pick = propsPicker.pick(x, y);
		if (pick.hasResult()) {
			Prop pickedProp = pick.getProp();
			log.info("Picked a prop {}", pickedProp);
			if (pickedProp.equals(mousePressedProp) && !pickedProp.equals(activeProp)) {
				setProp(mousePressedProp);
			}
		}
		timePressed = now;
		mousePressedProp = NULL_PROP;
	}

	private boolean isClick(final long timestamp) {
		return (timestamp - timePressed) < CLICK_TIMEOUT;
	}


	@Override
	public boolean hasActiveProp() {
		return activeProp != NULL_PROP;
	}

	@Override
	public Prop getActiveProp() {
		return activeProp;
	}

	@Override
	public void setActiveProp(Prop prop) {
		log.info("Setting active prop {}", prop);
		setProp(prop);
	}

	private void setProp(Prop prop) {
		Prop old = activeProp;
		activeProp = prop;
		dispatcher.trigger().activePropChanged(activeProp, old);
	}
}
