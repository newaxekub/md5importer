package com.model.md5.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.model.md5.controller.MD5Controller;
import com.model.md5.importer.resource.AnimImporter;
import com.model.md5.importer.resource.MeshImporter;
import com.model.md5.interfaces.IMD5Animation;
import com.model.md5.interfaces.IMD5Controller;
import com.model.md5.interfaces.IMD5Node;

/**
 * <code>MD5Importer</code> is a singleton utility class that provides a
 * mechanism to load models and animations of MD5 format.
 * <p>
 * <code>MD5Importer</code> allows separate <code>Mesh</code> and
 * <code>IMD5Animation</code> loading process. However, it also provides
 * convenient methods for loading both MD5 resources at once.
 * <p>
 * <code>MD5Importer</code> should be cleaned up after the loading process
 * is completed.
 * <P>
 * For details on MD5 format, please go to official MD5 wiki at
 * {@link}http://www.modwiki.net/wiki/MD5_(file_format).
 *
 * @author Yi Wang (Neakor)
 * @version Modified date: 11-18-2008 00:24 EST
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
	 * The <code>MinificationFilter</code> enumeration.
	 */
	private Texture.MinificationFilter miniFilter = Texture.MinificationFilter.Trilinear;
	/**
	 * The <code>MagnificationFilter</code> enumeration.
	 */
	private Texture.MagnificationFilter magFilter = Texture.MagnificationFilter.Bilinear;
	/**
	 * The <code>Integer</code> anisotropic level value.
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
	 * The <code>IMD5Node</code> instance.
	 */
	private IMD5Node node;
	/**
	 * The <code>IMD5Animation</code> instance.
	 */
	private IMD5Animation animation;

	/**
	 * Private default constructor of <code>MD5Importer</code>.
	 */
	private MD5Importer() {}

	/**
	 * Retrieve the importer singleton instance.
	 * @return The <code>MD5Importer</code> instance.
	 */
	public static MD5Importer getInstance() {
		if(MD5Importer.instance == null) {
			synchronized(MD5Importer.class) {
				if(MD5Importer.instance == null) MD5Importer.instance = new MD5Importer();
			}
		}
		return MD5Importer.instance;
	}

	/**
	 * Load the given md5mesh and md5anim files and assign the loaded
	 * <code>JointAnimation</code> to the <code>Mesh</code>.
	 * @param md5mesh The <code>URL</code> of the md5mesh file.
	 * @param modelName The <code>String</code> name of the loaded model.
	 * @param md5anim The <code>URL</code> points to the md5anim file.
	 * @param animName The <code>String</code> name of the loaded animation.
	 * @param repeatType The <code>Integer</code> repeat type.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	public void load(URL md5mesh, String modelName, URL md5anim, String animName, int repeatType) throws IOException {
		this.loadMesh(md5mesh, modelName);
		this.loadAnim(md5anim, animName);
		this.assignAnimation(repeatType);
	}

	/**
	 * Load the given md5mesh file.
	 * @param md5mesh The <code>URL</code> points to the md5mesh file.
	 * @param name The <code>String</code> name of the loaded model.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	public void loadMesh(URL md5mesh, String name) throws IOException {
		this.setupReader(md5mesh.openStream());
		MeshImporter meshImporter = new MeshImporter(this.reader);
		this.node = meshImporter.loadMesh(name);
	}

	/**
	 * Load the given md5anim file.
	 * @param md5anim The <code>URL</code> points to the md5anim file.
	 * @param name The <code>String</code> name of the loaded animation.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	public void loadAnim(URL md5anim, String name) throws IOException {
		this.setupReader(md5anim.openStream());
		AnimImporter animImporter = new AnimImporter(this.reader);
		this.animation = animImporter.loadAnim(name);
	}

	/**
	 * Setup the reader for file reading.
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
	 * Assign the loaded animation to the node.
	 * @param repeatType The <code>Integer</code> repeat type.
	 */
	private void assignAnimation(int repeatType) {
		IMD5Controller controller = new MD5Controller(this.node);
		controller.setRepeatType(repeatType);
		controller.addAnimation(this.animation);
		controller.setActive(true);
		this.node.addController(controller);
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
	 * Set the texture anisotropic level.
	 * @param value The <code>Integer</code> anisotropic level value.
	 */
	public void setAnisotropic(int aniso) {
		if(aniso >= 0) this.anisotropic = aniso;
		else MD5Importer.logger.info("Invalid Anisotropic filter level. Default 16 used.");
	}

	/**
	 * Set if oriented bounding should be used for the meshes.
	 * @param orientedBounding True if oriented bounding should be used. False otherwise.
	 */
	public void setOrientedBounding(boolean orientedBounding) {
		this.orientedBounding = orientedBounding;
	}

	/**
	 * Retrieve the minification (MM) texture filter.
	 * @return The <code>MinificationFilter</code> enumeration.
	 */
	public Texture.MinificationFilter getMiniFilter() {
		return this.miniFilter;
	}

	/**
	 * Retrieve the magnification (FM) texture filter.
	 * @return The <code>MagnificationFilter</code> enumeration.
	 */
	public Texture.MagnificationFilter getMagFilter() {
		return this.magFilter;
	}

	/**
	 * Retrieve the anisotropic level.
	 * @return The <code>Integer</code> anisotropic level.
	 */
	public int getAnisotropic() {
		return this.anisotropic;
	}

	/**
	 * Retrieve the MD5 node instance.
	 * @return The <code>IMD5Node</code> instance.
	 */
	public IMD5Node getModelNode() {
		return this.node;
	}

	/**
	 * Retrieve the MD5 animation instance.
	 * @return The <code>IMD5Animation</code> instance.
	 */
	public IMD5Animation getAnimation() {
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
	 * Cleanup the importer.
	 */
	public void cleanup() {
		this.reader = null;
		this.node = null;
		this.animation = null;
		MD5Importer.instance = null;
	}
}
