package se.spaced.spacedit.xmo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.shared.model.xmo.XmoRoot;
import se.spaced.spacedit.xmo.model.WrappedXmoRoot;

import java.io.File;

@Singleton
public class XmoCreatorImpl implements XmoCreator {
	private final XmoManager xmoManager;

	@Inject
	public XmoCreatorImpl(final XmoManager xmoManager) {
		this.xmoManager = xmoManager;
	}

	@Override
	public void create(File boundToFile) {
		WrappedXmoRoot xmoRoot = new WrappedXmoRoot(new XmoRoot(), boundToFile.getPath());
		xmoRoot.setName("New XmoGroup");

		xmoManager.initXmoRoot(xmoRoot);
	}
}
