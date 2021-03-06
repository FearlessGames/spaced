package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.annotations.*;
import se.fearless.common.uuid.UUID;
import se.spaced.server.model.spawn.area.*;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;
import se.spaced.shared.model.PositionalData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

@Entity
public class GraveyardTemplate extends ExternalPersistableBase implements NamedPersistable {
	@Any(metaColumn = @Column(name = "areaType"), fetch = FetchType.LAZY)
	@AnyMetaDef(
			idType = "se.spaced.server.persistence.dao.impl.hibernate.types.SpacedUUIDHibernateType",
			metaType = "string",
			metaValues = {
					@MetaValue(targetEntity = RandomSpaceSpawnArea.class, value = "RandomSpaceSpawnArea"),
					@MetaValue(targetEntity = PolygonSpaceSpawnArea.class, value = "PolygonSpaceSpawnArea"),
					@MetaValue(targetEntity = SinglePointSpawnArea.class, value = "SinglePointSpawnArea")

			})
	@JoinColumn(name = "area_pk")
	@Cascade({CascadeType.ALL})
	private final SpawnArea area;

	private final String name;

	public GraveyardTemplate() {
		area = null;
		name = null;
	}

	public GraveyardTemplate(UUID pk, String name, SpawnArea area) {
		super(pk);
		this.name = name;
		this.area = area;
	}

	@Override
	public String getName() {
		return name;
	}

	public PositionalData getSpawnPoint() {
		SpawnPoint spawnPoint = area.getNextSpawnPoint();
		return new PositionalData(spawnPoint.getPosition(), spawnPoint.getRotation());
	}

}
