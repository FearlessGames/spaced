package se.spaced.client.tools.spd;

import se.spaced.shared.model.xmo.XmoEntity;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class DummyPropPreviewer implements PropPreviwer {
	@Override
	public void shutdown() {
	}

	@Override
	public void lostFocus() {
	}

	@Override
	public void gainedFocus() {
	}

	@Override
	public JComponent getPreviewComponent() {
		return new JPanel();
	}

	@Override
	public void preview(XmoEntity xmoEntity) {
	}
}
