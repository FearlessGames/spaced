/*
 * Created on 19-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.unintelligible.antjnlpwar.datatype;

import org.apache.tools.ant.types.DataType;
import org.unintelligible.antjnlpwar.task.JnlpWar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ngc
 */
public class Application extends DataType implements JnlpWarDataType {

	private String mainclass;
	private File jar;
	private String arguments;

	/**
	 * @return Returns the arguments.
	 */
	public String getArguments() {
		return arguments;
	}

	/**
	 * @param arguments The arguments to set.
	 */
	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	/**
	 * @return Returns the jar.
	 */
	public File getJar() {
		return jar;
	}

	/**
	 * @param jar The jar to set.
	 */
	public void setJar(File jar) {
		this.jar = jar;
	}

	/**
	 * @return Returns the mainclass.
	 */
	public String getMainclass() {
		return mainclass;
	}

	/**
	 * @param mainclass The mainclass to set.
	 */
	public void setMainclass(String mainclass) {
		this.mainclass = mainclass;
	}

	/**
	 * @see org.unintelligible.antjnlpwar.datatype.JnlpWarDataType#getJnlpVersion()
	 */
	public double getJnlpVersion() {
		// TODO Auto-generated method stub
		return JnlpWar.JNLP_VERSION_10;
	}

	public List getMainclassargs() {
		if (arguments == null) {
			return new ArrayList();
		}
		return Arrays.asList(arguments.split(" "));
	}
}
