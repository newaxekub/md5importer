package com.md5.viewer.player;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.jme.app.SimpleGame;
import com.jme.scene.Controller;
import com.model.md5.controller.MD5Controller;
import com.model.md5.importer.MD5Importer;
import com.model.md5.interfaces.IMD5Animation;
import com.model.md5.interfaces.IMD5Controller;
import com.model.md5.interfaces.IMD5Node;

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
 * @version Creation date: 11-23-2008 23:12 EST
 * @version Modified date: 11-24-2008 00:06 EST
 */
public class AnimationPlayer extends SimpleGame {
	/**
	 * The <code>String</code> directory of the hierarchy.
	 */
	private String dir;
	/**
	 * The <code>List</code> of <code>String</code> animated parts.
	 */
	private final List<String> hierarchy;
	/**
	 * The <code>URL</code> link to the base animation.
	 */
	private final URL baseAnimURL;
	/**
	 * The <code>List</code> of <code>URL</code> link to the animations.
	 */
	private final List<URL> urls;
	/**
	 * The flag indicates if the playback mode is manual.
	 */
	private final boolean manual;
	/**
	 * The <code>MD5Importer</code> instance.
	 */
	private final MD5Importer importer;
	/**
	 * The loaded model mesh <code>IMD5Node</code>.
	 */
	private IMD5Node modelMesh;
	/**
	 * The base <code>IMD5Animation</code>.
	 */
	private IMD5Animation baseAnimation;
	/**
	 * The array of loaded <code>IMD5Animation</code>.
	 */
	private IMD5Animation[] animations;
	/**
	 * The <code>IMD5Controller</code> instance set on the model mesh.
	 */
	private IMD5Controller controller;

	/**
	 * Constructor of <code>AnimationPlayer</code>.
	 * @param dir The <code>String</code> directory of the hierarchy.
	 * @param hierarchy The <code>List</code> of <code>String</code> animated parts.
	 * @param baseAnimURL The <code>URL</code> link to the base animation.
	 * @param urls The <code>List</code> of <code>URL</code> link to the animations.
	 * @param manual True if the playback mode is manual. False automatic.
	 */
	public AnimationPlayer(String dir, List<String> hierarchy, URL baseAnimURL, List<URL> urls, boolean manual) {
		this.dir = dir;
		this.hierarchy = hierarchy;
		this.baseAnimURL = baseAnimURL;
		this.urls = urls;
		this.manual = manual;
		this.importer = MD5Importer.getInstance();
	}

	@Override
	protected void simpleInitGame() {
		try {
			this.loadModelMesh();
			this.loadAnimations();
			this.setupController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void simpleUpdate() {
		// TODO
	}
	
	/**
	 * Load the mesh of the model based on the hierarchy.
	 * @throws IOException If the loading process is interrupted.
	 */
	private void loadModelMesh() throws IOException {
		// Load the base model mesh.
		URL url = this.getClass().getClassLoader().getResource(this.dir + this.hierarchy.get(0) + ".md5mesh");
		this.importer.loadMesh(url, "Mesh0");
		this.modelMesh = this.importer.getModelNode();
		this.importer.cleanup();
		// Load the dependent children.
		for(int i = 1; i < this.hierarchy.size(); i++) {
			url = this.getClass().getClassLoader().getResource(this.dir + this.hierarchy.get(i) + ".md5mesh");
			this.importer.loadMesh(url, "Mesh"+i);
			this.modelMesh.attachDependent(this.importer.getModelNode());
			this.importer.cleanup();
		}
	}
	
	/**
	 * Load the animations from the URL links.
	 * @throws IOException If the loading process is interrupted.
	 */
	private void loadAnimations() throws IOException {
		// Load the base animation.
		this.importer.loadAnim(this.baseAnimURL, "BaseAnimation");
		this.baseAnimation = this.importer.getAnimation();
		this.importer.cleanup();
		// Load the animation chain.
		this.animations = new IMD5Animation[this.urls.size()];
		for(int i = 0; i < this.animations.length; i++) {
			this.importer.loadAnim(this.urls.get(i), "Animation"+i);
			this.animations[i] = this.importer.getAnimation();
			this.importer.cleanup();
		}
	}
	
	/**
	 * Set the controller on the loaded model mesh.
	 */
	private void setupController() {
		this.controller = new MD5Controller(this.modelMesh);
		this.controller.setActive(true);
		this.controller.setRepeatType(Controller.RT_WRAP);
		this.controller.setFading(this.baseAnimation, 0, false);
		this.modelMesh.addController(this.controller);
	}
}
