package se.spaced.client.tools.areacreator;

import se.spaced.shared.world.AreaPoint;

import java.util.Collection;

public interface AreaCreatorView {
	void display();

	AreaTypeSelector addAreaType(String name, SelectionCallback selectionCallback);

	void setListItems(Collection<AreaPoint> items);

	void clearListItems();

	AreaPoint getCurrentSelectedAreaPoint();

	void setPresenter(Presenter presenter);

	void close();

	boolean useLocalSpace();

	void setActiveXmo(String xmoFile);

	public interface Presenter {

		void addAreaPointHere();

		void insertAreaPointHere();

		void copyValuesToClipBoard();

		void copyGeometryToClipBoard();

		void toggleShowInWorld();

		void deletePoint();

		void clearPoints();

		void selectedPoint();

		void pasteGeometry();

		void pasteAreaPointFromClipBoard();

		void cutAreaPointToClipBoard();

		void copyAreaPointToClipBoard();
	}

	public interface SelectionCallback {
		void onSelection();
	}

	interface AreaTypeSelector {
		void select();
	}
}
