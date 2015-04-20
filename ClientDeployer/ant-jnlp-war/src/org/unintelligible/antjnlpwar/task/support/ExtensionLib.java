/*
 * ExtensionLib.java
 *
 * Created on 29. november 2005, 10:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.unintelligible.antjnlpwar.task.support;

import org.unintelligible.antjnlpwar.datatype.Extension;
import org.unintelligible.antjnlpwar.task.BaseJnlpWar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glasius
 */
public class ExtensionLib extends Extension {
	private BaseJnlpWar task;
	private List jars = new ArrayList();

	/**
	 * Creates a new instance of ExtensionLib
	 */
	public ExtensionLib(BaseJnlpWar task, Extension ext) {
		this.task = task;
		setName(ext.getName());
		setVersion(ext.getVersion());
		setVendor(ext.getVendor());
	}

	public BaseJnlpWar getWarTask() {
		return task;
	}

	public void addJar(String jar) {
		jars.add(jar);
	}

	public List getJars() {
		return jars;
	}

	public String getJnlpName() {
		return getName() + "_extension.jnlp";
	}
}
