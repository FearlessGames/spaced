package se.spaced.client.view;

import com.ardor3d.scenegraph.Node;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.pick.Picker;
import se.fearless.common.util.MockTimeProvider;
import se.spaced.client.model.Prop;
import se.spaced.shared.util.ListenerDispatcher;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.anyInt;

public class PropViewImplTest {

	private PropViewImpl propView;
	private Picker picker;
	private MockTimeProvider timeProvider;
	private ListenerDispatcher<PropSelectionListener> listeners;
	private Node propsNode;
	private PropsPicker propPicker;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		listeners = ListenerDispatcher.create(PropSelectionListener.class);
		propPicker = mock(PropsPicker.class);
		propView = new PropViewImpl(timeProvider, listeners, propPicker);
	}

	@Test
	public void mouseDownDoesntActivate() throws Exception {
		Prop prop = mock(Prop.class);
		when(propPicker.pick(anyInt(), anyInt())).thenReturn(new PropsPicker.SimpleResult(prop));

		propView.onMouseDown(10, 10);

		assertFalse(propView.hasActiveProp());
	}

	@Test
	public void onlyMouseUpDoesntActivate() throws Exception {
		Prop prop = mock(Prop.class);
		when(propPicker.pick(anyInt(), anyInt())).thenReturn(new PropsPicker.SimpleResult(prop));

		propView.onMouseUp(10, 10);

		assertFalse(propView.hasActiveProp());
	}


	@Test
	public void mouseUpActivates() throws Exception {
		Prop prop = mock(Prop.class);
		when(propPicker.pick(anyInt(), anyInt())).thenReturn(new PropsPicker.SimpleResult(prop));

		propView.onMouseDown(100, 100);
		propView.onMouseUp(100, 100);

		assertTrue(propView.hasActiveProp());
		assertSame(prop, propView.getActiveProp());
	}

	@Test
	public void dontActivateWhenMouseUpOnDifferentProp() throws Exception {
		Prop prop1 = mock(Prop.class);
		Prop prop2 = mock(Prop.class);

		when(propPicker.pick(100, 100)).thenReturn(new PropsPicker.SimpleResult(prop1));
		when(propPicker.pick(101, 101)).thenReturn(new PropsPicker.SimpleResult(prop2));

		propView.onMouseDown(100, 100);
		propView.onMouseUp(101, 101);

		assertFalse(propView.hasActiveProp());
	}

	@Test
	public void mouseUpTooSlowDoesntActivate() throws Exception {
		Prop prop = mock(Prop.class);
		when(propPicker.pick(anyInt(), anyInt())).thenReturn(new PropsPicker.SimpleResult(prop));

		propView.onMouseDown(100, 100);
		timeProvider.advanceTime(PropViewImpl.CLICK_TIMEOUT + 1);
		propView.onMouseUp(100, 100);

		assertFalse(propView.hasActiveProp());
	}

	@Test
	public void listenerFires() throws Exception {
		Prop prop = mock(Prop.class);
		when(propPicker.pick(anyInt(), anyInt())).thenReturn(new PropsPicker.SimpleResult(prop));

		Prop nullProp = propView.getActiveProp();
		PropSelectionListener listener = mock(PropSelectionListener.class);
		listeners.addListener(listener);

		propView.onMouseDown(100, 100);
		verifyNever().on(listener).activePropChanged(any(Prop.class), any(Prop.class));
		propView.onMouseUp(100, 100);
		verifyOnce().on(listener).activePropChanged(prop, nullProp);
	}

	@Test
	public void listenerFiresSecondTime() throws Exception {
		Prop prop1 = mock(Prop.class);
		Prop prop2 = mock(Prop.class);

		when(propPicker.pick(100, 100)).thenReturn(new PropsPicker.SimpleResult(prop1));
		when(propPicker.pick(200, 200)).thenReturn(new PropsPicker.SimpleResult(prop2));

		PropSelectionListener listener = mock(PropSelectionListener.class);
		listeners.addListener(listener);

		propView.onMouseDown(100, 100);
		timeProvider.advanceTime(1);
		propView.onMouseUp(100, 100);

		timeProvider.advanceTime(1);
		propView.onMouseDown(100, 100);
		timeProvider.advanceTime(1);
		propView.onMouseUp(100, 100);

		verifyOnce().on(listener).activePropChanged(prop1, any(Prop.class));

		timeProvider.advanceTime(1);
		propView.onMouseDown(200, 200);
		timeProvider.advanceTime(1);
		propView.onMouseUp(200, 200);

		verifyOnce().on(listener).activePropChanged(prop1, any(Prop.class));
		verifyOnce().on(listener).activePropChanged(prop2, prop1);
	}
}
