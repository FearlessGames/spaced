package se.spaced.client.ardor.font.ttf;

import se.spaced.client.ardor.font.ttf.effects.ColorEffect;
import se.spaced.client.ardor.font.ttf.effects.Effect;
import se.spaced.client.ardor.font.ttf.effects.OutlineEffect;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrueTypeFontApp {
	public static void main(String[] args) throws IOException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		Font[] allFonts = ge.getAllFonts();
		Font arialFont = allFonts[18];


		TrueTypeFont trueTypeFont = new TrueTypeFont(arialFont, 12, false, false, 2, 2, 2, 2);

		trueTypeFont.addAsciiGlyphs();

		List<Glyph> glyphs = trueTypeFont.getGlyphs();
		List<Effect> effects = new ArrayList<Effect>(Arrays.asList(new OutlineEffect(2, Color.BLUE), new ColorEffect(Color.GREEN)));


		TrueTypeFontRenderer trueTypeFontRenderer = new TrueTypeFontRenderer(glyphs, effects);
		trueTypeFontRenderer.render();
		BufferedImage texture = trueTypeFontRenderer.getTexture();

		ImageIO.write(texture, "png", new File("c:\\temp\\font.png"));


	}
}
