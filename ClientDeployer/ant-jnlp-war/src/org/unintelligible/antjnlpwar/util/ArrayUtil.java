/*
 * Created on 18-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.unintelligible.antjnlpwar.util;

/**
 * @author ngc
 */
public class ArrayUtil {
	/**
	 * Return a readable string representation of an array formatted as such:<br/>
	 * <var>
	 * item1, item2, item3
	 * </var>
	 *
	 * @param o
	 * @return a comma-separated textual represnetation of the array
	 */
	public static String toString(Object[] o) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < o.length; ) {
			sb.append(o[i].toString());
			if (i++ < o.length) {
				sb.append(',');
				sb.append(' ');
			}
		}
		return sb.toString();

	}

}
