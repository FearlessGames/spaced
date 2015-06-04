package se.spaced.shared.world.walkmesh;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.PointSequence;
import se.spaced.shared.world.area.Polygon;

import java.util.List;

public class WalkmeshConnection implements PointSequence {
	private UUID from;
	private UUID to;

	private SpacedVector3 point1;
	private SpacedVector3 point2;

	private Direction direction;

	public WalkmeshConnection(UUID from, UUID to, SpacedVector3 point1, SpacedVector3 point2, Direction direction) {
		this.from = from;
		this.to = to;
		this.point1 = point1;
		this.point2 = point2;
		this.direction = direction;
	}

	public UUID getFrom() {
		return from;
	}

	public UUID getTo() {
		return to;
	}

	public static WalkmeshConnection create(Polygon polygon1, Polygon polygon2) {
		List<SpacedVector3> points1 = Lists.newArrayList(polygon1.getPoints());
		points1.retainAll(polygon2.getPoints());
		return new WalkmeshConnection(polygon1.getId(), polygon2.getId(), points1.get(0), points1.get(1), Direction.UNIDIRECTIONAL);
	}

	public Direction getDirection() {
		return direction;
	}

	@Override
	public ImmutableList<AreaPoint> getAreaPoints() {
		return ImmutableList.of(new AreaPoint(point1, SpacedRotation.IDENTITY), new AreaPoint(point2, SpacedRotation.IDENTITY));
	}

	public SpacedVector3 getPoint1() {
		return point1;
	}

	public SpacedVector3 getPoint2() {
		return point2;
	}
}
