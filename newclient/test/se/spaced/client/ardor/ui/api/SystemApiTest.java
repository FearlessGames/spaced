package se.spaced.client.ardor.ui.api;

import com.ardor3d.extension.ui.UIHud;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.mockachino.order.*;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class SystemApiTest {

	private MockTimeProvider timeProvider;
	private SystemApi systemApi;
	private UIHud uiHud;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		uiHud = mock(UIHud.class);
		systemApi = new SystemApi(timeProvider, uiHud);
	}

	@Test
	public void timeProvider() throws Exception {
		timeProvider.setNow(4711);
		assertEquals(4711, systemApi.getTime());

		timeProvider.setNow(1337);

		assertEquals(1337, systemApi.getTime());
	}

	@Test
	public void mouseTracker() throws Exception {
		when(uiHud.getLastMouseX()).thenReturn(18);
		when(uiHud.getLastMouseY()).thenReturn(-39);
		ReturnValues values = mock(ReturnValues.class);
		systemApi.getMousePosition(values);

		OrderingContext order = newOrdering();
		order.verify().on(values).push(18);
		order.verify().on(values).push(-39);
	}
}
