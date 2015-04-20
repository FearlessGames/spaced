package se.spaced.client.view;

import com.ardor3d.intersection.PickResults;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.pick.Picker;
import se.spaced.client.model.Prop;

import javax.inject.Inject;

public class PropsPicker {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Picker picker;
	private final Node propsNode;

	@Inject
	public PropsPicker(Picker picker, @Named("propsNode") Node propsNode) {
		this.picker = picker;
		this.propsNode = propsNode;
	}

	public PickResult pick(int x, int y) {
		PickResults pickResults = picker.pickWithPrimitives(x, y, propsNode);
		if (pickResults.getNumber() > 0) {
			log.debug("mouse down found pickresult > 0");
			Spatial spatial = Picker.getSpatial(pickResults.getPickData(0));
			Prop prop = getPropFromSpatial(spatial);
			return new SimpleResult(prop);
		}
		return NO_RESULT;
	}

	private Prop getPropFromSpatial(Spatial spatial) {
		if (spatial == null) {
			return null;
		}
		if (!(spatial.getUserData() instanceof Prop)) {
			return getPropFromSpatial(spatial.getParent());
		}
		Prop prop = (Prop) spatial.getUserData();
		if (prop != null) {
			return prop;
		}
		return getPropFromSpatial(spatial.getParent());
	}



	interface PickResult {
		boolean hasResult();
		Prop getProp();
	}

	private static final PickResult NO_RESULT = new PickResult() {
		@Override
		public boolean hasResult() {
			return false;
		}

		@Override
		public Prop getProp() {
			return null;
		}
	};

	static class SimpleResult implements PickResult {
		private final Prop prop;

		SimpleResult(Prop prop) {
			this.prop = prop;
		}

		@Override
		public boolean hasResult() {
			return prop != null;
		}

		@Override
		public Prop getProp() {
			return prop;
		}
	}
}
