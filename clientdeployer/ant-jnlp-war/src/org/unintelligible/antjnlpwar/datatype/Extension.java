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
public class Extension extends ZipFileSet {

	/**
	 * Holds value of property name.
	 */
	private String name;

	/**
	 * Getter for property name.
	 *
	 * @return Value of property name.
	 */
	public String getName() {

		return this.name;
	}

	/**
	 * Setter for property name.
	 *
	 * @param name New value of property name.
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * Holds value of property version.
	 */
	private String version;

	/**
	 * Getter for property version.
	 *
	 * @return Value of property version.
	 */
	public String getVersion() {

		return this.version;
	}

	/**
	 * Setter for property version.
	 *
	 * @param version New value of property version.
	 */
	public void setVersion(String version) {

		this.version = version;
	}

	/**
	 * Holds value of property vendor.
	 */
	private String vendor;

	/**
	 * Getter for property vendor.
	 *
	 * @return Value of property vendor.
	 */
	public String getVendor() {

		return this.vendor;
	}

	/**
	 * Setter for property vendor.
	 *
	 * @param vendor New value of property vendor.
	 */
	public void setVendor(String vendor) {

		this.vendor = vendor;
	}

}
