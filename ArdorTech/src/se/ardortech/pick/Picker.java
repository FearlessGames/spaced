package se.ardortech.pick;

import com.ardor3d.framework.Canvas;
import com.ardor3d.intersection.BoundingPickResults;
import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.Pickable;
import com.ardor3d.intersection.PickingUtil;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.Spatial;
import com.google.inject.Inject;

public class Picker {
	private final Canvas canvas;
	private final Vector2 canvasPosition = new Vector2();
	private final Ray3 pickRay = new Ray3();

	@Inject
	public Picker(final Canvas canvas) {
		this.canvas = canvas;
	}

	public PickResults pickWithBoundingBox(final int canvasX, final int canvasY, final Spatial spatial) {
		final PickResults pickResults = new BoundingPickResults();
		return pickWithResults(canvasX, canvasY, spatial, pickResults);
	}

	private PickResults pickWithResults(int canvasX, int canvasY, Spatial spatial, PickResults pickResults) {
		canvasPosition.set(canvasX, canvasY);
		canvas.getCanvasRenderer().getCamera().getPickRay(canvasPosition, false, pickRay);
		pickResults.setCheckDistance(true);
		PickingUtil.findPick(spatial, pickRay, pickResults);
		return pickResults;
	}

	public PickResults pickWithPrimitives(final int canvasX, final int canvasY, final Spatial spatial) {
		final PickResults pickResults = new PrimitivePickResults();
		return pickWithResults(canvasX, canvasY, spatial, pickResults);
	}

	public static Spatial getSpatial( PickData pickData ) {
		Pickable pickable = pickData.getTarget();
		if( pickable != null ) {
			if( pickable instanceof Spatial ) {
				return (Spatial)pickable;
			}
		}
		return null;
	}
}
