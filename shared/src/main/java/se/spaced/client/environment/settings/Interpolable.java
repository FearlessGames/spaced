package se.spaced.client.environment.settings;

public interface Interpolable<T> {
	T interpolate(T other, float pos);
}
