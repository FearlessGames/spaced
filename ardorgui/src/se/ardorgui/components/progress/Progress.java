package se.ardorgui.components.progress;

import se.ardorgui.components.area.BasicComponentArea;
import se.ardorgui.components.base.Component;

public class Progress extends Component {
	private float fillPercent;
	private final ProgressListeners listeners;

	public Progress(final ProgressViewInterface view, final int width, final int height, final float fillPercent) {
		super(view, new BasicComponentArea(width, height));
		listeners = new ProgressListeners();
		listeners.add(view);
	}

	public float getFillPercent() {
		return fillPercent;
	}

	public void setFillPercent(final float fillPercent) {
		this.fillPercent = fillPercent;
		listeners.onFillChanged(this);
	}
}