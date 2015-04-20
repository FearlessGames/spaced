/*
 * This file has been split up in two for readability - this
 * one provides getters and setters for required info. 
 * Created on 15-Apr-2005
 *
 <jnlp-war 
 	tofile="path/to/war" 
 	offline-allowed="true" 
 	codebase="http://dsffs/" 
 	main-class="" 
 	installer-main-class="" 
 	title="" 
 	vendor="" 
 	homepage="" 
 	allpermission="true|false"
 	pack200="true|false"
 	storepass=""
 	>
 <description kind="one-line|short|tooltip">dfsdfsdf</description>
 <icon kind="splash" file="path/to/icon"/>
 <shortcut online="true|false" desktop="true|false" menu="true|false"/>
 <j2se minversion="1.2|1.3|1.4|1.5" args="-client -server...">
 <j2seProperty name="" value=""/>
 </j2se>
 <lib basedir="">
 	<include="*.jar"/>
 </lib>
 <nativelib basedir="">
 	<include="*.jar"/>
 </nativelib>
</jnlp-war>
 
 */
package org.unintelligible.antjnlpwar.task;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.ZipFileSet;
import org.unintelligible.antjnlpwar.datatype.Application;
import org.unintelligible.antjnlpwar.datatype.Association;
import org.unintelligible.antjnlpwar.datatype.Description;
import org.unintelligible.antjnlpwar.datatype.Extension;
import org.unintelligible.antjnlpwar.datatype.Icon;
import org.unintelligible.antjnlpwar.datatype.J2se;
import org.unintelligible.antjnlpwar.datatype.NativeLib;
import org.unintelligible.antjnlpwar.datatype.Shortcut;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ngc
 *         <p/>
 *         The main task to generate the WAR file
 */
public abstract class BaseJnlpWar extends Task {

	public static final double JNLP_VERSION_10 = 1.0;

	public static final double JNLP_VERSION_15 = 1.5;


	//task properties
	private File tofile;

	private boolean offlineallowed = true;

	private boolean allpermissions = true;

	private boolean pack200 = false;

	private String codebase;

	private String installermainclass;

	private String title = "Jnlp Application";

	private String vendor = "Vendor Name";

	private String homepage;

	private String signStorepass;

	private String signAlias;

	private String keystorePath;

	//task sub-elements
	private List descriptions = new ArrayList();

	private List icons = new ArrayList();

	private Shortcut shortcut;

	private List libs = new ArrayList();

	protected List expandedLibs = new ArrayList();

	private List nativeLibs = new ArrayList();

	protected List expandedNativeLibs = new ArrayList();

	private List extensions = new ArrayList();

	protected List expandedExtensions = new ArrayList();

	private List j2ses = new ArrayList();

	private Association association;

	private Application application;

	/*
	 * Getters and setters for public properties
	 */

	/**
	 * @return Returns the allpermissions.
	 */
	public boolean isAllpermissions() {
		return allpermissions;
	}

	/**
	 * @param allpermissions The allpermissions to set.
	 */
	public void setAllpermissions(boolean allpermissions) {
		this.allpermissions = allpermissions;
	}

	/**
	 * @return Returns the codebase.
	 */
	public String getCodebase() {
		return codebase;
	}

	/**
	 * @param codebase The codebase to set.
	 */
	public void setCodebase(String codebase) {
		if (codebase.charAt(codebase.length() - 1) == '/') {
			this.codebase = codebase + "application";
		} else {
			this.codebase = codebase + "/application";
		}
	}

	/**
	 * @return Returns the installermainclass.
	 */
	public String getInstallermainclass() {
		return installermainclass;
	}

	/**
	 * @param installermainclass The installermainclass to set.
	 */
	public void setInstallermainclass(String installermainclass) {
		this.installermainclass = installermainclass;
	}

	/**
	 * @return Returns the offlineallowed.
	 */
	public boolean isOfflineallowed() {
		return offlineallowed;
	}

	/**
	 * @param offlineallowed The offlineallowed to set.
	 */
	public void setOfflineallowed(boolean offlineallowed) {
		this.offlineallowed = offlineallowed;
	}

	/**
	 * @return Returns the tofile.
	 */
	public File getTofile() {
		return tofile;
	}

	/**
	 * @param tofile The tofile to set.
	 */
	public void setTofile(File tofile) {
		this.tofile = tofile;
	}

	/**
	 * @return Returns the homepage.
	 */
	public String getHomepage() {
		return homepage;
	}

	/**
	 * @param homepage The homepage to set.
	 */
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return Returns the vendor.
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * @param vendor The vendor to set.
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	/**
	 * @return Returns the pack200.
	 */
	public boolean isPack200() {
		return pack200;
	}

	/**
	 * @param pack200 The pack200 to set.
	 */
	public void setPack200(boolean pack200) {
		this.pack200 = pack200;
	}

	/**
	 * @return Returns the signAlias.
	 */
	public String getSignAlias() {
		return signAlias;
	}

	/**
	 * @param signAlias The signAlias to set.
	 */
	public void setSignAlias(String signAlias) {
		this.signAlias = signAlias;
	}

