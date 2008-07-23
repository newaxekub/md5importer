package com.md5viewer;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jme.app.SimpleGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.scene.Controller;
import com.jme.util.resource.MultiFormatResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.md5viewer.enumn.EFileType;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;

/**
 * <code>MD5Viewwer</code> allows artists to use <code>JFileChooser</code> to choose
 * the selected MD5 files for viewing easily.
 * 
 * @author Yi Wang (Neakor)
 * @version Creation date: 05-21-08 11:30
 * @version Modified date: 07-23-08 11:58
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
		app.mesh = app.selectFile(EFileType.Mesh);
		if(app.mesh == null) System.exit(-1);
		app.anim = app.selectFile(EFileType.Animation);
		app.start();
	}

	/**
	 * Constructor of <code>MD5Viewer</code>.
	 */
	public MD5Viewer() {
		this.initTheme();
		this.setConfigShowMode(ConfigShowMode.AlwaysShow);
		this.logger = Logger.getLogger(MD5Viewer.class.toString());
		this.chooser = new JFileChooser();
		this.speed = 1;
		this.scale = 1;
	}

	/**
	 * Initialize the Swing theme.
	 */
	private void initTheme() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		UIManager.put("Button.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("ToggleButton.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("RadioButton.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("CheckBox.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("ColorChooser.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("ComboBox.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("Label.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("List.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("MenuBar.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("MenuItem.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("RadioButtonMenuItem.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("CheckBoxMenuItem.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("Menu.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("PopupMenu.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("OptionPane.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("Panel.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("ProgressBar.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("ScrollPane.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("Viewport.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("TabbedPane.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("Table.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("TableHeader.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("TextField.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("PasswordField.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("TextArea.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("TextPane.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("EditorPane.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("TitledBorder.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("ToolBar.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("ToolTip.font", new Font("Tohama",Font.PLAIN,11));
		UIManager.put("Tree.font", new Font("Tohama",Font.PLAIN,11));
	}

	/**
	 * Select a <code>File</code> and return its <code>URL</code>.
	 * @param type The intended <code>FileType</code> of the <code>File</code>.
	 * @return The <code>URL</code> of the selected <code>File</code>.
	 */
	private URL selectFile(EFileType type) {
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
				if(type.equals(EFileType.Mesh)) System.exit(-1);
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
	private boolean validateFile(EFileType type, String filename) {
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
			MultiFormatResourceLocator locator = new MultiFormatResourceLocator(this.mesh, new String[]{".tga", ".bmp", ".jpg", ".png"});
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
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
