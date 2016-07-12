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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Represents the j2se element of a JNLP file. Implies JNLP spec version 1.5 if JVM arguments are set, 1.0 otherwise
 *
 * @author ngc
 */
public class J2se extends DataType implements JnlpWarDataType {

	private String args;
	private String minVersion = "1.4";
	private List properties = new ArrayList();
	//might want to support versions more fully in the future?
	//private static final String[] versions={"1.1", "1.2", "1.3", "1.4", "1.5"};
	private static final String[] minVersions = {"1.1", "1.2", "1.3", "1.4", "1.5", "1.6"};
	// from http://java.sun.com/j2se/1.5.0/docs/guide/javaws/developersguide/syntax.html
	private static final String[] validFullArgs = {"-client", "-server", "-verbose", "-showversion", "-esa", "-enablesystemassertions", "-dsa", "-disablesystemassertions", "-Xmixed", "-Xint", "-Xnoclassgc", "-Xincgc", "-Xbatch", "-Xprof", "-Xdebug", "-Xrs", "-XX:+ForceTimeHighResolution", "-XX:-ForceTimeHighResolution"};
	private static final String[] validPartialArgs = {"-ea:", "-enableassertions:", "-da:", "-disableassertions:", "-verbose:", "-Xms", "-Xmx", "-Xss", "-XX:NewRatio", "-XX:NewSize", "-XX:MaxNewSize", "-XX:PermSize", "-XX:MaxPermSize", "-XX:MaxHeapFreeRatio", "-XX:MinHeapFreeRatio", "-XX:UseSerialGC", "-XX:ThreadStackSize", "-XX:MaxInlineSize", "-XX:ReservedCodeCacheSize"};


	static {
		//Arrays.sort(versions);
		Arrays.sort(minVersions);
		Arrays.sort(validFullArgs);
		Arrays.sort(validPartialArgs);
	}

	/**
	 * @return Returns the args.
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * @param args The args to set.
	 */
	public void setArgs(String args) {
		StringTokenizer st = new StringTokenizer(args, " ", false);
		base:
		while (st.hasMoreTokens()) {
			String arg = st.nextToken();
			if (Arrays.binarySearch(validFullArgs, arg) > -1) {
				continue base;
			}
			for (int i = 0; i < validPartialArgs.length; i++) {
				String validPartialArg = validPartialArgs[i];
				if (arg.startsWith(validPartialArg)) {
					continue base;
				}
			}
			throw new BuildException("The " + arg + " j2se argument is not permitted by the JNLP spec");
		}
		this.args = args;
	}

	/**
	 * @return Returns the minVersion.
	 */
	public String getMinVersion() {
		return minVersion;
	}

	/**
	 * Sets the minimum version of the JRE required to run this application. Must be one of <code>1.1+</code>, <code>1.2+</code>, <code>1.3+</code>, <code>1.4+</code>, <code>1.5</code>
	 *
	 * @param minVersion The minVersion to set.
	 */
	public void setMinVersion(String minVersion) {
		if (Arrays.binarySearch(minVersions, minVersion) < 0) {
			String msg = "The j2se minVersion must be one of ";
			msg += ArrayUtil.toString(minVersions);
			throw new BuildException(msg);
		}
		this.minVersion = minVersion;
	}

	/**
	 * Add a property to be set by webstart on application startup
	 *
	 * @param p
	 */
	public void addJ2seProperty(J2seProperty p) {
		properties.add(p);
	}

	/**
	 * Gets the version of the JNLP spec associated with this element
	 *
	 * @see org.unintelligible.antjnlpwar.task.JnlpWarProjectComponent#getJnlpVersion()
	 */
	public double getJnlpVersion() {
		if (args == null) {
			return BaseJnlpWar.JNLP_VERSION_10;
		}
		return BaseJnlpWar.JNLP_VERSION_15;
	}


	/**
	 * @return Returns the properties.
	 */
	public List getProperties() {
		return properties;
	}
}
