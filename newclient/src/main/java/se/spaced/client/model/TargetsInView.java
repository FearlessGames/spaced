package se.spaced.client.model;


import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Singleton
public class TargetsInView {
	private final Camera camera;
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final PlayerEntityProvider playerProvider;

	@Inject
	public TargetsInView(
			Camera camera, ActiveCache<Entity, ClientEntity> entityCache, PlayerEntityProvider playerProvider) {
		this.camera = camera;
		this.entityCache = entityCache;
		this.playerProvider = playerProvider;
	}

	public List<ClientEntity> getDistanceSortedTargets(double maxRange, boolean ignoreDeadTargets) {
		List<ClientEntity> list = new ArrayList<ClientEntity>();
		for (EntityDistance entityDistance : createSortedEntityDistanceList(maxRange, ignoreDeadTargets)) {
			list.add(entityDistance.entity);
		}
		return list;
	}

	private Iterable<EntityDistance> createSortedEntityDistanceList(double maxRange, boolean ignoreDeadTargets) {
		ClientEntity playerEntity = playerProvider.get();
		ClientEntity currentTarget = playerEntity.getTarget();

		List<EntityDistance> list = new ArrayList<EntityDistance>();
		for (ClientEntity clientEntity : entityCache.getValues()) {

			if (clientEntity.equals(playerEntity)) {
				continue;
			}

			if (currentTarget != null && currentTarget.equals(clientEntity)) {
				continue;
			}

			if (ignoreDeadTargets && !clientEntity.isAlive()) {
				continue;
			}


			double distance = clientEntity.getPosition().distance(playerEntity.getPosition());
			if (distance > maxRange) {
				continue;
			}

			if (isInFrustum(clientEntity)) {
				continue;
			}


			list.add(new EntityDistance(clientEntity, distance));
		}

		Collections.sort(list, new Comparator<EntityDistance>() {
			@Override
			public int compare(EntityDistance o1, EntityDistance o2) {
				return (int) (o1.distance - o2.distance);
			}
		});
		return list;
	}

	private boolean isInFrustum(ClientEntity clientEntity) {
		ReadOnlyVector3 fsp = camera.getFrustumCoordinates(clientEntity.getPosition());
		return fsp.getX() < camera.getFrustumLeft() ||
				fsp.getX() > camera.getFrustumRight() ||
				fsp.getY() < camera.getFrustumBottom() ||
				fsp.getY() > camera.getFrustumTop() ||
				fsp.getZ() < 0;
	}

	private static class EntityDistance {
		private final ClientEntity entity;
		private final double distance;

		private EntityDistance(ClientEntity entity, double distance) {
			this.entity = entity;
			this.distance = distance;
		}
	}
}
