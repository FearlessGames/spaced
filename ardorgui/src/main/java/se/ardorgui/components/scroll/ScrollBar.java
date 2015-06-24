package se.ardorgui.components.scroll;

import se.ardorgui.components.area.ComponentArea;
import se.ardorgui.components.base.ComponentContainer;
import se.ardorgui.components.base.ComponentContainerViewInterface;
import se.ardorgui.components.button.Button;
import se.ardorgui.components.listeners.ComponentDraggedListenerAdapter;
import se.ardorgui.input.events.ComponentMouseEvent;

import java.awt.Insets;

public class ScrollBar extends ComponentContainer {
	private final ScrollBarListeners scrollBarListeners;
	private final Button button;
	private final ScrollBarData scrollBarData;
	private final ScrollBarDirection scrollBarDirection;

	public ScrollBar(final ComponentContainerViewInterface view, final ComponentArea area, final Button button, final ScrollBarData scrollBarData, final ScrollBarDirection scrollBarDirection) {
		super(view, area);
		this.button = button;
		this.scrollBarData = scrollBarData;
		this.scrollBarDirection = scrollBarDirection;
		this.scrollBarListeners = new ScrollBarListeners();
		addComponent(button);
		button.setDraggable(true);
		update();
		button.getInputListeners().getDraggedListeners().add(new ComponentDraggedListenerAdapter() {
			@Override
			public void dragged(final ComponentMouseEvent e) {
				readValue();
			}
			@Override
			public void dragEnded(final ComponentMouseEvent e) {
				updateButtonPosition();
			}
		});
	}

	private void updateButtonPositionConstraints() {
		if (ScrollBarDirection.HORIZONTAL.equals(scrollBarDirection)) {
			button.setPositionConstraints(
				new Insets(0, -getArea().getWidth() / 2 + button.getArea().getWidth() / 2,
						   0,  getArea().getWidth() / 2 - button.getArea().getWidth() / 2));
		} else {
			button.setPositionConstraints(
				new Insets( getArea().getHeight() / 2 - button.getArea().getHeight() / 2, 0,
						   -getArea().getHeight() / 2 + button.getArea().getHeight() / 2, 0));
		}
	}

	private void readValue() {
		float value = 0;
		if (ScrollBarDirection.HORIZONTAL.equals(scrollBarDirection)) {
			final int scrollWidth = getArea().getWidth() - button.getArea().getWidth();
			value = (button.getPosition().x + scrollWidth / 2.0f) / scrollWidth;
		} else {
			final int scrollHeight = getArea().getHeight() - button.getArea().getHeight();
			value = (button.getPosition().y + scrollHeight / 2.0f) / scrollHeight;
		}
		scrollBarData.setValuePercentage(value);
		scrollBarListeners.valueChanged(this);
	}

	private void updateButtonSize() {
		final float sizePercentage = scrollBarData.getSizePercentage();
		if (ScrollBarDirection.HORIZONTAL.equals(scrollBarDirection)) {
			button.setSize((int)(getArea().getWidth() * sizePercentage), button.getArea().getHeight());
		} else {
			button.setSize(button.getArea().getWidth(), (int)(getArea().getHeight() * sizePercentage));
		}
	}

	private void updateButtonPosition() {
		final float valuePercentage = scrollBarData.getValuePercentage();
		if (ScrollBarDirection.HORIZONTAL.equals(scrollBarDirection)) {
			final int scrollWidth = getArea().getWidth() - button.getArea().getWidth();
			button.setPosition(((int)((valuePercentage * scrollWidth - scrollWidth / 2.0f))), 0);
		} else {
			final int scrollHeight = getArea().getHeight() - button.getArea().getHeight();
			button.setPosition(0, ((int)((valuePercentage * scrollHeight - scrollHeight / 2.0f))));
		}
	}

	public Button getButton() {
		return button;
	}

	@Override
	public void setSize(final int width, final int height) {
		super.setSize(width, height);
		update();
	}

	private void update() {
		updateButtonSize();
		updateButtonPositionConstraints();
		updateButtonPosition();
	}

	public int getValue() {
		return scrollBarData.getValue();
	}

	public void setValue(final int value) {
		scrollBarData.setValue(value);
		updateButtonPosition();
		scrollBarListeners.valueChanged(this);
	}

	public int getMax() {
		return scrollBarData.getMax();
	}

	public void setMax(final int max) {
		scrollBarData.setMax(max);
		updateButtonSize();
		updateButtonPosition();
		scrollBarListeners.maxChanged(this);
	}

	public int getSize() {
		return scrollBarData.getSize();
	}

	public void setSize(final int size) {
		scrollBarData.setSize(size);
		updateButtonSize();
		updateButtonPosition();
		scrollBarListeners.sizeChanged(this);
	}

	public ScrollBarListeners getScrollBarListeners() {
		return scrollBarListeners;
	}
}
