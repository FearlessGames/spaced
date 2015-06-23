package se.spaced.client.settings.ui;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.lwjgl.opengl.DisplayMode;

import java.util.Collections;
import java.util.List;

public class DisplayModeSelector {

	private final Supplier<List<DisplayMode>> displayModesSupplier;
	private final Supplier<DisplayMode> savedDisplayModeSupplier;

	@Inject
	public DisplayModeSelector(
			Supplier<List<DisplayMode>> displayModesSupplier,
			Supplier<DisplayMode> savedDisplayModeSupplier) {
		this.displayModesSupplier = displayModesSupplier;
		this.savedDisplayModeSupplier = savedDisplayModeSupplier;
	}

	public DisplayMode getDisplayMode() {
		DisplayMode savedDisplayMode = savedDisplayModeSupplier.get();
		if (savedDisplayMode != null) {
			return savedDisplayMode;
		}
		return getDefaultSelectedDisplayMode();
	}

	public DisplayMode getDefaultSelectedDisplayMode() {
		List<DisplayMode> displayModes = displayModesSupplier.get();
		Collections.sort(displayModes, new DisplayModeComparator());
		return Iterators.getLast(displayModes.listIterator());
	}


}
