package se.spaced.client.view.entity;

import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.ReadOnlyTimer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.ardortech.input.ClientMouseButton;
import se.ardortech.pick.Picker;
import se.fearless.common.util.MockTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.shared.util.ListenerDispatcher;

import java.security.SecureRandom;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;


public class EntityInteractionViewTest {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());
	private final UUID entityUuidA = uuidFactory.randomUUID();
	private final UUID entityUuidB = uuidFactory.randomUUID();

	private Node pickSpatial;
	private Picker picker;
	private EntityViewListener listener;
	private Mesh mesh;
	private PickResults pickResults;
	private PickData pickData;
	private ReadOnlyTimer timer;
	private EntityInteractionView entityView;


	@Before
	public void setUp() {
		pickSpatial = mock(Node.class);
		picker = mock(Picker.class);
		ListenerDispatcher<EntityViewListener> dispatcher = ListenerDispatcher.create(EntityViewListener.class);
		listener = mock(EntityViewListener.class);
		dispatcher.addListener(listener);
		mesh = mock(Mesh.class);
		pickResults = mock(PickResults.class);
		pickData = mock(PickData.class);
		timer = mock(ReadOnlyTimer.class);

		entityView = new DefaultEntityInteractionView(timeProvider, picker, pickSpatial, dispatcher);
	}

	@Test
	public void shouldClickEntity() {
		setUpPickA();

		entityView.onMouseDown(1, 1, ClientMouseButton.LEFT);
		entityView.onMouseUp(1, 1, ClientMouseButton.LEFT);

		verifyOnce().on(listener).entityLeftClicked(entityUuidA);
	}

	@Test
	public void shouldNotClickWhenMouseDownTooLong() {
		setUpPickA();

		entityView.onMouseDown(1, 1, ClientMouseButton.LEFT);
		timeProvider.advanceTime(DefaultEntityInteractionView.HOVER_FREQUENCE * 6);
		entityView.onMouseUp(1, 1, ClientMouseButton.LEFT);

		verifyNever().on(listener).entityLeftClicked(any(UUID.class));
	}

	@Test
	public void shouldNotClickWhenHittingNothing() {
		stubReturn(pickResults).on(picker).pickWithBoundingBox(1, 1, pickSpatial);
		stubReturn(0).on(pickResults).getNumber();

		entityView.onMouseDown(1, 1, ClientMouseButton.LEFT);
		entityView.onMouseUp(1, 1, ClientMouseButton.LEFT);

		verifyOnce().on(listener).nothingLeftClicked();
	}

	@Test
	public void shouldNotClickWhenMouseDownDiffersFromMouseUp() {
		setUpPickA();

		entityView.onMouseDown(1, 1, ClientMouseButton.LEFT);
		stubReturn(entityUuidB).on(mesh).getUserData();
		entityView.onMouseUp(1, 1, ClientMouseButton.LEFT);

		verifyNever().on(listener).entityLeftClicked(entityUuidA);
	}

	@Test
	public void shouldHoverEntity() {
		setUpPickA();

		entityView.onMouseMove(1, 1);
		timeProvider.advanceTime(DefaultEntityInteractionView.HOVER_FREQUENCE * 4);
		entityView.update(timer.getTimePerFrame());

		verifyOnce().on(listener).entityHovered(entityUuidA);
	}

	@Test
	public void resetsHoverWhenHoveringNothing() {
		setUpPickA();

		entityView.onMouseMove(1, 1);
		timeProvider.advanceTime(DefaultEntityInteractionView.HOVER_FREQUENCE * 4);
		entityView.update(timer.getTimePerFrame());

		stubReturn(0).on(pickResults).getNumber();
		entityView.onMouseMove(1, 1);
		timeProvider.advanceTime(DefaultEntityInteractionView.HOVER_FREQUENCE * 4);
		entityView.update(timer.getTimePerFrame());

		verifyOnce().on(listener).entityHovered(entityUuidA);
		verifyOnce().on(listener).hoverReset();
	}

	@Test
	public void disablesHoveringForCameraMove() {
		setUpPickA();

		entityView.onMouseDown(1, 1, ClientMouseButton.LEFT);
		getData(picker).resetCalls();
		timeProvider.advanceTime(DefaultEntityInteractionView.HOVER_FREQUENCE * 4);
		entityView.update(timer.getTimePerFrame());

		verifyNever().on(picker).pickWithBoundingBox(1, 1, pickSpatial);
	}

	@Test
	@Ignore("This might have to be added back?")
	public void resetsHoverWhenUiHoverStart() {
		setUpPickA();

		entityView.onMouseMove(1, 1);
		timeProvider.advanceTime(DefaultEntityInteractionView.HOVER_FREQUENCE * 4);
		entityView.update(timer.getTimePerFrame());
		verifyOnce().on(listener).entityHovered(entityUuidA);

		entityView.onMouseMove(1, 1);
		timeProvider.advanceTime(DefaultEntityInteractionView.HOVER_FREQUENCE * 4);
		entityView.update(timer.getTimePerFrame());

		verifyOnce().on(listener).hoverReset();
	}

	private void setUpPickA() {
		stubReturn(pickResults).on(picker).pickWithBoundingBox(1, 1, pickSpatial);
		stubReturn(1).on(pickResults).getNumber();
		stubReturn(pickData).on(pickResults).getPickData(0);
		stubReturn(mesh).on(pickData).getTarget();
		stubReturn(entityUuidA).on(mesh).getUserData();
	}
}
