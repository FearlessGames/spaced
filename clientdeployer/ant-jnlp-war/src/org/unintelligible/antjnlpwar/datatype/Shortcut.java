/*
 * Created on 18-Apr-2005
 */
package org.unintelligible.antjnlpwar.datatype;

import org.apache.tools.ant.types.DataType;
import org.unintelligible.antjnlpwar.task.BaseJnlpWar;

/**
 * Represents the shortcut element of a JNLP file. Implies version 1.5 of the JNLP spec.
 *
 * @author ngc
 */
public class Shortcut extends DataType implements JnlpWarDataType {
	private boolean menu;
	private boolean desktop;
	private boolean online;
	private String submenu;


	/**
	 * @return Returns the submenu.
	 */
	public String getSubmenu() {
		return submenu;
	}

	/**
	 * @param submenu The submenu to set.
	 */
	public void setSubmenu(String submenu) {
		this.submenu = submenu;
	}

	/**
	 * A shortcut to the application should be added to the desktop
	 *
	 * @return Returns the desktop.
	 */
	public boolean isDesktop() {
		return desktop;
	}

	/**
	 * A shortcut to the application should be added to the desktop
	 *
	 * @param desktop The desktop to set.
	 */
	public void setDesktop(boolean desktop) {
		this.desktop = desktop;
	}

	/**
	 * A shortcut to the application should be added to the start menu
	 *
	 * @return Returns the menu.
	 */
	public boolean isMenu() {
		return menu;
	}

	/**
	 * A shortcut to  the application should be added to the start menu
	 *
	 * @param menu The menu to set.
	 */
	public void setMenu(boolean menu) {
		this.menu = menu;
	}

	/**
	 * Find out whether the application requires online connectivity for launching
	 *
	 * @return Returns the online.
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * Set whether the application requires online connectivity for launching
	 *
	 * @param online The online to set.
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * Find out which version of the JNLP spec is implied by this component
	 *
	 * @see org.unintelligible.antjnlpwar.task.JnlpWarProjectComponent#getJnlpVersion()
	 */
	public double getJnlpVersion() {
		return BaseJnlpWar.JNLP_VERSION_15;
	}
}
