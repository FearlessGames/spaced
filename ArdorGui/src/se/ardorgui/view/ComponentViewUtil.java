package se.ardorgui.view;

import com.ardor3d.image.Texture;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.TextureCombineMode;
import se.ardorgui.view.util.ResourceDisposer;

public class ComponentViewUtil {
	public static void setupComponent(Spatial spatial) {
		spatial.getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
		spatial.getSceneHints().setTextureCombineMode(TextureCombineMode.Replace);
		spatial.getSceneHints().setLightCombineMode(LightCombineMode.Off);
		spatial.setRenderState(createBlendState());
	}

	public static TextureState createTextureState(Texture texture) {
		if (texture == null) {
			return null;
		}
		TextureState ts = new TextureState();
		ts.setTexture(texture);
		ts.setEnabled(true);
		return ts;
	}

	private static BlendState createBlendState() {
		return createBlendState(true);
	}

	private static BlendState createBlendState(boolean testEnabled) {
		BlendState blendState = new BlendState();
		blendState.setEnabled(true);
		blendState.setBlendEnabled(true);
		blendState.setSourceFunctionAlpha(BlendState.SourceFunction.SourceAlpha);
		blendState.setDestinationFunctionAlpha(BlendState.DestinationFunction.OneMinusSourceAlpha);
		if (testEnabled) {
			blendState.setTestFunction(BlendState.TestFunction.GreaterThan);
		}
		blendState.setTestEnabled(testEnabled);
		return blendState;
	}

	public static void release(Spatial spatial) {
		final TextureState ts = (TextureState)spatial.getLocalRenderStates().get(RenderState.StateType.Texture);
		if (ts != null) {
			ResourceDisposer.releaseTextureState(ts, true);		// Removes all textures from cache!
		}
		//		clearBuffers();
		spatial.clearControllers();
		spatial.removeFromParent();
	}

	public static BlendState createBlendRGBMaxAlphaBlend() {
		final BlendState state = new BlendState();
		state.setBlendEnabled(true);
		state.setSourceFunctionRGB(BlendState.SourceFunction.SourceAlpha);
		state.setDestinationFunctionRGB(BlendState.DestinationFunction.OneMinusSourceAlpha);
		state.setBlendEquationRGB(BlendState.BlendEquation.Add);
		state.setSourceFunctionAlpha(BlendState.SourceFunction.SourceAlpha);
		state.setDestinationFunctionAlpha(BlendState.DestinationFunction.DestinationAlpha);
		state.setBlendEquationAlpha(BlendState.BlendEquation.Max);
		return state;
	}
}