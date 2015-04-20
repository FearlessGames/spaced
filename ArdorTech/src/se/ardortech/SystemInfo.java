package se.ardortech;

import com.ardor3d.renderer.ContextCapabilities;
import com.ardor3d.renderer.ContextManager;
import org.slf4j.Logger;
import se.fearlessgames.common.SystemUtils;


public class SystemInfo {
	private final Logger log;

	public SystemInfo(Logger log) {
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
		log.info("JVM Vendor: " + SystemUtils.JAVA_VENDOR);
		log.info("Java version: " + SystemUtils.JAVA_VERSION);
	}

	public void logOsData() {
		log.info("OS Name: " + SystemUtils.OS_NAME);
		log.info("OS Version: " + SystemUtils.OS_VERSION);
		log.info("OS Architecture: " + SystemUtils.OS_ARCH);
	}

	public void logHardwareData() {
		log.info("Number of available processors: " + Runtime.getRuntime().availableProcessors());
		log.info("Free Memory: " + Runtime.getRuntime().freeMemory());
		log.info("Total Memory: " + Runtime.getRuntime().totalMemory());
		log.info("Max Memory: " + Runtime.getRuntime().maxMemory());

	}
}
