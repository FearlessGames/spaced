package se.spaced.shared.util.guice.dependencytool;

import java.util.HashSet;
import java.util.Set;

public class VisitResult<T> {
	public enum State {
		OPEN, CLOSED, INVALID
	}

	private State state;
	private int level;

	private final T owner;
	private final Set<T> elements;

	public VisitResult(State state, T element) {
		this.level = 0;
		this.state = state;
		this.owner = element;
		this.elements = new HashSet<T>();
		this.elements.add(element);
	}

	public State getState() {
		return state;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Set<T> getElements() {
		return elements;
	}

	public T getOwner() {
		return owner;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof VisitResult)) {
			return false;
		}

		VisitResult that = (VisitResult) o;

		return elements.equals(that.elements);
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public String toString() {
		return "VisitResult{" +
				"state=" + state +
				", owner=" + owner +
				", level=" + level +
				", elements=" + elements +
				'}' + System.identityHashCode(this);
	}
}
