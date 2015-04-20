package se.spaced.client.ardor.font.ttf.effects;

import se.spaced.client.ardor.font.ttf.Glyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ShadowEffect implements Effect {
	public static final int NUM_KERNELS = 16;
	public static final float[][] GAUSSIAN_BLUR_KERNELS = generateGaussianBlurKernels(NUM_KERNELS);

	private final Color color;
	private final float opacity;
	private final float xDistance;
	private final float yDistance;
	private final int blurKernelSize;
	private final int blurPasses;


	public ShadowEffect(Color color, float opacity, float xDistance, float yDistance, int blurKernelSize, int blurPasses) {
		this.color = color;
		this.opacity = opacity;
		this.xDistance = xDistance;
		this.yDistance = yDistance;
		this.blurKernelSize = blurKernelSize;
		this.blurPasses = blurPasses;
	}

	@Override
	public void draw(BufferedImage image, Graphics2D g, Glyph glyph) {
		g = (Graphics2D) g.create();
		g.translate(xDistance, yDistance);
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.round(opacity * 255)));
		g.fill(glyph.getShape());

		g.dispose();
		if (blurKernelSize > 1 && blurKernelSize < NUM_KERNELS && blurPasses > 0) {
			blur(image);
		}
	}

	@Override
	public int getPaddingTop() {
		return 0;
	}

	@Override
	public int getPaddingLeft() {
		return 0;
	}

	@Override
	public int getPaddingBottom() {
		return 0;
	}

	@Override
	public int getPaddingRight() {
		return 0;
	}

	private void blur(BufferedImage image) {
		float[] matrix = GAUSSIAN_BLUR_KERNELS[blurKernelSize - 1];
		Kernel gaussianBlur1 = new Kernel(matrix.length, 1, matrix);
		Kernel gaussianBlur2 = new Kernel(1, matrix.length, matrix);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		BufferedImageOp gaussianOp1 = new ConvolveOp(gaussianBlur1, ConvolveOp.EDGE_NO_OP, hints);
		BufferedImageOp gaussianOp2 = new ConvolveOp(gaussianBlur2, ConvolveOp.EDGE_NO_OP, hints);
		BufferedImage scratchImage = EffectUtil.getScratchImage();
		for (int i = 0; i < blurPasses; i++) {
			gaussianOp1.filter(image, scratchImage);
			gaussianOp2.filter(scratchImage, image);
		}
	}

	private static float[][] generateGaussianBlurKernels(int level) {
		float[][] pascalsTriangle = generatePascalsTriangle(level);
		float[][] gaussianTriangle = new float[pascalsTriangle.length][];
		for (int i = 0; i < gaussianTriangle.length; i++) {
			float total = 0.0f;
			gaussianTriangle[i] = new float[pascalsTriangle[i].length];
			for (int j = 0; j < pascalsTriangle[i].length; j++) {
				total += pascalsTriangle[i][j];
			}
			float coefficient = 1 / total;
			for (int j = 0; j < pascalsTriangle[i].length; j++) {
				gaussianTriangle[i][j] = coefficient * pascalsTriangle[i][j];
			}
		}
		return gaussianTriangle;
	}


	private static float[][] generatePascalsTriangle(int level) {
		if (level < 2) {
			level = 2;
		}
		float[][] triangle = new float[level][];
		triangle[0] = new float[1];
		triangle[1] = new float[2];
		triangle[0][0] = 1.0f;
		triangle[1][0] = 1.0f;
		triangle[1][1] = 1.0f;
		for (int i = 2; i < level; i++) {
			triangle[i] = new float[i + 1];
			triangle[i][0] = 1.0f;
			triangle[i][i] = 1.0f;
			for (int j = 1; j < triangle[i].length - 1; j++) {
				triangle[i][j] = triangle[i - 1][j - 1] + triangle[i - 1][j];
			}
		}
		return triangle;
	}
}
