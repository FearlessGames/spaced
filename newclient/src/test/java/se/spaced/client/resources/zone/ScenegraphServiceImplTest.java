package se.spaced.client.resources.zone;

import com.ardor3d.scenegraph.Node;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.fearlessgames.common.lifetime.LifetimeManagerImpl;
import se.mockachino.order.*;
import se.spaced.client.model.Prop;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.util.QueueRunner;
import se.spaced.shared.util.SteppedQueueRunner;

import java.util.List;

import static se.mockachino.Mockachino.*;

public class ScenegraphServiceImplTest {

	private ScenegraphService scenegraphService;
	private LifetimeManager lifetimeManager;
	private SteppedQueueRunner<Prop,Void> xmoCreator;

	@Before
	public void setUp() throws Exception {
		Node propsRoot = new Node("propsRoot");
		PhysicsWorld<?> physWorld = mock(PhysicsWorld.class);
		lifetimeManager = new LifetimeManagerImpl();

		QueueRunner.Runner<Prop, Void> runner = mock(PropXmoEntityCreator.class);
		xmoCreator = new SteppedQueueRunner<Prop, Void>(runner);

		scenegraphService = new ScenegraphServiceImpl(propsRoot, physWorld, lifetimeManager, xmoCreator);
		lifetimeManager.start();
	}


	@Test
	public void singleLoad() throws Exception {
		LoadListener loadListener = mock(LoadListener.class);
		scenegraphService.setLoadListener(loadListener);
		Prop prop = mock(Prop.class);
		Zone zone = mock(Zone.class);
		scenegraphService.addProp(prop, zone);
		verifyOnce().on(loadListener).loadUpdate(1);

		xmoCreator.step();

		OrderingContext order = newOrdering();
		order.verify().on(loadListener).loadUpdate(1);
		order.verify().on(loadListener).loadUpdate(0);
		order.verify().on(loadListener).loadCompleted();
		lifetimeManager.shutdown();
	}

	@Test
	public void loadFive() throws Exception {
		LoadListener loadListener = mock(LoadListener.class);
		scenegraphService.setLoadListener(loadListener);
		List<Prop> props = Lists.newArrayList(mock(Prop.class), mock(Prop.class), mock(Prop.class), mock(Prop.class), mock(Prop.class));
		Zone zone = mock(Zone.class);

		scenegraphService.addProp(props.get(0), zone);
		scenegraphService.addProp(props.get(1), zone);
		xmoCreator.step();
		scenegraphService.addProp(props.get(2), zone);
		scenegraphService.addProp(props.get(3), zone);
		xmoCreator.step();
		xmoCreator.step();
		scenegraphService.addProp(props.get(4), zone);
		xmoCreator.step();
		xmoCreator.step();


		OrderingContext order = newOrdering();
		order.verify().on(loadListener).loadUpdate(1);
		order.verify().on(loadListener).loadUpdate(2);
		order.verify().on(loadListener).loadUpdate(1);
		order.verify().on(loadListener).loadUpdate(2);
		order.verify().on(loadListener).loadUpdate(3);
		order.verify().on(loadListener).loadUpdate(2);
		order.verify().on(loadListener).loadUpdate(1);
		order.verify().on(loadListener).loadUpdate(2);
		order.verify().on(loadListener).loadUpdate(1);
		order.verify().on(loadListener).loadUpdate(0);

		order.verify().on(loadListener).loadCompleted();
		lifetimeManager.shutdown();
	}


}
