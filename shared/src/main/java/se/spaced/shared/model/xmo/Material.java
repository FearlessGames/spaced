package se.spaced.shared.model.xmo;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.state.CullState;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("material")
public class Material {
	private ColorRGBA diffuse;
	private ColorRGBA specular;
	private ColorRGBA ambient;
	private ColorRGBA emissive;
	private float shininess;
	private CullState.Face cull;
	private Blending blending;
	private String renderBucket;
	private boolean depthRead;
	private boolean depthWrite;

	public Material(
			ColorRGBA diffuse,
			ColorRGBA specular,
			ColorRGBA ambient,
			ColorRGBA emissive,
			float shininess,
			CullState.Face cull, Blending blending) {
		this.diffuse = diffuse;
		this.specular = specular;
		this.ambient = ambient;
		this.emissive = emissive;
		this.shininess = shininess;
		this.cull = cull;
		this.blending = blending;
	}

	public ReadOnlyColorRGBA getDiffuse() {
		return diffuse;
	}

	public ReadOnlyColorRGBA getSpecular() {
		return specular;
	}

	public ReadOnlyColorRGBA getAmbient() {
		return ambient;
	}

	public ReadOnlyColorRGBA getEmissive() {
		return emissive;
	}

	public float getShininess() {
		return shininess;
	}

	public CullState.Face getCull() {
		return cull;
	}

	public Blending getBlending() {
		return blending;
	}

	public String getRenderBucket() {
		return renderBucket;
	}

	public boolean isDepthRead() {
		return depthRead;
	}

	public boolean isDepthWrite() {
		return depthWrite;
	}
}
