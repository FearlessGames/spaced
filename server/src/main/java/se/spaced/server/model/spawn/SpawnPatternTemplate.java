package se.spaced.server.model.spawn;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.MetaValue;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.spawn.area.CompositeSpawnArea;
import se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea;
import se.spaced.server.model.spawn.area.RandomSpaceSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.shared.util.random.RandomProvider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class SpawnPatternTemplate extends ExternalPersistableBase {
	@Any(metaColumn = @Column(name = "areaType"), fetch = FetchType.LAZY)
	@AnyMetaDef(
			idType = "se.spaced.server.persistence.dao.impl.hibernate.types.SpacedUUIDHibernateType",
			metaType = "string",
			metaValues = {
					@MetaValue(targetEntity = RandomSpaceSpawnArea.class, value = "RandomSpaceSpawnArea"),
					@MetaValue(targetEntity = PolygonSpaceSpawnArea.class, value = "PolygonSpaceSpawnArea"),
					@MetaValue(targetEntity = SinglePointSpawnArea.class, value = "SinglePointSpawnArea"),
					@MetaValue(targetEntity = CompositeSpawnArea.class, value = "CompositeSpawnArea")
			})
	@JoinColumn(name = "area_pk")
	@Cascade(CascadeType.ALL)
	private SpawnArea area;
	@XStreamAsAttribute
	private String name;

	@OneToMany(cascade = javax.persistence.CascadeType.ALL)
	private final Set<MobSpawnTemplate> mobspawns = new HashSet<MobSpawnTemplate>();


	protected SpawnPatternTemplate() {
		this(null, null, Collections.emptyList(), null);
	}

	public SpawnPatternTemplate(UUID uuid, SpawnArea area, Collection<MobSpawnTemplate> mobSpawnTemplates, String name) {
		super(uuid);
		this.area = area;
		this.name = name;
		mobspawns.addAll(mobSpawnTemplates);
	}

	public SpawnPattern createSpawnPattern(
			ActionScheduler actionScheduler, EntityService entityService,
			SpawnService spawnService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			TimeProvider timeProvider, RandomProvider randomProvider) {

		Collection<MobSpawn> spawns = Lists.newArrayList();
		for (MobSpawnTemplate spawnTemplate : mobspawns) {
			MobSpawn spawn = spawnTemplate.createMobSpawn(actionScheduler, smrtBroadcaster, spawnService, entityService,
					timeProvider, area, randomProvider);
			spawns.add(spawn);
		}
		return new SpawnPattern(area, spawns);

	}

	public SpawnArea getArea() {
		return area;
	}

	public Set<MobSpawnTemplate> getMobspawns() {
		return mobspawns;
	}

	@Override
	public String toString() {
		return getPk().toString();
	}

	public void setArea(SpawnArea area) {
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
