package se.ardorgui.components.area;

import com.ardor3d.extension.ui.util.Alignment;
import com.google.common.collect.Maps;

import java.awt.Dimension;
import java.awt.Point;
import java.util.EnumMap;

public enum AnchorPoint {
	TOPLEFT(Alignment.TOP_LEFT),
	TOPCENTER(Alignment.TOP),
	TOPRIGHT(Alignment.TOP_RIGHT),
	MIDLEFT(Alignment.LEFT),
	MIDCENTER(Alignment.MIDDLE),
	MIDRIGHT(Alignment.RIGHT),
	BOTTOMLEFT(Alignment.BOTTOM_LEFT),
	BOTTOMCENTER(Alignment.BOTTOM),
	BOTTOMRIGHT(Alignment.BOTTOM_RIGHT);

	private static final EnumMap<Alignment, AnchorPoint> ardorToSpaced = Maps.newEnumMap(Alignment.class);

	static {
		for (AnchorPoint value : AnchorPoint.values()) {
			ardorToSpaced.put(value.getArdorAlignment(), value);
		}
	}

	private final Alignment ardorAlign;

	AnchorPoint(Alignment ardorAlign) {
		this.ardorAlign = ardorAlign;
	}

	public Alignment getArdorAlignment() {
		return ardorAlign;
	}

	public static AnchorPoint fromArdor(Alignment alignment) {
		return ardorToSpaced.get(alignment);
	}

	public static Point getAnchorPointOffsetFromLowerLeft(Dimension dimension, String anchor) {
		return getAnchorPointOffsetFromLowerLeft(dimension, AnchorPoint.valueOf(anchor));
	}

	public static Point getAnchorPointOffsetFromLowerLeft(Dimension dimension, String anchor, Point defaultPoint) {
		return getAnchorPointOffsetFromLowerLeft(dimension, AnchorPoint.valueOf(anchor), defaultPoint);
	}

	public static Point getAnchorPointOffsetFromLowerLeft(Dimension dimension, AnchorPoint anchorPoint) {
		return getAnchorPointOffsetFromLowerLeft(dimension, anchorPoint, new Point(0,0));
	}

	public static Point getAnchorPointOffsetFromLowerLeft(Dimension dimension, AnchorPoint anchorPoint, Point defaultPoint) {
		int w = (int) dimension.getWidth();
		int h = (int) dimension.getHeight();
		switch (anchorPoint) {
			case BOTTOMLEFT:
				return new Point(0, 0);
			case TOPCENTER:
				return new Point((w / 2), h);
			case TOPLEFT:
				return new Point(0, h);
			case TOPRIGHT:
				return new Point(w, h);
			case BOTTOMCENTER:
				return new Point((w / 2), 0);
			case BOTTOMRIGHT:
				return new Point(w, 0);
			case MIDCENTER:
				return new Point((w / 2), (h / 2));
			case MIDLEFT:
				return new Point(0, (h / 2));
			case MIDRIGHT:
				return new Point(w, (h / 2));
		}
		return defaultPoint;
	}
}
