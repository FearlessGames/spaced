package se.spaced.shared.world.walkmesh;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.xml.SharedXStreamRegistry;

import javax.swing.JFileChooser;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WalkmeshAutoConnector {
	private static final Logger log = LoggerFactory.getLogger(WalkmeshAutoConnector.class);
	public static final Predicate<List<Polygon>> NOT_EQUAL_ELEMENTS = new Predicate<List<Polygon>>() {
		@Override
		public boolean apply(List<Polygon> walkmeshPolygons) {
			return walkmeshPolygons.get(0) != walkmeshPolygons.get(1);
		}
	};
	public static final Predicate<List<Polygon>> SHARES_TWO_POINTS = new Predicate<List<Polygon>>() {
		@Override
		public boolean apply(List<Polygon> walkmeshPolygons) {
			return sharesTwoPoints(walkmeshPolygons.get(0), walkmeshPolygons.get(1));
		}
	};
	public static final double CLOSE_ENOUGH_DISTANCE = 0.5;

	private WalkmeshAutoConnector() {
	}

	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser(new File("shared/resources/mobs/navmesh"));
		int choice = fileChooser.showOpenDialog(null);
		if (choice == JFileChooser.CANCEL_OPTION) {
			return;
		}
		XStream xStream = new XStream(new DomDriver());
		xStream.setMode(XStream.NO_REFERENCES);
		SharedXStreamRegistry xStreamRegistry = new SharedXStreamRegistry();
		xStreamRegistry.registerDefaultsOn(xStream);

		File selectedFile = fileChooser.getSelectedFile();
		Walkmesh walkmesh = (Walkmesh) xStream.fromXML(selectedFile);

		Iterable<List<Polygon>> allAdjecent = extractAdjecentPolygons(walkmesh);
		Map<UnorderedPair<Polygon>, WalkmeshConnection> connectionMap = Maps.newHashMap();
		for (List<Polygon> adjecent : allAdjecent) {
			Polygon from = adjecent.get(0);
			Polygon to = adjecent.get(1);
			Iterable<List<SpacedVector3>> pointsInCommon = getPointsInCommon(from, to);
			SpacedVector3 point1 = Iterables.get(pointsInCommon, 0).get(0);
			SpacedVector3 point2 = Iterables.get(pointsInCommon, 1).get(0);
			connectionMap.put(new UnorderedPair<Polygon>(from, to), new WalkmeshConnection(from.getId(), to.getId(), point1, point2, Direction.BIDIRECTIONAL));
		}
		log.info("{} bidirectional connections", connectionMap.size());
		for (WalkmeshConnection walkmeshConnection : connectionMap.values()) {
			System.out.println(xStream.toXML(walkmeshConnection) + "\n");
		}
	}

	public static Iterable<List<Polygon>> extractAdjecentPolygons(Walkmesh walkmesh) {
		ImmutableSet<Polygon> walkmeshPolygons = walkmesh.getPolygons();
		log.info("{} polygons in input file", walkmeshPolygons.size());
		Set<List<Polygon>> allCombinations = Sets.cartesianProduct(walkmeshPolygons, walkmeshPolygons);
		log.info("That makes for {} pairs of polygons", allCombinations.size());
		Iterable<List<Polygon>> allNonSelfCombinations = Iterables.filter(allCombinations, NOT_EQUAL_ELEMENTS);
		log.info("When pairs of same are removed {} remains", Iterables.size(allNonSelfCombinations));

		Iterable<List<Polygon>> sharingTwoPoints = Iterables.filter(allNonSelfCombinations, SHARES_TWO_POINTS);
		int size = Iterables.size(sharingTwoPoints);
		log.info("{} have 2 matching points", size);
		return sharingTwoPoints;
	}

	private static boolean sharesTwoPoints(Polygon walkmeshPolygon1, Polygon walkmeshPolygon2) {
		Iterable<List<SpacedVector3>> allCommonPoints = getPointsInCommon(walkmeshPolygon1, walkmeshPolygon2);
		int size = Iterables.size(allCommonPoints);
		if (size == 2) {
			return true;
		} else if (size > 2) {
			log.warn("{} points in common {} and {}", new Object[]{size, walkmeshPolygon1, walkmeshPolygon2});
		}
		return false;
	}

	private static Iterable<List<SpacedVector3>> getPointsInCommon(
			Polygon walkmeshPolygon1,
			Polygon walkmeshPolygon2) {
		Set<SpacedVector3> points1 = Sets.newHashSet(walkmeshPolygon1.getPoints());
		Set<SpacedVector3> points2 = Sets.newHashSet(walkmeshPolygon2.getPoints());
		Set<List<SpacedVector3>> allPointCombinations = Sets.cartesianProduct(points1, points2);
		return Iterables.filter(allPointCombinations,
				new Predicate<List<SpacedVector3>>() {
					@Override
					public boolean apply(List<SpacedVector3> points) {
						return pointsAreCloseEnough(points.get(0), points.get(1));
					}
				});
	}

	private static boolean pointsAreCloseEnough(SpacedVector3 points1, SpacedVector3 point2) {
		return SpacedVector3.distance(points1, point2) < CLOSE_ENOUGH_DISTANCE;
	}

	public static class UnorderedPair<T> {
		T first;
		T second;

		public T getFirst() {
			return first;
		}

		public T getSecond() {
			return second;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof UnorderedPair)) {
				return false;
			}

			@SuppressWarnings("unchecked")
			final UnorderedPair pair = (UnorderedPair) o;

			return (((first == null ? pair.first == null : first.equals(pair.first)) && (second == null ? pair.second == null : second.equals(pair.second))) || ((first == null ? pair.second == null : first.equals(
					pair.second)) && (second == null ? pair.first == null : second.equals(pair.first))));
		}

		public int hashCode() {
			int firstHashCode = (first == null ? 0 : first.hashCode());
			int secondHashCode = (second == null ? 0 : second.hashCode());
			if (firstHashCode != secondHashCode) {
				return (((firstHashCode & secondHashCode) << 16) ^ ((firstHashCode | secondHashCode)));
			} else {
				return firstHashCode;
			}
		}

		public String toString() {
			String firstString = getFirst().toString();
			String secondString = getSecond().toString();
			if (firstString.compareTo(secondString) > 0) {
				String tempString = firstString;
				firstString = secondString;
				secondString = tempString;
			}
			return "(" + firstString + ", " + secondString + ")";
		}

		public UnorderedPair(T first, T second) {
			this.first = first;
			this.second = second;
		}
	}
}
