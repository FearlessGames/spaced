package se.ardorgui.view.views;

import com.ardor3d.image.Texture;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import se.ardorgui.view.ComponentViewUtil;
import se.ardortech.meshgenerator.shapes.QuadMeshDataGenerator;

public class RttView extends PictureView {

	private final Mesh mesh;
	private TextureState textureRenderState;
	
	public RttView(final Node node, final Mesh mesh, final QuadMeshDataGenerator generator) {
		super(node, mesh, generator);
		this.mesh = mesh;
	
	}

	public void updateTexture(Texture texture) {
		if(textureRenderState == null) {
			textureRenderState = ComponentViewUtil.createTextureState(texture);
			mesh.setRenderState(textureRenderState);
		}
		
		textureRenderState.setTexture(texture);
	   textureRenderState.setNeedsRefresh(true);
	}
}
