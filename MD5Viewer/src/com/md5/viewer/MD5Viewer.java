package com.md5.viewer;

import com.md5.viewer.gui.AnimationSelector;

/**
 * <code>MD5Viewer</code> defines the concrete implementation of a viewer
 * unit that allows user to selected one or more animations and view them
 * in a chain in manual or automatic mode.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-23-2008 23:08 EST
 * @version Modified date: 11-23-2008 23:08 EST
 */
public class MD5Viewer {

	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		AnimationSelector selector = new AnimationSelector();
		selector.initialize();
		selector.display();
	}
}
