package se.spaced.spacedit;

import org.junit.Ignore;
import se.fearlessgames.common.log.Slf4jJulBridge;

@Ignore
public abstract class Slf4jTest {
	static {
		Slf4jJulBridge.init();
	}
}
