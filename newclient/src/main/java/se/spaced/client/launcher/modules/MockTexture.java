package se.spaced.client.launcher.modules;

import com.ardor3d.image.Image;
import com.ardor3d.image.Texture;

public final class MockTexture extends Texture {

	@Override
	public Image getImage() {
		return new Image();
	}

	@Override
	public void setWrap(WrapAxis wrapAxis, WrapMode wrapMode) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setWrap(WrapMode wrapMode) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public WrapMode getWrap(WrapAxis wrapAxis) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Type getType() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Texture createSimpleClone() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
