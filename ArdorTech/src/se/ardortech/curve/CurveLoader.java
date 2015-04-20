package se.ardortech.curve;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CurveLoader {

	private BezierCurve loadBezier(String values) {
		StringTokenizer st = new StringTokenizer(values, ",");
		int numConstraints = Integer.valueOf(st.nextToken().trim());
		float[] constraints = new float [numConstraints * 4];
		for (int i = 0; i < numConstraints; i++) {
			constraints[i * 4] = Float.valueOf(st.nextToken().trim());
			constraints[i * 4 + 1] = Float.valueOf(st.nextToken().trim());
			constraints[i * 4 + 2] = 0;
			constraints[i * 4 + 3] = 0;
		}
		return new BezierCurve(constraints, numConstraints);
	}

	private BSplineCurve loadBSpline(String values) {
		StringTokenizer st = new StringTokenizer(values, ",");
		int numConstraints = Integer.valueOf(st.nextToken().trim());
		float[] constraints = new float [numConstraints * 4];
		for (int i = 0; i < numConstraints; i++) {
			constraints[i * 4] = Float.valueOf(st.nextToken().trim());
			constraints[i * 4 + 1] = Float.valueOf(st.nextToken().trim());
			constraints[i * 4 + 2] = 0;
			constraints[i * 4 + 3] = 0;
		}
		return new BSplineCurve(constraints, numConstraints);
	}

	private HermiteCurve loadHermite(String values) {
		StringTokenizer st = new StringTokenizer(values, ",");
		int numConstraints = Integer.valueOf(st.nextToken().trim());
		float[] constraints = new float [numConstraints * 4];
		if (numConstraints > 4 && ((numConstraints - 4) % 3) == 0) {
			numConstraints += (numConstraints - 4) / 3;
		}

		int j=0;
		for (int i = 0; i < numConstraints; i++) {
			if ((i >= 4) && (i % 4) == 0) {
				constraints[i * 4] = constraints[i * 4 - 12];
				constraints[i * 4 + 1] = constraints[i * 4 - 11];
				constraints[i * 4 + 2] = 0.0f;
				constraints[i * 4 + 3] = 0.0f;
				continue;
			}

			constraints[j * 4] = Float.valueOf(st.nextToken().trim());
			constraints[j * 4 + 1] = Float.valueOf(st.nextToken().trim());
			constraints[j * 4 + 2] = 0;
			constraints[j * 4 + 3] = 0;
			j++;
		}
		return new HermiteCurve(constraints, numConstraints);
	}

	public List<Curve> load(String filename) {
		List<Curve> curves = new ArrayList<Curve>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String strLine = "";
			while ((strLine = br.readLine()) != null) {
				if (strLine.startsWith("BEZIER")) {
					curves.add(loadBezier(strLine.substring(strLine.indexOf(" "))));
				} else if (strLine.startsWith("HERMITE")) {
					curves.add(loadHermite(strLine.substring(strLine.indexOf(" "))));
				} else if (strLine.startsWith("BSPLINE")) {
					curves.add(loadBSpline(strLine.substring(strLine.indexOf(" "))));
				}
			}
		} catch (Exception e) {
			System.out.println("Exception while reading file: " + e);
		}
		return curves;
	}
}
