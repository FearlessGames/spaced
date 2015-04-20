package se.spaced.shared.resources.zone;

import com.ardor3d.scenegraph.Node;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import se.ardortech.math.Shape3D;
import se.ardortech.math.SpacedVector3;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.Prop;

import java.util.ArrayList;
import java.util.List;

public class Zone {
	private static final double ENV_WEIGHT = 1.0;
	private static final double ENV_REACH = 100.0;


	private final String name;
	private final Shape3D shape;

	private final List<Prop> props;

	private String environmentDayCycleSettingsFile;

	private double envMaxWeight;
	private double envOuterFadeDistance;
	/**
	 * null means no inner fade at all
	 */
	private Double envInnerFadeDistance;


	/**
	 * All zone file rerefences in the xml
	 */
	private final List<String> subzoneFiles;

	@XStreamOmitField
	private final transient Node node = new Node();

	@XStreamOmitField
	private transient int depth;

	@XStreamOmitField
	private transient Zone parentZone;

	@XStreamOmitField
	private final transient List<Zone> subZones;

	@XStreamOmitField
	private transient String filename;


	public Zone(String name, Shape3D shape) {
		this(name, shape, null, null, null, 0, 0, null);
	}

	public Zone(
			String name, Shape3D shape,
			List<String> subzoneFiles, List<Prop> props,
			String environmentDayCycleSettingsFile,
			double envOuterFadeDistance, double envMaxWeight, Double envInnerFadeDistance) {
		this.envInnerFadeDistance = envInnerFadeDistance;

		if (envOuterFadeDistance == 0.0) {
			envOuterFadeDistance = ENV_REACH;
		}

		if (envMaxWeight == 0.0) {
			envMaxWeight = ENV_WEIGHT;
		}

		this.name = name;
		this.shape = shape;
		this.environmentDayCycleSettingsFile = environmentDayCycleSettingsFile;
		this.envOuterFadeDistance = envOuterFadeDistance;
		this.envMaxWeight = envMaxWeight;

		if (subzoneFiles == null) {
			subzoneFiles = Lists.newArrayList();
		}

		if (props == null) {
			props = Lists.newArrayList();
		}

		this.subzoneFiles = subzoneFiles;
		this.props = props;
		this.subZones = new ArrayList<Zone>();
	}


	public boolean isInside(SpacedVector3 point, double margin) {
		return shape.isInside(point, margin);
	}

	public List<Zone> getSubzones() {
		return subZones;
	}

	public Zone getSubzoneAt(SpacedVector3 point, double margin) {
		for (Zone subzone : subZones) {
			if (subzone.isInside(point, margin)) {
				return subzone;
			}
		}
		return null;
	}

	@LuaMethod(name = "GetName")
	public String getName() {
		return name;
	}

	public Zone getParentZone() {
		return parentZone;
	}

	public void setParentZone(Zone parent) {
		this.parentZone = parent;
		if (parent == null) {
			depth = 0;
		} else {
			depth = parent.getDepth() + 1;
		}
	}


	public List<String> getSubzoneFiles() {
		return subzoneFiles;
	}

	public List<Prop> getProps() {
		return props;
	}

	public String getEnvironmentDayCycleSettingsFile() {
		return environmentDayCycleSettingsFile;
	}

	public void setEnvironmentDayCycleSettingsFile(String environmentDayCycleSettingsFile) {
		this.environmentDayCycleSettingsFile = environmentDayCycleSettingsFile;
	}

	public void addSubZone(Zone subZone) {
		subZones.add(subZone);
	}


	public Node getNode() {
		return node;
	}


	@Override
	public String toString() {
		return "Zone{" +
				"name='" + name + '\'' +
				", subzoneFiles=" + subzoneFiles +
				", shape=" + shape +
				'}';
	}

	public Shape3D getShape() {
		return shape;
	}

	public void addProp(Prop prop) {
		props.add(prop);
	}

	public int getDepth() {
		return depth;
	}

	private Object readResolve() {
		return new Zone(name, shape, subzoneFiles,
				props, environmentDayCycleSettingsFile, envOuterFadeDistance, envMaxWeight, envInnerFadeDistance);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	private double getEnvironmentMaxWeight() {
		return envMaxWeight;
	}

	private double getOuterFadeDistance() {
		return envOuterFadeDistance;
	}

	public Double getInnerFadeDistance() {
		return envInnerFadeDistance;
	}

	public double distanceToEdge(SpacedVector3 pos) {
		double shapeDistance = shape.distanceToEdge(pos);
		if (getParentZone() == null) {
			return shapeDistance;
		}
		return Math.max(shapeDistance, getParentZone().distanceToEdge(pos));
	}

	/**
	 * @param pos the position in 3d space
	 * @return 1 if outer nodes have normal influence, 0 if outer nodes have no influence
	 */
	public double getInnerFade(SpacedVector3 pos) {
		Double max = getInnerFadeDistance();
		if (max == null) {
			return 1.0;
		}
		double distance = -distanceToEdge(pos);
		if (distance <= 0) {
			return 1.0;
		}
		return 1.0 - Math.min(1.0, distance / max);
	}

	public double getEnvironmentWeight(SpacedVector3 pos) {
		double distance = distanceToEdge(pos);
		double maxWeight = getEnvironmentMaxWeight();
		if (distance <= 0) {
			return maxWeight;
		}
		double reach = getOuterFadeDistance();
		if (distance > reach) {
			return 0;
		}
		return maxWeight * (1 - (distance / reach));
	}
}
