package se.spaced.client.launcher.installer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopBrowser {
	private final Logger log = LoggerFactory.getLogger(getClass());
	public void browseTo(String uri) {
		Desktop desktop = Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(new URI(uri));
			} catch (IOException e) {
				log.warn(String.format("Failed to browse %s", uri), e);
			} catch (URISyntaxException e) {
				log.warn(String.format("Failed to browse, bad URI %s", uri), e);
			}
		}
	}
}
