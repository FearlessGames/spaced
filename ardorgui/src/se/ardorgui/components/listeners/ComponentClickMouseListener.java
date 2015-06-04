package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

/**
 * The <code>ClickMouseListener</code> is a mouseListener used to trigger events when
 * a component has been clicked. The name is a bit missleading since it also handles double
 * clicks.
 * @author patrik.lindegren
 * @see <code>DoubleClickMouseListener</code>
 */
public abstract class ComponentClickMouseListener extends ComponentMouseListenerAdapter {
	/**
	 * {@link #DOUBLE_CLICK_TIME} is the maximum time between two clicks needed to trigger a
	 * double click.
	 */
	protected static final long DOUBLE_CLICK_TIME = 200;
	protected long lastClickTime = -1;

	@Override
	public final void mouseClicked(ComponentMouseEvent e) {
		long time = System.currentTimeMillis();
		doClick(e);
		if (time < lastClickTime + DOUBLE_CLICK_TIME) {
			mouseDoubleClicked(e);
		}
		lastClickTime = time;
	}

	public void mouseDoubleClicked(ComponentMouseEvent e) {}
	protected abstract void doClick(ComponentMouseEvent e);
}