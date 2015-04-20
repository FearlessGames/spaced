package se.spaced.client.ardor.font.ttf;

import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.ardor.font.ttf.effects.ColorEffect;
import se.spaced.client.ardor.font.ttf.effects.OutlineEffect;
import se.spaced.client.ardor.font.ttf.effects.ShadowEffect;

import java.awt.Color;

public class FontEffectFactory {

	@LuaMethod(name = "CreateShadowEffect")
	public ShadowEffect createShadowEffect(Double r, Double g, Double b, Double opacity, Double xDistance, Double yDistance,
														Double blurKernelSize, Double blurPasses) {
		return new ShadowEffect(createColor(r, g, b), opacity.floatValue(),
				xDistance.floatValue(), yDistance.floatValue(), blurKernelSize.intValue(), blurPasses.intValue());
	}

	@LuaMethod(name = "CreateColorEffect")
	public ColorEffect createColorEffect(Double r, Double g, Double b) {
		return new ColorEffect(createColor(r, g, b));
	}

	@LuaMethod(name = "CreateOutline")
	public OutlineEffect createOutlineEffect(Double r, Double g, Double b, Double width) {
		return new OutlineEffect(width.intValue(), createColor(r, g, b));
	}

	private Color createColor(Double r, Double g, Double b) {
		return new Color(r.floatValue(), g.floatValue(), b.floatValue());
	}
}
