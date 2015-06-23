package se.spaced.client.ardor.ui;

import org.junit.Before;
import org.junit.Test;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;

import java.util.HashSet;
import java.util.Set;

import static se.mockachino.Mockachino.*;

public class GuiApiExposerTest {
	private Object mockApiA;
	private Object mockApiB;
	private LuaJavaClassExposer exposer;

	@Before
	public void setUp() {
		mockApiA = mock(Object.class);
		mockApiB = mock(Object.class);
		exposer = mock(LuaJavaClassExposer.class);
	}

	@Test
	public void exposesDefaultApi() {
		Set<Object> apiObjects = new HashSet<Object>();
		apiObjects.add(mockApiA);
		apiObjects.add(mockApiB);

		GuiApiExposer apiExposer = new GuiApiExposer(apiObjects);
		apiExposer.expose(exposer);

		verifyOnce().on(exposer).exposeGlobalFunctions(mockApiA);
		verifyOnce().on(exposer).exposeGlobalFunctions(mockApiB);
	}
}
