/*
 * Created on 18-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.unintelligible.antjnlpwar.datatype;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.unintelligible.antjnlpwar.task.BaseJnlpWar;
import org.unintelligible.antjnlpwar.util.ArrayUtil;

import java.io.File;
import java.util.Arrays;

/**
 * Represents the icon elemwent of a JNLP file
 *
 * @author ngc
 */
public class Icon extends DataType implements JnlpWarDataType {
	public static final String[] kinds = {"splash", "default", "selected", "disabled", "rollover"};

	static {
		Arrays.sort(kinds);
	}

	private File file;
	private String kind;

	/**
	 * @return Returns the file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file The file to set.
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		if (kind != null) {
			return kind;
		} else {
			return "default";
		}
	}

	/**
	 * @param kind The kind to set.
	 */
	public void setKind(String kind) {
		if (Arrays.binarySearch(kinds, kind) == -1) {
			StringBuffer sb = new StringBuffer("The icon kind must one of ");
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
