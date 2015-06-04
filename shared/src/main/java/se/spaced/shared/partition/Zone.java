package se.spaced.shared.partition;

import java.util.concurrent.ConcurrentHashMap;

public class Zone<T> {
	final ConcurrentHashMap<T, Boolean> objects = new ConcurrentHashMap<T, Boolean>();
}
