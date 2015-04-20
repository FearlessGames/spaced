package se.spaced.shared.model.xmo;

import com.ardor3d.renderer.state.GLSLShaderObjectsState;
import se.spaced.shared.xml.XmlIOException;

public interface XmoEntityFactory {
	XmoEntity create(String xmoRoot, String entityName) throws XmlIOException;

	GLSLShaderObjectsState getSkinningShader();
}
