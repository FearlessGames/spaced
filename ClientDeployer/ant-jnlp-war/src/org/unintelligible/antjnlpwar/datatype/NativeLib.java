/*
 * Created on 18-Apr-2005
 */
package org.unintelligible.antjnlpwar.datatype;

import org.apache.tools.ant.types.ZipFileSet;

/**
 * An extension of the ZipFileSet allowing users to specify which OS a particular JNLP NativeLib applies to.
 *
 * @author ngc
 */
public class NativeLib extends ZipFileSet {
	private String os;
	private String arch;


	/**
	 * @return Returns the os.
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @param os The os to set.
	 */
	public void setOs(String os) {
		this.os = os;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}
}
