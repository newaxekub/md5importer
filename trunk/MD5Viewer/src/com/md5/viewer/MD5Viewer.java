package com.md5.viewer;

import com.md5.viewer.gui.AnimationSelector;

public class MD5Viewer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AnimationSelector selector = new AnimationSelector();
		selector.initialize();
		selector.display();
	}
}
