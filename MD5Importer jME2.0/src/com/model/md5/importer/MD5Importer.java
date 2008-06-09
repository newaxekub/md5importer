package com.model.md5.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.model.md5.JointAnimation;
import com.model.md5.ModelNode;
import com.model.md5.controller.JointController;
import com.model.md5.importer.resource.AnimImporter;
import com.model.md5.importer.resource.MeshImporter;

/**
 * <code>MD5Importer</code> is a singleton utility class that provides a machanism
 * to load models and animations of MD5 format.
 * <p>
 * <code>MD5Importer</code> allows seperate <code>Mesh</code> and <code>JointAnimation</code>
 * loading process. However, it also provides convinient methods for loading both
 * MD5 resources at once.
 * <p>
 * <code>MD5Importer</code> should be cleaned up after the loading process is completed.
 * <P>
 * For details on MD5 format, please go to official MD5 wiki at
 * {@link}http://www.modwiki.net/wiki/MD5_(file_format).
 *
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-09-2008 19:52 EST
 */
public class MD5Importer {
	/**
	 * The base orientation value used for translating coordinate systems.
	 */
	public static final Quaternion base = new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f);
	/**
	 * The current supported versions of MD5 format.
	 */
	public static final int version = 10;
	/**
	 * The <code>Logger</code> instance.
	 */
	private static final Logger logger = Logger.getLogger(MD5Importer.class.getName());
	/**
	 * The singleton <code>MD5Importer</code> instance.
	 */
	private static MD5Importer instance;
	/**
	 * The minification (MM) <code>Texture</code> filter.
	 */
	private Texture.MinificationFilter miniFilter = Texture.MinificationFilter.Trilinear;
	/**
	 * The magnification (FM) <code>Texture</code> filter.
	 */
	private Texture.MagnificationFilter magFilter = Texture.MagnificationFilter.Bilinear;
	/**
	 * The anisotropic level value.
	 */
	private int anisotropic = 16;
	/**
	 * The flag indicates if oriented bounding should be used.
	 */
	private boolean orientedBounding;
	/**
	 * The <code>StreamTokenizer</code> instance.
	 */
	private StreamTokenizer reader;
	/**
	 * The <code>ModelNode</code> instance.
	 */
	private ModelNode modelNode;
	/**
	 * The <code>JointAnimation</code> instance.
	 */
	private JointAnimation animation;

	/**
	 * Private default constructor of <code>MD5Importer</code>.
	 */
	private MD5Importer() {}
	
	/**
	 * Retrieve the <code>MD5Importer</code> instance.
	 * @return The <code>MD5Importer</code> instance.
	 */
	public static MD5Importer getInstance() {
		if(MD5Importer.instance == null) {
			MD5Importer.instance = new MD5Importer();
		}
		return MD5Importer.instance;
	}

	/**
	 * Load the given md5mesh and md5anim files and assign the loaded
	 * <code>JointAnimation</code> to the <code>Mesh</code>.
	 * @param md5mesh The <code>URL</code> of the md5mesh file.
	 * @param modelName The name of the loaded <code>ModelNode</code>.
	 * @param md5anim The <code>URL</code> points to the md5anim file.
	 * @param animName The name of the loaded <code>JointAnimation</code>.
	 * @param repeatType The repeat type of the loaded <code>JointAnimation</code>.
	 * @throws IOException Thrown when errors occured during file reading.
	 */
	public void load(URL md5mesh, String modelName, URL md5anim, String animName, int repeatType) throws IOException {
		this.loadMesh(md5mesh, modelName);
		this.loadAnim(md5anim, animName);
		this.assignAnimation(repeatType);
	}
	
	/**
	 * Load the given md5mesh file.
	 * @param md5mesh The <code>URL</code> points to the md5mesh file.
	 * @param name The name of the loaded <code>ModelNode</code>.
	 * @throws IOException Thrown when errors occured during file reading.
	 */
	public void loadMesh(URL md5mesh, String name) throws IOException {
		this.setupReader(md5mesh.openStream());
		MeshImporter meshImporter = new MeshImporter(this.reader);
		this.modelNode = meshImporter.loadMesh(name);
	}
	
	/**
	 * Load the given md5anim file.
	 * @param md5anim The <code>URL</code> points to the md5anim file.
	 * @param name The name of the loaded <code>JointAnimation</code>.
	 * @throws IOException Thrown when errors occured during file reading.
	 */
	public void loadAnim(URL md5anim, String name) throws IOException {
		this.setupReader(md5anim.openStream());
		AnimImporter animImporter = new AnimImporter(this.reader);
		this.animation = animImporter.loadAnim(name);
	}
	
	/**
	 * Setup the <code>StreamTokenizer</code> for file reading.
	 * @param stream The <code>InputStream</code> of the file.
	 */
	private void setupReader(InputStream stream) {
		InputStreamReader streamReader = new InputStreamReader(stream);
		this.reader = new StreamTokenizer(streamReader);
		this.reader.quoteChar('"');
		this.reader.ordinaryChar('{');
		this.reader.ordinaryChar('}');
		this.reader.ordinaryChar('(');
		this.reader.ordinaryChar(')');
		this.reader.parseNumbers();
		this.reader.slashSlashComments(true);
		this.reader.eolIsSignificant(true);
	}
	
	/**
	 * Assign the loaded <code>JointAnimation</code> to the <code>ModelNode</code>.
	 * @param repeatType The repeat type of this <code>JointAnimation</code>.
	 */
	private void assignAnimation(int repeatType) {
		JointController controller = new JointController(this.modelNode.getJoints());
		controller.setRepeatType(repeatType);
		controller.addAnimation(this.animation);
		controller.setActive(true);
		this.modelNode.addController(controller);
	}
	
	/**
	 * Set the minification (MM) <code>Texture</code> filter.
	 * @param filter The minification (MM) <code>Texture</code> filter.
	 */
	public void setMiniFilter(Texture.MinificationFilter filter) {
		this.miniFilter = filter;
	}

	/**
	 * Set the magnification (FM) <code>Texture</code> filter.
	 * @param filter The magnification (FM) <code>Texture</code> filter.
	 */
	public void setMagFilter(Texture.MagnificationFilter filter) {
		this.magFilter = filter;
	}

	/**
	 * Set the <code>Texture</code> anisotropic level.
	 * @param value The anisotropic level value.
	 */
	public void setAnisotropic(int aniso) {
		if(aniso >= 0) this.anisotropic = aniso;
		else MD5Importer.logger.info("Invalid Anisotropic filter level. Default 16 used.");
	}
	
	/**
	 * Set if oriented bounding should be used for the <code>Mesh</code>.
	 * @param orientedBounding True if oriented bounding should be used. False otherwise.
	 */
	public void setOrientedBounding(boolean orientedBounding) {
		this.orientedBounding = orientedBounding;
	}

	/**
	 * Retrieve the minification (MM) <code>Texture</code> filter.
	 * @return The minification (MM) <code>Texture</code> filter.
	 */
	public Texture.MinificationFilter getMiniFilter() {
		return this.miniFilter;
	}

	/**
	 * Retrieve the magnification (FM) <code>Texture</code> filter.
	 * @return The magnification (FM) <code>Texture</code> filter.
	 */
	public Texture.MagnificationFilter getMagFilter() {
		return this.magFilter;
	}

	/**
	 * Retrieve the anisotropic level.
	 * @return The anisotropic level.
	 */
	public int getAnisotropic() {
		return this.anisotropic;
	}

	/**
	 * Retrieve the <code>ModelNode</code> instance.
	 * @return The <code>ModelNode</code> instance.
	 */
	public ModelNode getModelNode() {
		return this.modelNode;
	}

	/**
	 * Retrieve the <code>JointAnimation</code> instance.
	 * @return The <code>JointAnimation</code> instance.
	 */
	public JointAnimation getAnimation() {
		return this.animation;
	}
	
	/**
	 * Check if oriented bounding should be used.
	 * @return True if oriented bounding should be used. False otherwise.
	 */
	public boolean isOriented() {
		return this.orientedBounding;
	}

	/**
	 * Cleanup the <code>MD5Importer</code>.
	 */
	public void cleanup() {
		this.reader = null;
		this.modelNode = null;
		this.animation = null;
		MD5Importer.instance = null;
	}
}
