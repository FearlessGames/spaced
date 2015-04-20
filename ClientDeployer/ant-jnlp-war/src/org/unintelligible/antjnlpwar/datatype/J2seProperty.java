/*
 * Created on 18-Apr-2005
 *
 */
package org.unintelligible.antjnlpwar.datatype;

import org.apache.tools.ant.types.DataType;
import org.unintelligible.antjnlpwar.task.JnlpWar;

/**
 * represents a property to be passed to the J2se elements
 *
 * @author ngc
 */
public class J2seProperty extends DataType implements JnlpWarDataType {
	private String name;
	private String value;


	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @see org.unintelligible.antjnlpwar.datatype.JnlpWarDataType#getJnlpVersion()
	 */
	public double getJnlpVersion() {
		return JnlpWar.JNLP_VERSION_10;
	}
}
