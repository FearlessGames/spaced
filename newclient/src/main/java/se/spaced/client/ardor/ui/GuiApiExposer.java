package se.spaced.client.ardor.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;

import java.util.Collection;
import java.util.Set;

@Singleton
public class GuiApiExposer {
	private final Collection<Object> apiObjects;

	@Inject
	public GuiApiExposer(@Named("defaultGuiFunctions") Set<Object> apiObjects) {
		this.apiObjects = apiObjects;
	}

	public void expose(LuaJavaClassExposer classExposer) {
		for (final Object apiObject : apiObjects) {
			classExposer.exposeGlobalFunctions(apiObject);
		}
	}
}