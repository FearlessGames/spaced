package se.ardorgui.view.views;

import com.ardor3d.image.Texture;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import se.ardorgui.components.button.Button;
import se.ardorgui.components.button.ButtonState;
import se.ardorgui.components.button.ButtonViewInterface;
import se.ardortech.meshgenerator.shapes.PanelMeshDataGenerator;

public class ButtonView extends PanelView implements ButtonViewInterface {
	private final Texture textureUp;
	private final Texture textureDown;
	private final Texture textureOver;

	public ButtonView(final Node node, final Mesh jmeObject, PanelMeshDataGenerator generator, final Texture textureUp, final Texture textureDown, final Texture textureOver) {
		super(node, jmeObject, generator);
		this.textureUp = textureUp;
		this.textureDown = textureDown;
		this.textureOver = textureOver;
	}

	private TextureState getTextureState() {
		return (TextureState)getMesh().getLocalRenderStates().get(RenderState.StateType.Texture);
	}

	@Override
	public void onChangeState(final Button button) {
		if (ButtonState.UP.equals(button.getState())) {
			getTextureState().setTexture(textureUp);
		} else if (ButtonState.DOWN.equals(button.getState())) {
			getTextureState().setTexture(textureDown);
		} else if (ButtonState.OVER.equals(button.getState())) {
			getTextureState().setTexture(textureOver);
		}
	}
}