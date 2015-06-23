package se.spaced.client.settings.ui;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.lwjgl.opengl.DisplayMode;
import se.ardortech.render.module.RendererSettings;
import se.ardortech.render.module.WindowMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RenderPropertiesPresenter implements RenderPropertiesView.Presenter {
	private final RenderPropertiesView view;
	private final ImmutableListMultimap<Resolution, DisplayMode> resolutionToDisplayModeMap;

	private RendererSettings rendererSettings;


	public RenderPropertiesPresenter(RenderPropertiesView view, Supplier<List<DisplayMode>> availableDisplayModeSupplier) {
		this.view = view;
		view.setPresenter(this);

		List<DisplayMode> displayModes = availableDisplayModeSupplier.get();
		resolutionToDisplayModeMap = Multimaps.index(displayModes, new Function<DisplayMode, Resolution>() {
			@Override
			public Resolution apply(DisplayMode displayMode) {
				return new Resolution(displayMode);
			}
		});

		view.setModes(Arrays.asList(WindowMode.values()));
		List<Resolution> resolutions = new ArrayList<Resolution>(resolutionToDisplayModeMap.keySet());
		Collections.sort(resolutions);
		view.setDisplayReses(resolutions);
		view.setSamples(Arrays.asList(new SampleValue(0), new SampleValue(2), new SampleValue(4), new SampleValue(8)));

	}

	public void showDialog() {
		view.showDialog();
	}

	public RendererSettings flush() {
		Resolution displayRes = view.getDisplayRes();
		rendererSettings.setWidth(displayRes.getWidth());
		rendererSettings.setHeight(displayRes.getHeight());

		SampleValue sample = view.getSample();
		rendererSettings.setSamples(sample.getValue());

		ColorMode color = view.getColor();
		rendererSettings.setColorDepth(color.getValue());

		rendererSettings.setWindowMode(view.getMode());

		return rendererSettings;
	}

	public void setCurrentSettings(RendererSettings rendererSettings) {
		this.rendererSettings = rendererSettings;
		view.setMode(rendererSettings.getWindowMode());
		view.setDisplayRes(new Resolution(new DisplayMode(rendererSettings.getWidth(), rendererSettings.getHeight())));
		view.setColor(new ColorMode(rendererSettings.getColorDepth()));
		view.setSample(new SampleValue(rendererSettings.getSamples()));
	}

	@Override
	public void onModeChanged() {
		switch (view.getMode()) {
			case WINDOWED:
				view.setLockedResolutions(false);
				break;
			case UNDECORATED:
				view.setLockedResolutions(true);
				break;
			case FULLSCREEN:
				view.setLockedResolutions(false);
				break;
		}
	}

	@Override
	public void onColorChanged() {
	}

	@Override
	public void onDisplayResChanged() {
		Resolution resolution = view.getDisplayRes();
		ImmutableList<DisplayMode> displayModes = resolutionToDisplayModeMap.get(resolution);
		List<ColorMode> colorModes = new ArrayList<ColorMode>();
		for (DisplayMode displayMode : displayModes) {
			ColorMode colorMode = new ColorMode(displayMode.getBitsPerPixel());
			if (!colorModes.contains(colorMode)) {
				colorModes.add(colorMode);
			}
		}
		Collections.sort(colorModes);
		view.setColors(colorModes);

	}

	@Override
	public void onSamplesChanged() {
	}

	@Override
	public void onSave() {
		flush();
		view.close();
	}

	@Override
	public void onCancel() {
		view.close();
	}

	public static void main(String[] args) {
		RenderPropertiesPresenter presenter = new RenderPropertiesPresenter(new RenderPropertiesViewImpl(), new AvailableDisplayModesSupplier());
		presenter.setCurrentSettings(new RendererSettings());
		presenter.showDialog();
		System.out.println("oink");
	}
}
