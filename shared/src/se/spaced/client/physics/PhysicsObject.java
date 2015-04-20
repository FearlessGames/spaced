package se.spaced.client.physics;

public class PhysicsObject<T> {
	private final T obj;

	public PhysicsObject(T obj) {
		this.obj = obj;
	}

	public T getObject() {
		return obj;
	}
}
