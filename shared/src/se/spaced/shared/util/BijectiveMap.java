package se.spaced.shared.util;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

public class BijectiveMap<X, Y> {
	private final Map<X, Y> xMap = Maps.newHashMap();
	private final Map<Y, X> yMap = Maps.newHashMap();
	private final X missingX;
	private final Y missingY;

	public BijectiveMap() {
		this(null, null);
	}

	public BijectiveMap(X missingX, Y missingY) {
		this.missingX = missingX;
		this.missingY = missingY;
	}

	public void connect(X x, Y y) {
		removeX(x);
		removeY(y);

		xMap.put(x, y);
		yMap.put(y, x);
	}

	public void removeX(X x) {
		Y y = xMap.get(x);
		remove(x, y);
	}

	public void removeY(Y y) {
		X x = yMap.get(y);
		remove(x, y);
	}

	public Y getY(X x) {
		Y y = xMap.get(x);
		if (y == null) {
			return missingY;
		}
		return y;
	}

	public X getX(Y y) {
		X x = yMap.get(y);
		if (x == null) {
			return missingX;
		}
		return x;
	}

	private void remove(X x, Y y) {
		xMap.remove(x);
		yMap.remove(y);
	}

	public Set<X> getAllX() {
		return xMap.keySet();
	}

	public Set<Y> getAllY() {
		return yMap.keySet();
	}

}
