package se.spaced.client.settings.ui;

import se.ardortech.render.module.WindowMode;

import java.util.List;

public interface RenderPropertiesView {
	void setColors(List<ColorMode> values);

	void setColor(ColorMode value);

	ColorMode getColor();

	void setDisplayReses(List<Resolution> values);

	void setDisplayRes(Resolution value);

	Resolution getDisplayRes();

	void setSamples(List<SampleValue> values);

	void setSample(SampleValue value);

	SampleValue getSample();

	void setModes(List<WindowMode> values);

	void setMode(WindowMode value);

	WindowMode getMode();

	void setPresenter(Presenter presenter);

	void showDialog();

	void close();

	void setLockedResolutions(boolean b);

	public interface Presenter {

		void onModeChanged();

		void onColorChanged();

		void onDisplayResChanged();

		void onSamplesChanged();

		void onSave();

		void onCancel();
	}
}
