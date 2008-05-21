package md5viewer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import md5viewer.enumn.FileType;

import com.jme.app.SimpleGame;
import com.jme.scene.Controller;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;

/**
 * <code>MD5Viewwer</code> allows artists to use <code>JFileChooser</code> to choose
 * the selected MD5 files for viewing easily.
 * 
 * @author Yi Wang (Neakor)
 * @version Creation date: 05-21-2008 11:30
 * @version Modified date: 05-21-2008 12:11
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
	 * Main application.
	 */
	public static void main(String[] args) {
		MD5Viewer app = new MD5Viewer();
		app.selectMeshFile();
		app.selectAnimFile();
		app.start();
	}
	
	/**
	 * Constructor of <code>MD5Viewer</code>.
	 */
	public MD5Viewer() {
		this.logger = Logger.getLogger(MD5Viewer.class.toString());
		this.chooser = new JFileChooser();
	}
	
	/**
	 * Select the MD5Mesh file with file chooser.
	 */
	private void selectMeshFile() {
		this.chooser.setDialogTitle("Select MD5Mesh file");
		Preferences preferences = Preferences.userNodeForPackage(MD5Viewer.class);
		File meshDir = new File(preferences.get("StartDirectory", "."));
		this.chooser.setCurrentDirectory(meshDir);
		if (this.chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = this.chooser.getSelectedFile();
			if(this.validateFile(FileType.Mesh, file.getName())) {
				try {
					this.mesh = file.toURI().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} else {
				this.logger.info("Invalid MD5Mesh file.");
				System.exit(-1);
			}
		}
	}
	
	/**
	 * Select the MD5Anim file with file chooser.
	 */
	private void selectAnimFile() {
		this.chooser.setDialogTitle("Select MD5Anim file");
		Preferences preferences = Preferences.userNodeForPackage(MD5Viewer.class);
		File animDir = new File(preferences.get("StartDirectory", "."));
		this.chooser.setCurrentDirectory(animDir);
		if (this.chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = this.chooser.getSelectedFile();
			if(this.validateFile(FileType.Animation, file.getName())) {
				try {
					this.anim = file.toURI().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} else {
				this.logger.info("Invalid MD5Anim file.");
			}
		}
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
	 * Override the <code>Texture</code> key location.
	 */
	private void overrideTextureKey() {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator(this.mesh));
		} catch (URISyntaxException e) {e.printStackTrace();}
	}
}
