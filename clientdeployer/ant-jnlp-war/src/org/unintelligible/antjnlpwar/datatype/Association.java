/*
 * Created on 18-Apr-2005
 *
 */
package org.unintelligible.antjnlpwar.datatype;

import org.apache.tools.ant.types.DataType;
import org.unintelligible.antjnlpwar.task.JnlpWar;

/**
 * @author ngc
 */
public class Association extends DataType implements JnlpWarDataType {

	private String mimetype;
	private String extensions;

	/**
	 * @return Returns the extensions.
	 */
	public String getExtensions() {
		return extensions;
	}

	/**
	 * Extensions should be a space-separated list of extensions
	 *
	 * @param extensions The extensions to set.
	 */
	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	/**
	 * @return Returns the mimetype.
	 */
	public String getMimetype() {
		return mimetype;
	}

	/**
	 * @param mimetype The mimetype to set.
	 */
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	/**
	 * @see org.unintelligible.antjnlpwar.datatype.JnlpWarDataType#getJnlpVersion()
	 */
	public double getJnlpVersion() {
		return JnlpWar.JNLP_VERSION_15;
	}

}
