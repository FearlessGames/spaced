package se.ardorgui.view.mesh;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.TextureCombineMode;
import com.ardor3d.ui.text.BMFont;
import com.ardor3d.ui.text.BMText;

public class TextQuad extends BMText {

	public TextQuad(
			final String name,
			final String text,
			final BMFont font,
			final double fontSize,
			BMText.Align textAlignment,
			boolean useShadow,
			ColorRGBA color) {
		// TODO: implement shadow!
		super(name, text, font);
		setAlign(textAlignment);
		getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
		setAutoFade(AutoFade.Off);
		setAutoScale(AutoScale.Off);
		setFontScale(fontSize);
		setAutoRotate(false);
		setRotation(new Matrix3().fromAngles(-MathUtils.HALF_PI, 0, 0));

		final ZBufferState zState = new ZBufferState();
		zState.setEnabled(false);
		zState.setWritable(false);
		setRenderState(zState);

		final CullState cState = new CullState();
		cState.setEnabled(false);
		setRenderState(cState);

		final BlendState blend = new BlendState();
		blend.setBlendEnabled(true);
		blend.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		blend.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		blend.setTestEnabled(true);
		blend.setReference(0f);
		blend.setTestFunction(BlendState.TestFunction.GreaterThan);
		setRenderState(blend);

		setDefaultColor(color);
		_sceneHints.setLightCombineMode(LightCombineMode.Off);
		_sceneHints.setTextureCombineMode(TextureCombineMode.Replace);
		updateModelBound();
	}

	@Override
	public float getWidth() {
		float width = super.getWidth();
		double unit = 1.0 / _font.getSize();
      double s = unit * _fontScale;
		return (float) (width * s);
	}

	@Override
	public float getHeight() {
		float height = super.getHeight();
		double unit = 1.0 / _font.getSize();
      double s = unit * _fontScale;
		return (float) (height * s);
	}
}