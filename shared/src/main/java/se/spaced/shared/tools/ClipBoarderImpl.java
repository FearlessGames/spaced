package se.spaced.shared.tools;

import com.google.inject.Singleton;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

@Singleton
public class ClipBoarderImpl implements ClipBoarder {
	@Override
	public void putToClipBoard(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
	}

	@Override
	public String getClipBoard() {
		Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				return (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				return null;
			} catch (IOException ex) {
				return null;
			}
		}
		return null;
	}
}
