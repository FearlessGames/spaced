package se.ardorgui.components.editbox;

import se.ardorgui.components.area.AnchorPoint;
import se.ardorgui.components.area.ComponentArea;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentContainer;
import se.ardorgui.components.base.ComponentContainerViewInterface;
import se.ardorgui.components.base.ComponentListenerAdapter;
import se.ardorgui.components.label.Label;
import se.ardorgui.components.listeners.ComponentFocusListener;
import se.ardorgui.input.events.ComponentFocusEvent;

import java.awt.Dimension;
import java.awt.Point;

public class EditBox extends ComponentContainer {
	private final EditBoxListeners editBoxlisteners;
	private final Label label;
	private final Component cursor;	// TODO: implement the cursor in the ComponentView, add cursor character position and enabled/disabled here
	private String text;
	private boolean passwordField;
	private static final int PADDING = 5;

	public EditBox(final ComponentContainerViewInterface view, final ComponentArea area, final Label label, final Component cursor, final String text, final boolean passwordField) {
		super(view, area);
		this.label = label;
		this.cursor = cursor;
		addComponent(label);
		addComponent(cursor);

		this.passwordField = passwordField;
		this.getListeners().add(new ComponentListenerAdapter() {
			@Override
			public void onResize(final Component component) {
				label.setSize(component.getArea().getWidth() - 10, 0);
			}
		});
		this.getInputListeners().getKeyListeners().add(new TextInputListener(this));
		this.getInputListeners().getFocusListeners().add(new ComponentFocusListener() {
			@Override
			public void focusGained(final ComponentFocusEvent focusEvent) {
				cursor.show();
			}
			@Override
			public void focusLost(final ComponentFocusEvent focusEvent) {
				cursor.hide();
			}
		});
		editBoxlisteners = new EditBoxListeners();
		setCanBeActive(true);
		setCanHaveFocus(true);
		setText(text);
		cursor.hide();
	}

	public String getText() {
		return text;
	}

	public void setText(final String str) {
		this.text = str;
		if (isPasswordField()) {
			final StringBuffer buffer = new StringBuffer(str.length());
			for (int i = 0; i < str.length(); i++) {
				buffer.append('*');
			}
			label.setText(buffer.toString());
		} else {
			label.setText(str);
		}
		Point offsetFromCenter = AnchorPoint.getAnchorPointOffsetFromLowerLeft(new Dimension(getArea().getWidth(),
				getArea().getHeight()), AnchorPoint.MIDLEFT);
		label.setPosition((int) offsetFromCenter.getX() + PADDING, (int) offsetFromCenter.getY());
		Point textEndPosition = label.getTextEndPosition();
		cursor.setPosition(textEndPosition.x, textEndPosition.y);
		editBoxlisteners.onTextChanged(this);
	}

	public Label getLabel() {
		return label;
	}

	public boolean isPasswordField() {
		return passwordField;
	}

	public void setPasswordField(final boolean passwordField) {
		this.passwordField = passwordField;
	}

	public EditBoxListeners getEditBoxListeners() {
		return editBoxlisteners;
	}

	@Override
	public String toString() {
		return "EditBox";
	}
}