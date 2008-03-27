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
 * MD5Importer provides a machanism to load models and animations of MD5 format.
 * The importer is a singleton object which should be cleaned after importing
 * process. For details on MD5 format, please go to official MD5 wiki at
 * http://www.modwiki.net/wiki/MD5_(file_format).
 *
 * @author Yi Wang (Neakor)
 */
public class MD5Importer {
	// The base orientation value.
	public static final Quaternion base = new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f);
	// The current support versions of MD5 format.
	public static final int version = 10;
	// The logger object.
	private static final Logger logger = Logger.getLogger(MD5Importer.class.getName());
	// The importer singleton instance.
	private static MD5Importer instance;
	// The image file extensions.
	private final String[] extensions = {".jpg", ".tga", ".png", ".dds", ".gif", ".bmp"};
	// The MM texture filter.
	private int MM_Filter = Texture.MM_LINEAR_LINEAR;
	// The FM texture filter.
	private int FM_Filter = Texture.FM_LINEAR;
	// The anisotropic value.
	private int anisotropic = 16;
	// The flag indicates if oriented bounding should be used.
	private boolean orientedBounding;
	// The stream tokenizer object.
	private StreamTokenizer reader;
	// The model node object.
	private ModelNode modelNode;
	// The skeleton animation object.
	private JointAnimation animation;

	/**
	 * Private default constructor.
	 */
	private MD5Importer() {}
	
	/**
	 * Retrieve the MD5Importer instance object.
	 * @return The MD5Importer instance object.
	 */
	public static MD5Importer getInstance() {
		if(MD5Importer.instance == null)
		{
			MD5Importer.instance = new MD5Importer();
		}
		return MD5Importer.instance;
	}

	/**
	 * Load the given md5mesh and md5anim files and add the animation to the mesh.
	 * @param md5mesh The URL points to the md5mesh file.
	 * @param modelName The name of the loaded model.
	 * @param md5anim The URL points to the md5anim file.
	 * @param animName The name of the loaded animation.
	 * @param repeatType The repeat type of the loaded animation.
	 * @throws IOException 
	 */
	public void load(URL md5mesh, String modelName, URL md5anim, String animName, int repeatType) throws IOException {
		this.loadMesh(md5mesh, modelName);
		this.loadAnim(md5anim, animName);
		this.assignAnimation(repeatType);
	}
	
	/**
	 * Load the given md5mesh file.
	 * @param md5mesh The URL points to the md5mesh file.
	 * @param name The name of the loaded model.
	 * @throws IOException 
	 */
	public void loadMesh(URL md5mesh, String name) throws IOException {
		this.setupReader(md5mesh.openStream());
		MeshImporter meshImporter = new MeshImporter(this.reader);
		this.modelNode = meshImporter.loadMesh(name);
	}
	
	/**
	 * Load the given md5anim file.
	 * @param md5anim The URL points to the md5anim file.
	 * @param name The name of the loaded animation.
	 * @throws IOException 
	 */
	public void loadAnim(URL md5anim, String name) throws IOException {
		this.setupReader(md5anim.openStream());
		AnimImporter animImporter = new AnimImporter(this.reader);
		this.animation = animImporter.loadAnim(name);
	}
	
	/**
	 * Setup the import StreamTokenizer for reading information.
	 * @param stream The input stream.
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
	 * Assign the loaded animation to the skeletion.
	 * @param repeatType The repeat type of this animation.
	 */
	private void assignAnimation(int repeatType) {
		JointController controller = new JointController(this.modelNode.getJoints());
		controller.setRepeatType(repeatType);
		controller.addAnimation(this.animation);
		controller.setActive(true);
		this.modelNode.addController(controller);
	}
	
	/**
	 * Set the MM texture filter the importer uses when loading textures.
	 * @param mm The MM texture filter.
	 */
	public void setMMFilter(int mm) {
		if(mm == Texture.MM_LINEAR || mm == Texture.MM_LINEAR_LINEAR || mm == Texture.MM_LINEAR_NEAREST || mm == Texture.MM_NEAREST || 
				mm == Texture.MM_NEAREST_LINEAR || mm == Texture.MM_NEAREST_NEAREST || mm == Texture.MM_NONE)
		{
			this.MM_Filter = mm;
		}
		else MD5Importer.logger.info("Invalid MM_Texture filter. Default bi-linear filter used.");
	}

	/**
	 * Set the FM texture filter the importer uses when loading textures.
	 * @param fm The FM texture filter.
	 */
	public void setFMFilter(int fm) {
		if(fm == Texture.FM_LINEAR || fm == Texture.FM_NEAREST)	this.FM_Filter = fm;
		else MD5Importer.logger.info("Invalid FM_Texture filter. Default linear fileter used.");
	}

	/**
	 * Set the anisotropic level the importer uses when loading textures.
	 * @param aniso The anisotropic level.
	 */
	public void setAnisotropic(int aniso) {
		if(aniso >= 0) this.anisotropic = aniso;
		else MD5Importer.logger.info("Invalid Anisotropic filter level. Default 16 used.");
	}
	
	/**
	 * Set if oriented bounding should be used for the model.
	 * @param orientedBounding True if oriented bounding should be used. False otherwise.
	 */
	public void setOrientedBounding(boolean orientedBounding) {
		this.orientedBounding = orientedBounding;
	}

	/**
	 * Retrieve the image file extensions.
	 * @return The String array of extensions.
	 */
	public String[] getExtensions() {
		return this.extensions;
	}

	/**
	 * Retrieve the MM texture filter.
	 * @return The MM texture filter.
	 */
	public int getMMFilter() {
		return this.MM_Filter;
	}

	/**
	 * Retrieve the FM texture filter.
	 * @return The FM texture filter.
	 */
	public int getFMFilter() {
		return this.FM_Filter;
	}

	/**
	 * Retrieve the anisotropic filter level.
	 * @return The anisotropic filter level.
	 */
	public int getAnisotropic() {
		return this.anisotropic;
	}

	/**
	 * Retrieve the MD5ModelNode object.
	 * @return The MD5ModelNode object.
	 */
	public ModelNode getModelNode() {
		return this.modelNode;
	}

	/**
	 * Retrieve the MD5Animation object.
	 * @return The MD5Animation object.
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
	 * Cleanup the importer.
	 */
	public void cleanup() {
		this.reader = null;
		this.modelNode = null;
		this.animation = null;
		MD5Importer.instance = null;
	}
}
