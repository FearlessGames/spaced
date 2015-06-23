package se.spaced.client.tools.spd;

import se.spaced.shared.model.xmo.XmoEntity;

import javax.swing.JComponent;

public interface PropPreviwer {
	void shutdown();

	void lostFocus();

	void gainedFocus();

	JComponent getPreviewComponent();

	void preview(XmoEntity xmoEntity);
}
