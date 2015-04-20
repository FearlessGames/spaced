package se.ardortech.curve;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Line;
import se.ardortech.curve.Curve.EvaluationMethod;

public class CurveLineCreator {

//	enum RenderMethods {
//		BRUTE_FORCE,
//		HORNERS,
//		FORWARD_DIFF,
//		SUBDIVIDE,
//		SUBDIVIDE_RECURSIVE
//	}

	public Line create(Curve curve, int numSamples, EvaluationMethod method) {
		int numSegments = curve.getNumSegments();
		final Vector3[] vertex = new Vector3 [numSamples * numSegments + 1];

		int v = 0;
		vertex[v++] = curve.get(0, 0, method);
		for (int s=0; s < numSegments; s++) {
	        for (int i=1; i < numSamples + 1; i++) {
	        	float t = (float)i / (float)(numSamples);
	        	vertex[v++] = curve.get(t, s, method);
	        }
		}
		Line line = createLine(vertex, ColorRGBA.WHITE);
		line.getMeshData().setIndexMode(IndexMode.LineStrip);
		line.setLineWidth(1);
		line.setSolidColor(ColorRGBA.WHITE);
		return line;
	}

	public Line createTangents(Curve curve, int numSamples, float scale, EvaluationMethod method) {
		int numSegments = curve.getNumSegments();
		final Vector3[] vertex = new Vector3 [(numSamples * numSegments + 1) * 2];

		int v = 0;
		Vector3 tangent = curve.getTangent(0, 0, method).normalizeLocal().multiplyLocal(scale);
    	vertex[0] = curve.get(0, 0, method);
    	vertex[1] = new Vector3(vertex[0]);
    	vertex[0].addLocal(tangent);
    	vertex[1].subtractLocal(tangent);
    	v += 2;
		for (int s=0; s < numSegments; s++) {
	        for (int i=1; i < numSamples + 1; i++) {
	        	float t = (float)i / (float)(numSamples);
	        	tangent = curve.getTangent(t, s, method).normalizeLocal().multiplyLocal(scale);
	        	vertex[v] = curve.get(t, s, method);
	        	vertex[v + 1] = new Vector3(vertex[v]);
	        	vertex[v].addLocal(tangent);
	        	vertex[v + 1].subtractLocal(tangent);
	        	v += 2;
	        }
        }
		Line line = createLine(vertex, ColorRGBA.RED);
		line.getMeshData().setIndexMode(IndexMode.Lines);
		line.setLineWidth(1);
		line.setSolidColor(ColorRGBA.RED);
		return line;
	}

	private Line createLine(final Vector3[] vertex, ReadOnlyColorRGBA color) {
		int size = vertex.length;
		final Vector3[] normal = new Vector3 [size];
        final ColorRGBA[] colors = new ColorRGBA [size];
        final Vector2[] texture = new Vector2 [size];
		for (int i=0; i < size; i++) {
        	normal[i] = new Vector3(0,0,1);
        	colors[i] = new ColorRGBA(color);
        	texture[i] = new Vector2(0,0);
        }

		Line line = new Line("line", vertex, normal, colors, texture);
        line.setTranslation(new Vector3(0,0,-10));
        line.setModelBound(new BoundingBox());
        line.updateWorldBound(true);

        return line;
	}
}
