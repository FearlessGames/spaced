package se.ardorgui.view.views;

import com.ardor3d.ui.text.BMText;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.label.Label;
import se.ardorgui.components.label.LabelViewInterface;
import se.ardorgui.view.mesh.TextQuad;

public class LabelView extends ComponentLeafView<TextQuad> implements LabelViewInterface {


	public LabelView(final TextQuad textQuad) {
		super(textQuad);
	}

	@Override
	public void onResize(final Component component) {
		updatePosition(component);
	}

	// TODO: should this be done here or in the text mesh?
	// TODO: this might crash if there is no text
	private void updatePosition(final Component component) {
		final TextQuad text = getSpatial();
		text.setTranslation(component.getPosition().getX(), component.getPosition().getY(), 0);
	}

	@Override
	public void setAlign(BMText.Align align) {
  		getSpatial().setAlign(align);
	}

	@Override
	public int getRenderedWidth() {
		return (int) getSpatial().getWidth();
	}

	@Override
	public int getRenderedHeight() {
		return (int) getSpatial().getHeight();
	}

	@Override
	public void onTextChanged(final Label label) {
		getSpatial().setText(label.getText());
		getSpatial().setFontScale(label.getTextSize());
	}
}
