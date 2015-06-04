package se.ardortech.example.curve;

import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.RenderState;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.curve.BezierCurve;
import se.ardortech.curve.Curve;
import se.ardortech.curve.Curve.EvaluationMethod;
import se.ardortech.curve.CurveLineCreator;
import se.ardortech.curve.CurveLoader;
import se.ardortech.example.BaseExampleScene;
import se.ardortech.render.DebugRender;
import se.ardortech.render.ScreenshotRender;

import java.util.List;

@Singleton
public class CurveScene extends BaseExampleScene {

	private static final CurveLoader curveLoader = new CurveLoader();
	private static final CurveLineCreator curveCreator = new CurveLineCreator();

	@Inject
	public CurveScene(DebugRender debugRender, ScreenshotRender screenshotRender) {
		super(debugRender, screenshotRender);
	}

	private static class CurveFile {
		private final String fileName;
		int numPoints;
		float scale;
		Vector3 translation;
		boolean tangents;

		public CurveFile(String fileName, int numPoints, float scale, Vector3 translation, boolean tangents) {
			this.fileName = fileName;
			this.numPoints = numPoints;
			this.scale = scale;
			this.translation = translation;
			this.tangents = tangents;
		}
	}

	private static final CurveFile bezier = new CurveFile("resources/curves/one_bezier.crv", 16, 0.01f, new Vector3(-3, -2, 0), true);
//	private static final CurveFile onlyBezier = new CurveFile("resources/curves/only_bezier.crv", 16, 0.02f, new Vector3(-3, -3, 0), true);
//	private static final CurveFile onlyBSpline = new CurveFile("resources/curves/only_bspline.crv", 16, 0.0006f, new Vector3(-2, -3, 0), false);
//	private static final CurveFile circle = new CurveFile("resources/curves/circle.crv", 1, 0.02f, new Vector3(-2, -2, 0), true);
//	private static final CurveFile hermite = new CurveFile("resources/curves/one_hermite.crv", 16, 0.003f, new Vector3(-3, -2, 0), true);
//	private static final CurveFile onlyHermite = new CurveFile("resources/curves/only_hermite.crv", 16, 0.001f, new Vector3(-3, -2, 0), false);
//	private static final CurveFile simpleMix = new CurveFile("resources/curves/simple_mix.crv", 16, 0.003f, new Vector3(-2, -1, 0), false);
//	private static final CurveFile symbol = new CurveFile("resources/curves/symbol.crv", 16, 0.05f, new Vector3(-7, -7, 0), true);
//	private static final CurveFile radius = new CurveFile("resources/curves/radius.crv", 1, 0.04f, new Vector3(-5, -5, 0), false);

	private static final CurveFile curveFile = bezier;
	private static final EvaluationMethod renderMethod = EvaluationMethod.Horners;

	@Override
	protected void setUp() {
		List<Curve> curves = curveLoader.load(curveFile.fileName);
		curves.add(new BezierCurve(new Vector3(  0, 400, 0),
								   new Vector3(200, 100, 0),
								   new Vector3(600, 700, 0),
								   new Vector3(600, 400, 0),
								   new Vector3(600, 100, 0),
								   new Vector3(300, 600, 0),
								   new Vector3(  0, 400, 0)));
		for (Curve curve : curves) {
		    root.attachChild(curveCreator.create(curve, curveFile.numPoints, renderMethod));
		    if (curveFile.tangents) {
		    	root.attachChild(curveCreator.createTangents(curve, curveFile.numPoints, 1 / curveFile.scale, renderMethod));
		    }
		}

		root.setTranslation(curveFile.translation);
        root.setScale(curveFile.scale);
        root.getLocalRenderStates().get(RenderState.StateType.Light).setEnabled(false);
	}
}