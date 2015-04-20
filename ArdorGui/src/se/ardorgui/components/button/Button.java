package se.ardorgui.components.button;

import se.ardorgui.components.area.ComponentArea;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentListenerAdapter;

public class Button extends Component {
	private ButtonState state;
	private final ButtonListeners listeners;

	public Button(final ButtonViewInterface buttonView, final ComponentArea componentArea) {
		super(buttonView, componentArea);
		listeners = new ButtonListeners();
		listeners.add(buttonView);
		getInputListeners().getMouseListeners().add(new ButtonMouseListener(this));
		getListeners().add(new ComponentListenerAdapter() {
			@Override
			public void onHide(final Component component) {
				setState(ButtonState.UP);
			}

			@Override
			public void onDisable(final Component component) {
				setState(ButtonState.UP);
			}

			@Override
			public void onEnable(final Component component) {
				setState(ButtonState.UP);
			}
		});
		setState(ButtonState.UP);
		setCanBeActive(true);
	}

	void setState(final ButtonState state) {
		this.state = state;
		listeners.onChangeState(this);
	}

	public ButtonState getState() {
		return state;
	}
}