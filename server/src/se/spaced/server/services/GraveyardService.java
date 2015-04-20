package se.spaced.server.services;

import com.google.inject.Inject;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplate;
import se.spaced.server.persistence.dao.interfaces.GraveyardTemplateDao;
import se.spaced.shared.model.PositionalData;

import java.util.List;

public class GraveyardService {

	private final GraveyardTemplateDao graveyardTemplateDao;

	@Inject
	public GraveyardService(GraveyardTemplateDao graveyardTemplateDao) {
		this.graveyardTemplateDao = graveyardTemplateDao;
	}

	public GraveyardTemplate getClosestGraveyard(SpacedVector3 position) {
		GraveyardTemplate graveyardTemplate = null;
		double best = Double.MAX_VALUE;

		List<GraveyardTemplate> graveyards = graveyardTemplateDao.findAll();
		for (GraveyardTemplate graveyard : graveyards) {
			PositionalData point = graveyard.getSpawnPoint();
			double distSq = point.getPosition().distanceSquared(position);
			if (distSq < best) {
				best = distSq;
				graveyardTemplate = graveyard;
			}
		}
		if (graveyardTemplate == null) {
			throw new IllegalStateException("No graveyards found! Can't respawn");
		}
		return graveyardTemplate;
	}
}