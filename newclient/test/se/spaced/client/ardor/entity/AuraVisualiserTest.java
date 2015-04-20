package se.spaced.client.ardor.entity;

import org.junit.Before;
import org.junit.Test;
import se.spaced.client.view.entity.VisualEntity;

import static se.mockachino.Mockachino.*;

public class AuraVisualiserTest {

	private AuraVisualiser auraVisualiser;

	@Before
	public void setUp() throws Exception {
		auraVisualiser = new AuraVisualiser();
	}

	@Test
	public void testMapping() {
		AuraVisualiserEvent event = AuraVisualiserEvent.JETPACK_STARTED;

		AuraTrigger auraTrigger = mock(AuraTrigger.class);
		VisualEntity visualEntity = mock(VisualEntity.class);
		auraVisualiser.addMapping(event, auraTrigger);

		auraVisualiser.fireEvent(AuraVisualiserEvent.JETPACK_STARTED, visualEntity);

		verifyOnce().on(auraTrigger).trigger(visualEntity);
	}

	@Test
	public void testNoCrashWithoutMapping() throws Exception {
		auraVisualiser.fireEvent(AuraVisualiserEvent.JETPACK_STARTED, mock(VisualEntity.class));
	}
}
