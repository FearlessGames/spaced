package se.spaced.shared.util;

import org.slf4j.bridge.SLF4JBridgeHandler;

public class Slf4jJulBridge {

	public static void init() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}
}