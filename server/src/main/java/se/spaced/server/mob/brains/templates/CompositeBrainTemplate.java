package se.spaced.server.mob.brains.templates;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.annotations.IndexColumn;
import se.fearless.common.uuid.UUID;
import se.spaced.server.mob.brains.CompositeMobBrain;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.area.SpawnArea;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class CompositeBrainTemplate extends BrainTemplate {
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@IndexColumn(name = "Indx")
	private List<BrainTemplate> brains;


	protected CompositeBrainTemplate() {
		this(null, null);
	}

	private CompositeBrainTemplate(UUID pk, List<BrainTemplate> brains) {
		super(pk, null);
		this.brains = brains;
	}

	@Override
	public MobBrain createBrain(
			Mob mob,
			SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		List<MobBrain> brainz = Lists.newArrayList();
		for (BrainTemplate template : brains) {
			brainz.add(template.createBrain(mob, spawnArea, brainParameterProvider));
		}
		return new CompositeMobBrain(brainz);
	}

	public static CompositeBrainTemplate create(
			UUID pk,
			List<BrainTemplate> templates) {
		return new CompositeBrainTemplate(pk, templates);
	}

	public static CompositeBrainTemplate create(UUID pk, BrainTemplate... brainz) {
		return create(pk, Lists.newArrayList(brainz));
	}

	public Iterable<BrainTemplate> brains() {
		return brains;
	}

	@Override
	public ImmutableSet<BrainParameter> getRequiredParameters() {
		ImmutableSet<BrainParameter> result = ImmutableSet.of();
		for (BrainTemplate brain : brains) {
			result = Sets.union(brain.getRequiredParameters(), result).immutableCopy();
		}
		return result;
	}
}
