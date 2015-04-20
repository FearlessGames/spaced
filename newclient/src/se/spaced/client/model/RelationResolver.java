package se.spaced.client.model;

import com.google.inject.Singleton;

/**
 * Resolves the relation between entities, i.e. friendly and hostile
 */
// TODO: How do we want this to work?
@Singleton
public class RelationResolver {
	public Relation resolveRelation(final ClientEntity first, final ClientEntity second) {
		if (first.getFaction().equals(second.getFaction())) {
			return Relation.FRIENDLY;
		}
		return Relation.HOSTILE;
	}

	public boolean areEnemies(final ClientEntity first, final ClientEntity second) {
		Relation relation = resolveRelation(first, second);
		return relation == Relation.HOSTILE;
	}
}
