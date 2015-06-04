package se.spaced.shared.util.math.interval;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class IntervalInt {

	private final int start;

	private final int end;

	private IntervalInt() {
		start = 0;
		end = 0;
	}

	public IntervalInt(int start, int end) {
		if (end < start) {
			end = start;
		}
		this.start = start;
		this.end = end;
	}

	@LuaMethod(name = "GetStart")
	public int getStart() {
		return start;
	}

	@LuaMethod(name = "GetEnd")
	public int getEnd() {
		return end;
	}

	/**
	 * Returns true if the point is inside the interval but not on the edge.
	 *
	 * @param point
	 */
	public boolean inside(int point) {
		return start < point && point < end;
	}

	/**
	 * Returns true if the point is inside the interval or on the edge.
	 *
	 * @param point
	 */
	public boolean contains(int point) {
		return start <= point && point <= end;
	}

	/**
	 * Returns true if the other interval is fully contained within this interval.
	 *
	 * @param other
	 */
	public boolean contains(IntervalInt other) {
		return start <= other.start && other.end <= end;
	}

	/**
	 * Returns true if the intervals overlap in anyway.
	 * Does not overlap if they simply touch at the edges.
	 *
	 * @param other
	 */
	public boolean overlaps(IntervalInt other) {
		return (
				this.contains(other) ||
						other.contains(this) ||
						(this.inside(other.start) || this.inside(other.end)) ||
						(other.inside(this.start) || other.inside(this.end)));
	}

	@Override
	public int hashCode() {
		int result = start;
		return 31 * result + end;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final IntervalInt other = (IntervalInt) obj;
		if (end != other.end) {
			return false;
		}
		if (start != other.start) {
			return false;
		}
		return true;
	}

	public IntervalInt merge(IntervalInt other) {
		int start = Math.min(getStart(), other.getStart());
		int end = Math.max(getEnd(), other.getEnd());
		return new IntervalInt(start, end);
	}

	public IntervalInt intersection(IntervalInt other) {
		int start = Math.max(getStart(), other.getStart());
		int end = Math.min(getEnd(), other.getEnd());
		return new IntervalInt(start, end);
	}

	public int size() {
		return end - start;
	}

	@Override
	public String toString() {
		return "[" + start + "-" + end + "]";
	}
}
