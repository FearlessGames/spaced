package se.spaced.client.view;

import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.scenegraph.Node;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.util.MockTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.client.ardor.entity.EntityIndicator;
import se.spaced.client.ardor.entity.VisualEntityFactory;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.Relation;
import se.spaced.client.model.player.TargetInfo;
import se.spaced.client.net.messagelisteners.EntityCacheImpl;
import se.spaced.client.view.entity.VisualEntities;
import se.spaced.client.view.entity.VisualEntity;
import se.spaced.client.view.entity.VisualEntityView;

import java.security.SecureRandom;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;


public class VisualEntityViewTest {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());
	private final UUID entityUuidA = uuidFactory.randomUUID();
	private final UUID entityUuidB = uuidFactory.randomUUID();

	private TargetInfo targetInfoA;
	private TargetInfo targetInfoB;

	private EntityIndicator hoverIndicator;

	private VisualEntityView entityView;
	private VisualEntity visualEntityA;
	private ClientEntity a;
	private ClientEntity b;

	@Before
	public void setUp() {
		visualEntityA = mock(VisualEntity.class);
		VisualEntity visualEntityB = mock(VisualEntity.class);
		a = new ClientEntity(entityUuidA, timeProvider);
		b = new ClientEntity(entityUuidB, timeProvider);

		VisualEntityFactory visualEntityFactory = mock(VisualEntityFactory.class);
		stubReturn(visualEntityA).on(visualEntityFactory).create(a, any(Relation.class));
		stubReturn(visualEntityB).on(visualEntityFactory).create(b, any(Relation.class));

		targetInfoA = mock(TargetInfo.class);
		targetInfoB = mock(TargetInfo.class);
		stubReturn(entityUuidA).on(targetInfoA).getUuid();
		stubReturn(entityUuidB).on(targetInfoB).getUuid();

		hoverIndicator = mock(EntityIndicator.class);

		entityView = new VisualEntityView(mock(Node.class),
				timeProvider,
				visualEntityFactory,
				mock(EntityIndicator.class), new EntityCacheImpl(null), hoverIndicator);
	}

	@Test
	public void addsVisualEntity() {
		entityView.addEntity(a, Relation.FRIENDLY);

		assertNotNull(entityView.getEntity(entityUuidA));
		assertNotSame(entityView.getEntity(entityUuidA), VisualEntities.EMPTY_ENTITY);
	}

	@Test
	public void setsEntityAlive() {
		entityView.addEntity(a, Relation.FRIENDLY);
		entityView.setEntityAlive(entityUuidA, true);

		verifyOnce().on(visualEntityA).setAlive(true);
	}

	@Test
	public void removesVisualEntity() {
		entityView.addEntity(a, Relation.FRIENDLY);
		VisualEntity removedVisualEntity = entityView.removeEntity(entityUuidA);

		verifyOnce().on(visualEntityA).removeFromParent();
		assertSame(visualEntityA, removedVisualEntity);
	}

	@Test
	public void setsNewVisualEntityTarget() {
		entityView.addEntity(a, Relation.FRIENDLY);
		entityView.setTargetedEntity(targetInfoA);

		verifyOnce().on(visualEntityA).setTargeted(true);
	}

	@Test
	public void resetsOldTargetWhenSettingNew() {
		entityView.addEntity(a, Relation.FRIENDLY);
		entityView.addEntity(b, Relation.FRIENDLY);

		entityView.setTargetedEntity(targetInfoA);
		entityView.setTargetedEntity(targetInfoB);

		verifyOnce().on(visualEntityA).setTargeted(false);
	}

	@Test
	public void clearsSetTarget() {
		entityView.addEntity(a, Relation.FRIENDLY);
		entityView.setTargetedEntity(targetInfoA);
		entityView.clearTargetedEntity();

		verifyOnce().on(visualEntityA).setTargeted(false);
	}

	@Test
	public void setsNewVisualEntityHover() {
		entityView.addEntity(a, Relation.FRIENDLY);
		entityView.setHoveredEntity(new TargetInfo(entityUuidA, null, true, true));

		verifyOnce().on(hoverIndicator).show(visualEntityA, any(Node.class), any(ReadOnlyColorRGBA.class));
	}

	@Test
	public void clearsSetHover() {
		entityView.addEntity(a, Relation.FRIENDLY);
		entityView.setHoveredEntity(new TargetInfo(entityUuidA, null, true, true));
		entityView.clearHoveredEntity();

		verifyOnce().on(hoverIndicator).hide();
	}
}
