package se.spaced.client.tools.areacreator.impl;

import javax.swing.JComponent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

public class TransferHandler extends javax.swing.TransferHandler {
	private final Interactions interactions;

	public TransferHandler(Interactions interactions) {
		this.interactions = interactions;
	}

	@Override
	public boolean importData(javax.swing.TransferHandler.TransferSupport info) {
		if (!canImport(info)) {
			return false;
		}
		interactions.paste();
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
		if (action == COPY) {
			interactions.copy();
		}

		if (action == MOVE) {
			interactions.cut();
		}
	}

	@Override
	public boolean canImport(javax.swing.TransferHandler.TransferSupport support) {
		return support.isDataFlavorSupported(DataFlavor.stringFlavor);
	}

	public interface Interactions {
		void copy();

		void paste();

		void cut();
	}
}