	/**
	 * @return Returns the signStorepass.
	 */
	public String getSignStorepass() {
		return signStorepass;
	}

	/**
	 * @param signStorepass The signStorepass to set.
	 */
	public void setSignStorepass(String signStorepass) {
		this.signStorepass = signStorepass;
	}

	/*
	 * Setters for sub-elements
	 */

	/**
	 * Add a description for the JNLP application
	 *
	 * @param d
	 */
	public void addApplication(Application a) {
		if (application != null) {
			throw new BuildException(
					"The application element was already set- only one application element can occur per task");
		}
		application = a;
	}

	/**
	 * @return Returns the application.
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Add a description for the JNLP application
	 *
	 * @param d
	 */
	public void addDescription(Description d) {
		descriptions.add(d);
	}

	/**
	 * @return Returns the descriptions.
	 */
	public List getDescriptions() {
		return descriptions;
	}

	/**
	 * Add an icon for the JNLP application
	 *
	 * @param i
	 */
	public void addIcon(Icon i) {
		icons.add(i);
	}

	/**
	 * Specify a shortcut element. Only one per task.
	 *
	 * @param s
	 */
	public void addShortcut(Shortcut s) {
		if (shortcut != null) {
			throw new BuildException(
					"The shortcut element was already set- only one shortcut element can occur per task");
		}
		shortcut = s;
	}

	/**
	 * Add a set of jars to be included in the JNLP app distribution
	 *
	 * @param fs
	 */
	public void addLib(ZipFileSet fs) {
		libs.add(fs);
	}

	/**
	 * Add a J2SE definition
	 *
	 * @param j2se
	 */
	public void addJ2se(J2se j2se) {
		j2ses.add(j2se);
	}

	/**
	 * Specify an association element. Only one per task.
	 *
	 * @param s
	 */
	public void addAssociation(Association a) {
		if (association != null) {
			throw new BuildException(
					"The association element was already set- only one association element can occur per task");
		}
		association = a;
	}

	/**
	 * get the file associations defined for the JNLP app
	 *
	 * @return
	 */
	public Association getAssociation() {
		return association;
	}

	/**
	 * Get the list of J2SE elements associated with this JNLP deployment file
	 *
	 * @return
	 */
	public List getJ2ses() {
		return j2ses;
	}

	/**
	 * @return Returns the icons.
	 */
	public List getIcons() {
		return icons;
	}

	/**
	 * @return Returns the jnlpVersion.
	 */
	public double getJnlpVersion() {
		if (association != null || shortcut != null || pack200) {
			return JNLP_VERSION_15;
		}
		//check each j2se element
		for (Iterator it = j2ses.iterator(); it.hasNext(); ) {
			J2se j2se = (J2se) it.next();
			if (j2se.getJnlpVersion() == JNLP_VERSION_15) {
				return JNLP_VERSION_15;
			}
		}
		//no 1.5-specific elements; return 1.0
		return JNLP_VERSION_10;
	}

	/**
	 * @return Returns the libs.
	 */
	protected List getLibs() {
		return libs;
	}

	/**
	 * Gets a list of String representing the filename of the jars for the application
	 *
	 * @return
	 */
	public List getExpandedLibs() {
		return expandedLibs;

	}

	/**
	 * Gets a list of String representing the filename of the native libs (packed as jars) for the application
	 *
	 * @return
	 */
	public List getExpandedNativeLibs() {
		return expandedNativeLibs;

	}

	/**
	 * @return Returns the nativeLibs.
	 */
	public List getNativeLibs() {
		return nativeLibs;
	}

	/**
	 * Gets a list of String representing the filename of the extension libs (packed as jars) for the application
	 *
	 * @return
	 */
	public List getExpandedExtensions() {
		return expandedExtensions;

	}

	/**
	 * @return Returns the extension libs.
	 */
	public List getExtensions() {
		return extensions;
	}

	/**
	 * @return Returns the shortcut.
	 */
	public Shortcut getShortcut() {
		return shortcut;
	}

	/**
	 * Adds a set of native libraries packeaged as jars to be distributed with
	 * the application. Can be specified in a per-OS basis.
	 *
	 * @param nl
	 */
	public void addNativeLib(NativeLib nl) {
		nativeLibs.add(nl);
	}

	/**
	 * Adds a set of extension libraries packeaged as jars to be distributed with
	 * the application.
	 *
	 * @param nl
	 */
	public void addExtension(Extension ext) {
		extensions.add(ext);
	}

	/**
	 * Holds value of property signKeystore.
	 */
	private String signKeystore;

	/**
	 * Getter for property signKeystore.
	 *
	 * @return Value of property signKeystore.
	 */
	public String getSignKeystore() {

		return this.signKeystore;
	}

	/**
	 * Setter for property signKeystore.
	 *
	 * @param signKeystore New value of property signKeystore.
	 */
	public void setSignKeystore(String signKeystore) {

		this.signKeystore = signKeystore;
	}

	public String getKeystorePath() {
		return keystorePath;
	}

	public void setKeystorePath(String keystorePath) {
		this.keystorePath = keystorePath;
	}
}