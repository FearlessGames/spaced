package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

/**
 * The abstract class <code>DoubleClickMouseListener</code> works exactly like the
 * <code>ClickMouseListener</code> but it requires an implementation of the
 * <code>doDoubleClick</code> method.
 * @author patrik.lindegren
 * @see <code>ClickMouseListener</code>
 */
public abstract class ComponentDoubleClickMouseListener extends ComponentClickMouseListener {
	@Override
	public final void mouseDoubleClicked(ComponentMouseEvent e) {doDoubleClick(e);}
	@Override
	protected abstract void doClick(ComponentMouseEvent e);
	protected abstract void doDoubleClick(ComponentMouseEvent e);
}
