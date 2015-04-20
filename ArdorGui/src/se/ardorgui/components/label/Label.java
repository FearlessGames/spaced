package se.ardorgui.components.label;

import com.ardor3d.ui.text.BMText;
import se.ardorgui.components.area.BasicComponentArea;
import se.ardorgui.components.base.Component;

import java.awt.Point;

public class Label extends Component {
	private String text;
	private float textSize;
	private final LabelListeners listeners;
	private final LabelViewInterface labelView;

	public Label(final LabelViewInterface view, String text, float textSize, final int width, final int height) {
		super(view, new BasicComponentArea(width, height));
		this.textSize = textSize;
		this.labelView = view;
		listeners = new LabelListeners();
		listeners.add(view);
		setText(text);
	}

	public String getText() {
		return text;
	}

	public final void setText(final String text) {
		this.text = text;
		listeners.onTextChanged(this);
		setSize(labelView.getRenderedWidth(), labelView.getRenderedHeight());
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
		listeners.onTextChanged(this);
	}

	public void setAlign(BMText.Align align) {
		labelView.setAlign(align);
	}

	public Point getTextEndPosition() {
		return new Point((int) getPosition().getX() + labelView.getRenderedWidth(), (int) getPosition().getY());
	}

	@Override
	public String toString() {
		return "Label: " + text;
	}
}