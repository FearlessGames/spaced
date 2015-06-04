package se.ardorgui.components.base;

import com.ardor3d.math.ColorRGBA;
import se.ardorgui.components.area.ComponentArea;
import se.ardorgui.components.listeners.ComponentInputListeners;

import java.awt.Insets;
import java.awt.Point;

public class Component {
	private final Point position;
	private final ComponentInputListeners inputListeners;		// Input events, that concern this component
	private final ComponentListeners listeners;
	private final ComponentArea area;
	private ComponentContainer parent;
	private Insets positionConstraints;
	private boolean canBeActive = false;	// Hover over the component makes it active
	private boolean canHaveFocus = false;	// Clicking an active component makes it have focus
	private boolean draggable = false;
	private boolean localEnabled = true;	// TODO: add global enabled
	private boolean localVisible = true;	// TODO: add global visible
	private float fade;						// The fade does affect the children
	private ColorRGBA color;				// The color does not affect the children

	public Component(final ComponentListener componentView, final ComponentArea area) {
		this.area = area;
		listeners = new ComponentListeners();
		if (componentView != null) {
			listeners.add(componentView);
		}
		color = new ColorRGBA(ColorRGBA.WHITE);
		fade = 1.0f;
		parent = null;
		positionConstraints = null;
		canHaveFocus = false;
		draggable = false;
		localEnabled = true;
		localVisible = true;
		position = new Point(0,0);
		inputListeners = new ComponentInputListeners();
	}

	public final boolean isDraggable() {
		return draggable;
	}

	public final void setDraggable(final boolean draggable) {
		this.draggable = draggable;
	}

	public boolean isCanBeActive() {
		return canBeActive;
	}

	public void setCanBeActive(boolean canBeActive) {
		this.canBeActive = canBeActive;
	}

	public final boolean isCanHaveFocus() {
		return canHaveFocus;
	}

	public final void setCanHaveFocus(final boolean canHaveFocus) {
		this.canHaveFocus = canHaveFocus;
	}

	public final Insets getPositionConstraints() {
		return positionConstraints;
	}

	public final void setPositionConstraints(final Insets positionConstraints) {
		this.positionConstraints = positionConstraints;
	}

	public final Point getPosition() {
		return new Point(position);
	}

	public final void setPosition(int posX, int posY) {
		if (positionConstraints != null) {
			posX = Math.min(posX, positionConstraints.right);
			posX = Math.max(posX, positionConstraints.left);
			posY = Math.min(posY, positionConstraints.top);
			posY = Math.max(posY, positionConstraints.bottom);
		}
		position.setLocation(posX, posY);
		listeners.onMove(this);
		notifyAreaChanged();
	}

	public final ComponentInputListeners getInputListeners() {
		return inputListeners;
	}

	public final ComponentListeners getListeners() {
		return listeners;
	}

	public final Point getWorldPosition() {
		if (parent != null) {
			final Point worldPosition = new Point(parent.getWorldPosition());
			worldPosition.x += position.x;
			worldPosition.y += position.y;
			return worldPosition;
		}
		return position;
	}

	public final void setWorldPosition(final int posX, final int posY) {
		Point worldPosition = new Point(0, 0);
		if (parent != null) {
			worldPosition = parent.getWorldPosition();
		}
		setPosition(posX - worldPosition.x, posY - worldPosition.y);
	}

	public final void hide() {
		localVisible = false;
		listeners.onHide(this);
		notifyAreaChanged();
	}

	public final void show() {
		localVisible = true;
		listeners.onShow(this);
		notifyAreaChanged();
	}

	public final boolean isLocalVisible() {
		return localVisible;
	}

	public final boolean isVisible() {
		if (parent != null) {
			return parent.isVisible() && localVisible;
		}
		return localVisible;
	}

	public final boolean isEnabled() {
		if (parent != null) {
			return parent.isEnabled() && localEnabled;
		}
		return localEnabled;
	}

	public final boolean isLocalEnabled() {
		return localEnabled;
	}

	public final void enable() {
		localEnabled = true;
		notifyEnabled();
		notifyAreaChanged();
	}

	public final void disable() {
		localEnabled = false;
		notifyDisabled();
		notifyAreaChanged();
	}

	protected void notifyEnabled() {
		listeners.onEnable(this);
	}

	protected void notifyDisabled() {
		listeners.onDisable(this);
	}

	public final ComponentContainer getParent() {
		return parent;
	}

	public final void setParent(final ComponentContainer parent) {
		this.parent = parent;
	}

	public void setSize(final int width, final int height) {
		area.setSize(width, height);
		listeners.onResize(this);
		notifyAreaChanged();
	}

	// TODO: Try to get rid of the parent
	public final void removeFromParent() {
		if (parent != null) {
			parent.removeComponent(this);
			parent = null;
		}
	}

	public void setFade(final float fade) {
		this.fade = fade;
		listeners.onChangeFade(this);
	}

	public float getFade() {
		return fade;
	}

	public final void setColor(final ColorRGBA color) {
		this.color = color;
		listeners.onChangeColor(this);
	}

	public ColorRGBA getColor() {
		return color;
	}

	public ComponentArea getArea() {
		return area;
	}

	public void notifyAreaChanged() {
		if (parent != null) {
			parent.notifyAreaChanged();
		}
	}

	public Component getHot(final int posX, final int posY) {
		if (isLocalVisible() && isCanBeActive()) {
			if (area.isInside(posX - getWorldPosition().x, posY - getWorldPosition().y)) {
				return this;
			}
		}
		return null;
	}

	public void releaseResources() {
		listeners.onReleaseResources(this);
		removeFromParent();
		color = null;
		inputListeners.clear();
		listeners.clear();
		parent = null;
		positionConstraints = null;
	}

	public void bringToFront() {
		parent.bringToFront(this);
	}
}
