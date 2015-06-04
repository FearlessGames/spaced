package se.ardortech;

import com.ardor3d.renderer.ContextCapabilities;
import com.ardor3d.renderer.ContextManager;
import org.slf4j.Logger;
import se.fearless.common.util.SystemInfo;


public class SystemInfoLogger {
	private final Logger log;

	public SystemInfoLogger(Logger log) {
		this.log = log;
	}

	public void logGlCapabilities() {
		final ContextCapabilities caps = ContextManager.getCurrentContext().getCapabilities();
		log.info("Display Vendor: "+caps.getDisplayVendor());
		log.info("Display Renderer: "+caps.getDisplayRenderer());
		log.info("Display Version: "+caps.getDisplayVersion());
		log.info("Shading Language Version: "+caps.getShadingLanguageVersion());
	}

	public void logJvmData() {
		log.info("JVM Vendor: " + SystemInfo.JAVA_VENDOR);
		log.info("Java version: " + SystemInfo.JAVA_VERSION);
	}

	public void logOsData() {
		log.info("OS Name: " + SystemInfo.OS_NAME);
		log.info("OS Version: " + SystemInfo.OS_VERSION);
		log.info("OS Architecture: " + SystemInfo.OS_ARCH);
	}

	public void logHardwareData() {
		log.info("Number of available processors: " + Runtime.getRuntime().availableProcessors());
		log.info("Free Memory: " + Runtime.getRuntime().freeMemory());
		log.info("Total Memory: " + Runtime.getRuntime().totalMemory());
		log.info("Max Memory: " + Runtime.getRuntime().maxMemory());

	}
}
