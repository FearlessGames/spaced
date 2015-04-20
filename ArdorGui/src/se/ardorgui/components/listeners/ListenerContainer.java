package se.ardorgui.components.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ListenerContainer<E> {
	protected List<E> listeners;

	protected ListenerContainer() {
		listeners = Collections.synchronizedList(new ArrayList<E>());
	}

	public void add(final E listener) {
		listeners.add(listener);
	}

	public void remove(final E listener) {
		listeners.remove(listener);
	}

	public void clear() {
		listeners.clear();
	}

	public List<E> getListeners() {
		return listeners;
	}
}
