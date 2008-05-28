package com.md5viewer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import com.jme.app.SimpleGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.scene.Controller;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.md5viewer.enumn.FileType;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;

/**
 * <code>MD5Viewwer</code> allows artists to use <code>JFileChooser</code> to choose
 * the selected MD5 files for viewing easily.
 * 
 * @author Yi Wang (Neakor)
 * @version Creation date: 05-21-08 11:30
 * @version Modified date: 05-28-08 12:11
 */
public class MD5Viewer extends SimpleGame{
	/**
	 * The <code>Logger</code> instance.
	 */
	private final Logger logger;
	/**
	 * The <code>JFileChooser</code> instance.
	 */
	private final JFileChooser chooser;
	/**
	 * The MD5Mesh file <code>URL</code> to load.
	 */
	private URL mesh;
	/**
	 * The MD5Anim file <code>URL</code> to load.
	 */
	private URL anim;
	/**
	 * The current action speed.
	 */
	private float speed;
	/**
	 * The current scale.
	 */
	private float scale;

	/**
	 * Main application.
	 */
	public static void main(String[] args) {
		MD5Viewer app = new MD5Viewer();
		app.mesh = app.selectFile(FileType.Mesh);
		if(app.mesh == null) System.exit(-1);
		app.anim = app.selectFile(FileType.Animation);
		app.start();
	}
	
	/**
	 * Constructor of <code>MD5Viewer</code>.
	 */
	public MD5Viewer() {
		this.logger = Logger.getLogger(MD5Viewer.class.toString());
		this.chooser = new JFileChooser();
		this.speed = 1;
		this.scale = 1;
		this.setDialogBehaviour(MD5Viewer.ALWAYS_SHOW_PROPS_DIALOG, (URL)null);
	}
	
	/**
	 * Select a <code>File</code> and return its <code>URL</code>.
	 * @param type The intended <code>FileType</code> of the <code>File</code>.
	 * @return The <code>URL</code> of the selected <code>File</code>.
	 */
	private URL selectFile(FileType type) {
		String promt = "";
		String error = "";
		switch(type) {
		case Mesh:
			promt = "Select MD5Mesh file";
			error = "Invalid MD5Mesh file.";
			break;
		case Animation:
			promt = "Select MD5Anim file";
			error = "Invalid MD5Anim file.";
			break;
		}
		this.chooser.setDialogTitle(promt);
		if (this.chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = this.chooser.getSelectedFile();
			if(this.validateFile(type, file.getName())) {
				try {
					return file.toURI().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} else {
				this.logger.info(error);
				if(type.equals(FileType.Mesh)) System.exit(-1);
			}
		}
		return null;
	}
	
	/**
	 * Validate the given file name with <code>FileType</code>.
	 * @param type The <code>FileType</code> standard.
	 * @param filename The file name to be validated.
	 * @return True if the given file name is valid. False otherwise.
	 */
	private boolean validateFile(FileType type, String filename) {
		switch(type) {
		case Mesh:
			return (filename.substring(filename.lastIndexOf(".")+1, filename.length()).equalsIgnoreCase("md5mesh"));
		case Animation:
			return (filename.substring(filename.lastIndexOf(".")+1, filename.length()).equalsIgnoreCase("md5anim"));
		}
		return false;
	}

	@Override
	protected void simpleInitGame() {
		this.setupKeyBindings();
		this.overrideTextureKey();
		// Load only mesh.
		if(this.mesh != null && this.anim == null) {
			try {
				MD5Importer.getInstance().loadMesh(this.mesh, "Mesh");
			} catch (IOException e) {
				e.printStackTrace();
			}
		// Load mesh and animation.
		} else if(this.mesh != null && this.anim != null) {
			try {
				MD5Importer.getInstance().load(this.mesh, "Mesh", this.anim, "Animation", Controller.RT_WRAP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ModelNode model = MD5Importer.getInstance().getModelNode();
		this.rootNode.attachChild(model);
		MD5Importer.getInstance().cleanup();
	}
	
	/**
	 * Setup the additional key bindings.
	 */
	private void setupKeyBindings() {
		KeyBindingManager.getKeyBindingManager().set("speedup", KeyInput.KEY_1);
		KeyBindingManager.getKeyBindingManager().set("slowdown", KeyInput.KEY_2);
		KeyBindingManager.getKeyBindingManager().set("scaleup", KeyInput.KEY_3);
		KeyBindingManager.getKeyBindingManager().set("scaledown", KeyInput.KEY_4);
	}
	
	/**
	 * Override the <code>Texture</code> key location.
	 */
	private void overrideTextureKey() {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator(this.mesh));
		} catch (URISyntaxException e) {e.printStackTrace();}
	}
	
	protected void simpleUpdate() {
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("speedup", false)) {
			this.speed += 1f;
			this.input.setActionSpeed(this.speed);
		} else if(KeyBindingManager.getKeyBindingManager().isValidCommand("slowdown", false)) {
			this.speed -= 1f;
			this.input.setActionSpeed(this.speed);
		} else if(KeyBindingManager.getKeyBindingManager().isValidCommand("scaleup", false)) {
			this.scale += 0.2f;
			this.rootNode.setLocalScale(this.scale);
		} else if(KeyBindingManager.getKeyBindingManager().isValidCommand("scaledown", false)) {
			this.scale -= 0.2f;
			this.rootNode.setLocalScale(this.scale);
		} 
	}
}
