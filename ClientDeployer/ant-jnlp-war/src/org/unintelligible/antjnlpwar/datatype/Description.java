/*
 * Created on 15-Apr-2005
 *
 */
package org.unintelligible.antjnlpwar.datatype;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.unintelligible.antjnlpwar.task.BaseJnlpWar;
import org.unintelligible.antjnlpwar.util.ArrayUtil;

import java.util.Arrays;

/**
 * Represents the description element of a JNLP file
 *
 * @author ngc
 */
public class Description extends DataType implements JnlpWarDataType {
	public static final String[] kinds = {"one-line", "short", "tooltip"};

	static {
		Arrays.sort(kinds);
	}

	private String description;
	private String kind;

	/**
	 * @return Returns the description.
	 */
	public String getText() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void addText(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @param kind The kind to set. Must be one of <code>one-line</code>, <code>short</code>, <code>tooltip</code>
	 */
	public void setKind(String kind) {
		if (Arrays.binarySearch(kinds, kind) == -1) {
			StringBuffer sb = new StringBuffer("The description kind must one of ");
			sb.append(ArrayUtil.toString(kinds));
			throw new BuildException(sb.toString());
		}
		this.kind = kind;
	}

	/**
	 * Find out which version of the JNLP spec is implied by this component
	 *
	 * @see org.unintelligible.antjnlpwar.task.JnlpWarProjectComponent#getJnlpVersion()
	 */
	public double getJnlpVersion() {
		return BaseJnlpWar.JNLP_VERSION_10;
	}
}
