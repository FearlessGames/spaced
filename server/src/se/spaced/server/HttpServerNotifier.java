package se.spaced.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class HttpServerNotifier implements ServerNotifier {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public HttpServerNotifier() {

	}

	@Override
	public void notifyServerCrash(Exception e) {
		try {
			String exceptionMessage = e.getMessage();

			if (exceptionMessage == null) {
				exceptionMessage = e.getClass().getName();	
			}
			String message = URLEncoder.encode(exceptionMessage, "UTF-8");
			URL benderAdress = new URL("http://localhost:8090/?type=crash&message=" + message);
			benderAdress.openConnection().getInputStream();
		} catch (MalformedURLException e1) {
			log.error("Bad URL", e1);
		} catch (IOException e1) {
			log.error("Failed to connect", e1);
		}
	}
}
