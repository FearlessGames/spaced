package se.ardortech.render.module;

import java.awt.Dimension;
import java.awt.Toolkit;

public class RendererSettings {
	private int width = 1280;
	private int height = 800;
	private int colorDepth = 32;
	private int frequency = -1;
	private int alphaBits;
	private int depthBits = 24;
	private int stencilBits;
	private int samples = 4;
	private boolean stereo;
	private int fov = 55;
	private WindowMode windowMode = WindowMode.WINDOWED;

	public RendererSettings() {
	}

	public int getWidth() {
		if (windowMode == WindowMode.UNDECORATED) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			return (int) screenSize.getWidth();
		}
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		if (windowMode == WindowMode.UNDECORATED) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			return (int) screenSize.getHeight();
		}
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getColorDepth() {
		return colorDepth;
	}

	public void setColorDepth(int colorDepth) {
		this.colorDepth = colorDepth;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getAlphaBits() {
		return alphaBits;
	}

	public void setAlphaBits(int alphaBits) {
		this.alphaBits = alphaBits;
	}

	public int getDepthBits() {
		return depthBits;
	}

	public void setDepthBits(int depthBits) {
		this.depthBits = depthBits;
	}

	public int getStencilBits() {
		return stencilBits;
	}

	public void setStencilBits(int stencilBits) {
		this.stencilBits = stencilBits;
	}

	public int getSamples() {
		return samples;
	}

	public void setSamples(int samples) {
		this.samples = samples;
	}

	public boolean isStereo() {
		return stereo;
	}

	public void setStereo(boolean stereo) {
		this.stereo = stereo;
	}

	public int getFov() {
		return fov;
	}

	public void setFov(int fov) {
		this.fov = fov;
	}

	public WindowMode getWindowMode() {
		return windowMode;
	}

	public void setWindowMode(WindowMode windowMode) {
		this.windowMode = windowMode;
	}

	public boolean isFullScreen() {
		return windowMode == WindowMode.FULLSCREEN;
	}

	public boolean isDecorated() {
		return windowMode != WindowMode.UNDECORATED;
	}


}
