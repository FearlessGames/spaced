package se.spaced.shared.model.xmo;

import com.ardor3d.renderer.state.BlendState;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("blending")
public class Blending {
	private BlendState.SourceFunction sourceFunction;
	private BlendState.DestinationFunction destinationFunction;
	private boolean alphaTesting;
	private float alphaTestReference;

	public Blending(
			BlendState.SourceFunction sourceFunction,
			BlendState.DestinationFunction destinationFunction,
			boolean alphaTesting, float alphaTestReference) {
		this.sourceFunction = sourceFunction;
		this.destinationFunction = destinationFunction;
		this.alphaTesting = alphaTesting;
		this.alphaTestReference = alphaTestReference;
	}

	public BlendState.SourceFunction getSourceFunction() {
		return sourceFunction;
	}

	public BlendState.DestinationFunction getDestinationFunction() {
		return destinationFunction;
	}

	public boolean isAlphaTesting() {
		return alphaTesting;
	}

	public float getAlphaTestReference() {
		return alphaTestReference;
	}
}
