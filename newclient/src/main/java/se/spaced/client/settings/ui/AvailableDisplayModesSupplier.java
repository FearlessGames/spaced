package se.spaced.client.settings.ui;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.util.List;

public class AvailableDisplayModesSupplier implements Supplier<List<DisplayMode>> {
	@Override
	public List<DisplayMode> get() {
		try {
			return Lists.newArrayList(Display.getAvailableDisplayModes());
		} catch (LWJGLException e) {
			throw new RuntimeException("Failed to get available display modes", e);
		}
	}
}
