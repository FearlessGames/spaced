package se.spaced.client.ardor.ui;

import se.krka.kahlua.integration.expose.LuaJavaClassExposer;

public interface SpacedGui {
	void teardown();

	void start(String[] files);

	void onUpdate(double timePerFrame);

	LuaJavaClassExposer setupBindings();

	void reload();

	void toggle();
}
