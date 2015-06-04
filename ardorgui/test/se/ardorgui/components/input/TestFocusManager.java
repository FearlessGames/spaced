package se.ardorgui.components.input;

import org.junit.Test;
import se.ardorgui.components.area.ComponentArea;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentListener;
import se.ardorgui.input.GuiFocusManager;
import se.mockachino.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestFocusManager {
	@Test
	public void testNoFocus() {
		GuiFocusManager guifocusManager = new GuiFocusManager();
		guifocusManager.setFocus(null);
		assertNull("No focus", guifocusManager.getFocus());
		assertFalse("No focus", guifocusManager.hasFocus());
	}
	
	@Test
	public void testFocus() {
		GuiFocusManager guifocusManager = new GuiFocusManager();
		Component component = new Component(Mockachino.mock(ComponentListener.class), Mockachino.mock(ComponentArea.class));
		component.setCanHaveFocus(false);
		
		guifocusManager.setFocus(component);		
		assertNull("No focus", guifocusManager.getFocus());
		assertFalse("No focus", guifocusManager.hasFocus());
		
		component.setCanHaveFocus(true);
		
		guifocusManager.setFocus(component);		
		assertEquals(guifocusManager.getFocus(), component);
		assertTrue("Got focus", guifocusManager.hasFocus());
	}
}