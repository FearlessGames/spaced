package se.spaced.server.model.spawn;

import com.google.inject.Inject;
import org.hibernate.annotations.Type;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.model.spawn.schedule.SpawnScheduleTemplate;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.world.area.Geometry;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class MobSpawnTemplate extends ExternalPersistableBase {
	@ManyToOne //should not be cascade all since the mobTemplate is also removed if this mobSpawnTemplate is removed!
	private final MobTemplate mobTemplate;

	@ManyToOne(cascade = CascadeType.ALL)
	private final SpawnScheduleTemplate spawnScheduleTemplate;

	@Type(type = "xml")
	private Geometry geometryData;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "start", column = @Column(name = "minTimeAtPoint")),
			@AttributeOverride(name = "end", column = @Column(name = "maxTimeAtPoint"))
	})
	private IntervalInt timePausAtPoints = new IntervalInt(0, 0);

	@Transient
	private final MobOrderExecutor mobOrderExecutor;

	@Type(type = "xml")
	private Geometry roamArea;

	@Inject
	public MobSpawnTemplate(MobOrderExecutor mobOrderExecutor) {
		this(null, null, null, mobOrderExecutor);
	}

	public MobSpawnTemplate(
			UUID pk,
			MobTemplate mobTemplate,
			SpawnScheduleTemplate spawnScheduleTemplate,
			MobOrderExecutor mobOrderExecutor) {
		super(pk);
		this.mobTemplate = mobTemplate;
		this.spawnScheduleTemplate = spawnScheduleTemplate;
		this.mobOrderExecutor = mobOrderExecutor;
	}

	public MobTemplate getMobTemplate() {
		return mobTemplate;
	}

	public MobSpawn createMobSpawn(
			ActionScheduler scheduler,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			SpawnService spawnService,
			EntityService entityService, TimeProvider timeProvider, SpawnArea area, RandomProvider randomProvider) {
		return new MobSpawn(mobTemplate, spawnScheduleTemplate.createSchedule(scheduler),
				smrtBroadcaster, spawnService, entityService,
				scheduler,
				timeProvider,
				randomProvider, area,
				new BrainParameterProviderImpl(this, randomProvider, mobTemplate), mobOrderExecutor
		);
	}

	public SpawnScheduleTemplate getSpawnScheduleTemplate() {
		return spawnScheduleTemplate;
	}

	public Geometry getGeometry() {
		return geometryData;
	}

	public IntervalInt getTimePausAtPoints() {
		return timePausAtPoints;
	}

	public void setGeometryData(Geometry geometryData) {
		this.geometryData = geometryData;
	}

	public Geometry getRoamArea() {
		return roamArea;
	}
}
