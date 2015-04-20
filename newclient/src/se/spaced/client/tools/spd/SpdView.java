package se.spaced.client.tools.spd;

import javax.swing.JComponent;

public interface SpdView {
	void display();

	void close();

	String getSelectedXmoFileName();

	double getScaleValue();

	void setPresenter(Presenter presenter);

	void addListItem(String s);

	void setPreviewComponent(JComponent previewComponent);

	void updateActiveProp();

	public interface Presenter {
		void placePropHere();

		void removeProp();

		void saveAllZones();

		void changedXmoFile();

		void gainedFocus();

		void lostFocus();

		void viewHiding();

		void viewShowing();
	}
}
