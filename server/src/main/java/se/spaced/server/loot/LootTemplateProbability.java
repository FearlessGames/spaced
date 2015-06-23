package se.spaced.server.loot;

import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class LootTemplateProbability extends ExternalPersistableBase {
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private final PersistableLootTemplate lootTemplate;

	private double probability;

	protected LootTemplateProbability() {
		this(null, null, 0.0);
	}

	public LootTemplateProbability(UUID uuid, PersistableLootTemplate lootTemplate, double probability) {
		super(uuid);
		this.lootTemplate = lootTemplate;
		this.probability = probability;
	}

	public LootTemplate getLootTemplate() {
		return lootTemplate;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
}
