package com.md5.viewer.player;

import java.net.URL;
import java.util.List;

import com.jme.app.SimpleGame;

/**
 * <code>AnimationPlayer</code> defines the concrete implementation of a
 * animation player unit that is responsible for the actual rendering and
 * updating of the animations. It allows two different playback modes.
 * <p>
 * <code>AnimationPlayer</code> takes in a list of links to the selected
 * animations and the animated parts hierarchy from the
 * <code>AnimationSelector</code>.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-23-2008 11:12:54 PM EST
 * @version Modified date: 11-23-2008 11:12:54 PM EST
 */
public class AnimationPlayer extends SimpleGame {
	/**
	 * The <code>List</code> of <code>URL</code> link to the animations.
	 */
	private final List<URL> urls;
	/**
	 * The <code>List</code> of <code>String</code> animated parts.
	 */
	private final List<String> hierarchy;
	/**
	 * The flag indicates if the playback mode is manual.
	 */
	private final boolean manual;

	/**
	 * Constructor of <code>AnimationPlayer</code>.
	 * @param urls The <code>List</code> of <code>URL</code> link to the animations.
	 * @param hierarchy The <code>List</code> of <code>String</code> animated parts.
	 * @param manual True if the playback mode is manual. False automatic.
	 */
	public AnimationPlayer(List<URL> urls, List<String> hierarchy, boolean manual) {
		this.urls = urls;
		this.hierarchy = hierarchy;
		this.manual = manual;
	}

	@Override
	protected void simpleInitGame() {
		// TODO Auto-generated method stub
		
	}
}
