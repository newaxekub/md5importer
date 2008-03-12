package jme.model.md5;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.BitSet;
import java.util.logging.Logger;

import jme.model.md5.exception.InvalidAnimationException;
import jme.model.md5.exception.InvalidVersionException;
import jme.model.md5.resource.anim.Frame;
import jme.model.md5.resource.mesh.Joint;
import jme.model.md5.resource.mesh.Mesh;
import jme.model.md5.resource.mesh.primitive.Triangle;
import jme.model.md5.resource.mesh.primitive.Vertex;
import jme.model.md5.resource.mesh.primitive.Weight;


import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.BoneTransform;
import com.jme.animation.SkinNode;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.scene.TriMesh;

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
	// The logger object.
	private static final Logger logger = Logger.getLogger(MD5Importer.class.getName());
	// The current support versions of MD5 format.
	private static final int version = 10;
	// The image file extensions.
	private static final String[] extensions = {".jpg", ".tga", ".png", ".bmp", ".dds", ".gif", ".tif", ".tiff", ".jpeg"};
	// The importer singleton instance.
	private static MD5Importer instance;
	// The MM texture filter.
	private static int MM_Filter = Texture.MM_LINEAR_LINEAR;
	// The FM texture filter.
	private static int FM_Filter = Texture.FM_LINEAR;
	// The anisotropic value.
	private static int anisotropic = 16;
	// The stream tokenizer object.
	private StreamTokenizer reader;
	// The joint resource library.
	private Joint[] joints;
	// The mesh resource library.
	private Mesh[] meshes;
	// The frame resource library.
	private Frame[] frames;
	// The base frame of the animation.
	private Frame baseframe;
	// The frame rate of the animatin.
	private float frameRate;
	// The animation bone name hierarchy.
	private String[] idHierarchy;
	// The animation bone parent hierarchy.
	private int[] parentHierarchy;
	// The bit set frame flags.
	private BitSet frameflags;
	// XXX The bit set translation flags.
	//private BitSet transflags;
	// XXX The bit set orientation flags.
	//private BitSet orienflags;
	// The root bone object.
	private Bone Skeleton;
	// The root skin object.
	private TriMesh skin;
	// The bone animation object.
	private BoneAnimation animation;
	// The skin node object.
	private SkinNode skinNode;

	/**
	 * Private default constructor.
	 */
	private MD5Importer() {}
	
	/**
	 * Set the MM texture filter the importer uses when loading textures.
	 * @param mm The MM texture filter.
	 */
	public static void setMMFilter(int mm) {
		if(mm == Texture.MM_LINEAR || mm == Texture.MM_LINEAR_LINEAR || mm == Texture.MM_LINEAR_NEAREST || mm == Texture.MM_NEAREST || 
				mm == Texture.MM_NEAREST_LINEAR || mm == Texture.MM_NEAREST_NEAREST || mm == Texture.MM_NONE)
		{
			MD5Importer.MM_Filter = mm;
		}
		else
		{
			MD5Importer.logger.info("Invalid MM_Texture filter");
		}
	}
	
	/**
	 * Set the FM texture filter the importer uses when loading textures.
	 * @param fm The FM texture filter.
	 */
	public static void setFMFilter(int fm) {
		if(fm == Texture.FM_LINEAR || fm == Texture.FM_NEAREST)
		{
			MD5Importer.FM_Filter = fm;
		}
		else
		{
			MD5Importer.logger.info("Invalid FM_Texture filter");
		}
	}
	
	/**
	 * Set the anisotropic level the importer uses when loading textures.
	 * @param aniso The anisotropic level.
	 */
	public static void setAnisotropic(int aniso) {
		if(aniso >= 0) MD5Importer.anisotropic = aniso;
		else MD5Importer.logger.info("Invalid Anisotropic filter level");
	}
	
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
	 * Retrieve the image file extensions.
	 * @return The String array of extensions.
	 */
	public static String[] getExtensions() {
		return MD5Importer.extensions;
	}
	
	/**
	 * Retrieve the MM texture filter.
	 * @return The MM texture filter.
	 */
	public static int getMMFilter() {
		return MD5Importer.MM_Filter;
	}
	
	/**
	 * Retrieve the FM texture filter.
	 * @return The FM texture filter.
	 */
	public static int getFMFilter() {
		return MD5Importer.FM_Filter;
	}
	
	/**
	 * Retrieve the anisotropic filter level.
	 * @return The anisotropic filter level.
	 */
	public static int getAnisotropic() {
		return MD5Importer.anisotropic;
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
	 * Load the given md5mesh file.
	 * @param md5mesh The URL points to the md5mesh file.
	 * @param name The name of the loaded model.
	 * @throws IOException 
	 */
	public void loadMesh(URL md5mesh, String name) throws IOException {
		this.setupReader(md5mesh.openStream());
		this.processSkin(md5mesh);
		this.buildSkinMesh(name);
	}
	
	/**
	 * Process the information in md5mesh file for skin and skeleton.
	 * @param md5mesh The URL points to the md5mesh file.
	 * @throws IOException
	 */
	private void processSkin(URL md5mesh) throws IOException {
		String sval = null;
		while(this.reader.nextToken() != StreamTokenizer.TT_EOF)
		{
			sval = this.reader.sval;
			if(sval != null)
			{
				if(sval.equals("MD5Version"))
				{
					this.reader.nextToken();
					if(this.reader.nval != MD5Importer.version) throw new InvalidVersionException((int)this.reader.nval);
				}
				else if(sval.equals("numJoints"))
				{
					this.reader.nextToken();
					this.joints = new Joint[(int)this.reader.nval];
				}
				else if(sval.equals("numMeshes"))
				{
					this.reader.nextToken();
					this.meshes = new Mesh[(int)this.reader.nval];
				}
				else if(sval.equals("joints"))
				{
					this.reader.nextToken();
					this.processJoints();
				}
				else if(sval.equals("mesh"))
				{
					this.reader.nextToken();
					this.processMesh();
				}
			}
		}
	}
	
	/**
	 * Process the information of this section to read in all the joints.
	 * @throws IOException 
	 */
	private void processJoints() throws IOException {
		int jointIndex = 0;
		int type = -4;
		// The index of the 6 transform values.
		int transIndex = 0;
		while(this.reader.nextToken() != '}' && jointIndex < this.joints.length)
		{
			type = this.reader.ttype;
			switch(type)
			{
				case '"':
					this.joints[jointIndex] = new Joint(this.reader.sval);
					break;
				case StreamTokenizer.TT_NUMBER:
					this.joints[jointIndex].setParent((int)this.reader.nval);
					break;
				case '(':
					while(this.reader.nextToken() != ')')
					{
						this.joints[jointIndex].setTransform(transIndex, (float)this.reader.nval);
						transIndex++;
					}
					break;
				case StreamTokenizer.TT_EOL:
					if(transIndex > 5)
					{
						transIndex = 0;
						jointIndex++;
					}
					break;
				default: break;
			}
		}
	}
	
	/**
	 * Process the information of this section to read in a single mesh.
	 * @throws IOException 
	 */
	private void processMesh() throws IOException {
		int meshIndex = -1;
		for(int i = 0; i < this.meshes.length && meshIndex == -1; i++)
		{
			if(this.meshes[i] == null)
			{
				this.meshes[i] = new Mesh();
				meshIndex = i;
			}
		}
		while(this.reader.nextToken() != '}')
		{
			if(this.reader.ttype == StreamTokenizer.TT_WORD)
			{
				if(this.reader.sval.equals("shader"))
				{
					this.reader.nextToken();
					this.meshes[meshIndex].setTexture(this.reader.sval);
				}
				else if(this.reader.sval.equals("numverts"))
				{
					this.reader.nextToken();
					this.meshes[meshIndex].setVrticesCount((int)this.reader.nval);
				}
				else if(this.reader.sval.equals("vert"))
				{
					this.processVertex(meshIndex);
				}
				else if(this.reader.sval.equals("numtris"))
				{
					this.reader.nextToken();
					this.meshes[meshIndex].setTrianglesCount((int)this.reader.nval);
				}
				else if(this.reader.sval.equals("tri"))
				{
					this.processTriangle(meshIndex);
				}
				else if(this.reader.sval.equals("numweights"))
				{
					this.reader.nextToken();
					this.meshes[meshIndex].setWeightsCount((int)this.reader.nval);
				}
				else if(this.reader.sval.equals("weight"))
				{
					this.processWeight(meshIndex);
				}
			}
		}
	}
	
	/**
	 * Process the information to read in a single vertex.
	 * @param meshIndex The index number of the Mesh that is being processed.
	 * @throws IOException 
	 */
	private void processVertex(int meshIndex) throws IOException {
		int pointer = 0;
		Vertex vertex = new Vertex(meshIndex);
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL)
		{
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER)
			{
				switch(pointer)
				{
					case 0:
						this.meshes[meshIndex].setVertex((int)this.reader.nval, vertex);
						pointer++;
						break;
					case 1:
						float u = (float)this.reader.nval;
						pointer++;
						this.reader.nextToken();
						float v = (float)this.reader.nval;				
						vertex.setTextureCoords(u, v);
						pointer++;
						break;
					case 3:
						int start = (int)this.reader.nval;
						pointer++;
						this.reader.nextToken();
						int length = (int)this.reader.nval;
						vertex.setWeightIndices(start, length);
						pointer++;
						break;
					default:
						break;
				}
			}
		}
	}
	
	/**
	 * Process the information to read in a single triangle.
	 * @param meshIndex The index number of the Mesh that is being processed.
	 * @throws IOException 
	 */
	private void processTriangle(int meshIndex) throws IOException {
		int pointer = 0;
		int index = -1;
		Triangle triangle = new Triangle(meshIndex);
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL)
		{
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER)
			{
				if(pointer == 0)
				{
					this.meshes[meshIndex].setTriangle((int)this.reader.nval, triangle);
					pointer++;
				}
				else if(pointer >= 1 && pointer <= 3)
				{
					switch(pointer)
					{
						case 1:	index = 0; break;
						case 2: index = 2; break;
						case 3: index = 1; break;
					}
					triangle.setVertexIndex(index, (int)this.reader.nval);
					this.meshes[meshIndex].getVertex((int)this.reader.nval).incrementUsedTimes();
					pointer++;
				}
			}
		}
	}
	
	/**
	 * Process the information to read in a single weight.
	 * @param meshIndex The index number of the Mesh that is being processed.
	 * @throws IOException 
	 * @throws IOException 
	 */
	private void processWeight(int meshIndex) throws IOException {
		int pointer = 0;
		Weight weight = new Weight();
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL)
		{
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER)
			{
				if(pointer == 0)
				{
					this.meshes[meshIndex].setWeight((int)this.reader.nval, weight);
					pointer++;
				}
				else if(pointer == 1)
				{
					weight.setJointIndex((int)this.reader.nval);
					pointer++;
				}
				else if(pointer == 2)
				{
					weight.setWeightValue((float)this.reader.nval);
					pointer++;
				}
				else if(pointer >=3 && pointer <= 5)
				{
					weight.setPosition(pointer - 3, (float)this.reader.nval);
					pointer++;
				}
			}
		}
	}
	
	/**
	 * Build the skin and skeleton based on information read in.
	 * @param name The name of the loaded model.
	 */
	private void buildSkinMesh(String name) {
		// Setup skeleton.
		for(int i = this.joints.length - 1; i >= 0; i--)
		{
			this.joints[i].processTransform();
		}
		for(int i = 0; i < this.joints.length; i++)
		{
			this.joints[i].generateBone();
			if(this.joints[i].getParent() < 0)
			{
				this.Skeleton = this.joints[i].getBone();
			}
			else
			{
				this.joints[this.joints[i].getParent()].getBone().attachChild(this.joints[i].getBone());
			}
		}
		// Setup skin.
		this.skin = new TriMesh(name + "Skin");
		this.skin.clearBatches();
		for(int i = 0; i < this.meshes.length; i++)
		{
			this.meshes[i].generateTriangleBatch();
			this.skin.addBatch(this.meshes[i].getTriangleBatch());
		}
	}
	
	/**
	 * Load the given md5anim file.
	 * @param md5anim The URL points to the md5anim file.
	 * @param name The name of the loaded animation.
	 * @throws IOException 
	 */
	public void loadAnim(URL md5anim, String name) throws IOException {
		this.setupReader(md5anim.openStream());
		this.processAnim(md5anim);
		this.buildAnimation(name);
	}
	
	/**
	 * Process the information in md5anim file.
	 * @param md5anim The URL points to the md5anim file.
	 * @throws IOException
	 */
	private void processAnim(URL md5anim) throws IOException {
		String sval = null;
		while(this.reader.nextToken() != StreamTokenizer.TT_EOF)
		{
			sval = this.reader.sval;
			if(sval != null)
			{
				if(sval.equals("MD5Version"))
				{
					this.reader.nextToken();
					if(this.reader.nval != MD5Importer.version) throw new InvalidVersionException((int)this.reader.nval);
				}
				else if(sval.equals("numFrames"))
				{
					this.reader.nextToken();
					this.frames = new Frame[(int)this.reader.nval];
				}
				else if(sval.equals("numJoints"))
				{
					this.reader.nextToken();
					int numJoints = (int)this.reader.nval;
					if(this.joints != null && this.joints.length != numJoints) throw new InvalidAnimationException();
					this.baseframe = new Frame(true, numJoints);
					for(int i = 0; i < this.frames.length; i++)
					{
						this.frames[i] = new Frame(false, numJoints);
					}
					this.idHierarchy = new String[numJoints];
					this.parentHierarchy = new int[numJoints];
				}
				else if(sval.equals("frameRate"))
				{
					this.reader.nextToken();
					this.frameRate = (int)this.reader.nval;
				}
				else if(sval.equals("hierarchy"))
				{
					this.reader.nextToken();
					this.processHierarchy();
				}
				else if(sval.equals("baseframe"))
				{
					this.reader.nextToken();
					this.processBaseframe();
				}
				else if(sval.equals("frame"))
				{
					this.reader.nextToken();
					this.processFrame((int)this.reader.nval);
				}
			}
		}
	}
	
	/**
	 * Process the hierarchy section to obtain the BitSet flags.
	 * @throws IOException
	 */
	private void processHierarchy() throws IOException {
		this.frameflags = new BitSet();
		// XXX this.transflags = new BitSet();
		// XXX this.orienflags = new BitSet();
		int pointer = -1;
		int joint = -1;
		int flag = -1;
		while(this.reader.nextToken() != '}')
		{
			pointer++;
			switch(this.reader.ttype)
			{
				case '"':
					this.idHierarchy[joint] = this.reader.sval;
					break;
				case StreamTokenizer.TT_NUMBER:
					switch(pointer)
					{
						case 2:
							this.parentHierarchy[joint] = (int)this.reader.nval;
							break;
						case 3:
							flag = (int)this.reader.nval;
							for(int i = 0; i < 6; i++)
							{
								this.frameflags.set(joint * 6 + i, (flag & (1 << i)) != 0);
							}
							// XXX this.transflags.set(joint, (flag & (0x1 | 0x2 | 0x4)) != 0);
							// XXX this.orienflags.set(joint, (flag & (0x8 | 0x10 | 0x20)) != 0);
							break;
					}
					break;
				case StreamTokenizer.TT_EOL:
					pointer = 0;
					joint++;
					break;
			}
		}
	}
	
	/**
	 * Process information to read in the base frame.
	 * @throws IOException 
	 */
	private void processBaseframe() throws IOException {
		int pointer = -1;
		int jointIndex = -1;
		while(this.reader.nextToken() != '}')
		{
			switch(this.reader.ttype)
			{
				case '(':
					while(this.reader.nextToken() != ')')
					{
						this.baseframe.setTransform(jointIndex, pointer, (float)this.reader.nval);
						pointer++;
					}
					break;
				case StreamTokenizer.TT_EOL:
					pointer = 0;
					jointIndex++;
					break;
				default:
					break;
			}
		}
		this.baseframe.processTransform();
	}
	
	/**
	 * Process information to read in a frame.
	 * @param index The index number of the frame.
	 * @throws IOException 
	 */
	private void processFrame(int index) throws IOException {
		float[] values = new float[6];
		for(int i = 0; i < this.parentHierarchy.length; i++)
		{
			for(int j = 0; j < values.length; j++)
			{
				if(this.frameflags.get(i * 6 + j))
				{
					while(this.reader.nextToken() != StreamTokenizer.TT_NUMBER);
					values[j] = (float)this.reader.nval;
				}
				else
				{
					values[j] = this.baseframe.getTransformValue(i, j);
				}
			}
			// XXX Check with the translation/orientation flags. See MD5AnimReader line 212 and 234.
			// XXX This may be different from the MD5AnimReader.
			if(this.parentHierarchy[i] < 0)
			{
				this.frames[index].setTransform(i, 0, values[2]);
				this.frames[index].setTransform(i, 1, values[1]);
				this.frames[index].setTransform(i, 2, values[0]);
				this.frames[index].setTransform(i, 3, values[5]);
				this.frames[index].setTransform(i, 4, values[4]);
				this.frames[index].setTransform(i, 5, values[3]);
			}
			else
			{
				for(int t = 0; t < values.length; t++)
				{
					this.frames[index].setTransform(i, t, values[t]);
				}
			}
		}
		this.frames[index].processTransform();
	}
	
	/**
	 * Build BoneAnimation based on information read in.
	 * @param name The name of the loaded animation.
	 */
	private void buildAnimation(String name) {
		this.animation = new BoneAnimation(name, this.Skeleton, this.frames.length);
		this.animation.setInterpolationRate(1.0f/this.frameRate);
		this.animation.setStartFrame(0);
		this.animation.setEndFrame(this.frames.length - 1);
		float[] times = new float[this.frames.length];
		for(int i = 0; i < times.length; i++)
		{
			times[i] = i * (1.0f/this.frameRate);
		}
		this.animation.setTimes(times);
		BoneTransform[] transforms = new BoneTransform[this.idHierarchy.length];
		for(int i = 0; i < transforms.length; i++)
		{
			if(this.joints != null)	transforms[i] = new BoneTransform(this.joints[i].getBone(), this.frames.length);
			else
			{
				transforms[i] = new BoneTransform(null, this.frames.length);
				transforms[i].setBoneId(this.idHierarchy[i]);
			}
			for(int j = 0; j < this.frames.length; j++)
			{
				transforms[i].setTranslation(j, this.frames[j].getTranslation(i));
				transforms[i].setRotation(j, this.frames[j].getOrientation(i));
			}
			this.animation.addBoneTransforms(transforms[i]);
		}
	}
	
	/**
	 * Assign the loaded animation to the skeletion.
	 * @param repeatType The repeat type of this animation.
	 */
	private void assignAnimation(int repeatType) {
		AnimationController controller = new AnimationController();
		controller.setRepeatType(repeatType);
		controller.setSkeleton(this.Skeleton);
		controller.clearAnimations();
		controller.addAnimation(this.animation);
		controller.setActiveAnimation(this.animation);
		controller.setActive(true);
		this.Skeleton.addController(controller);
	}
	
	/**
	 * Retrieve the Joint with given index.
	 * @param index The index number of the Joint.
	 * @return The Joint object with given index number.
	 */
	public Joint getJoint(int index) {
		return this.joints[index];
	}
	
	/**
	 * Retrieve the Mesh with given index.
	 * @param index The index number of the Mesh.
	 * @return The Mesh object with given index number.
	 */
	public Mesh getMesh(int index) {
		return this.meshes[index];
	}
	
	/**
	 * Retrieve the animation bone parent hierarchy.
	 * @return The animation bone parent hierarchy.
	 */
	public int[] getParents() {
		return this.parentHierarchy;
	}
	
	/**
	 * Retrieve the static skin geometry.
	 * @return The static skin geometry object.
	 */
	public TriMesh getSkin() {
		return this.skin;
	}
	
	/**
	 * Retrieve the root Bone object.
	 * @return The root Bone object.
	 */
	public Bone getSkeleton() {
		return this.Skeleton;
	}
	
	/**
	 * Retrieve the BoneAnimation object.
	 * @return The BoneAnimation object.
	 */
	public BoneAnimation getAnimation() {
		return this.animation;
	}

	/**
	 * Retrieve the SkinNode object.
	 * @return The SkinNode object.
	 */
	public SkinNode getSkinNode() {
		this.skinNode = new SkinNode(this.skin.getName()+"Node");
		this.skinNode.setSkeleton(this.Skeleton);
		this.skinNode.setSkin(this.skin);
		// Bone influence.
		Weight weight = null;
		for(int i = 0; i < this.meshes.length; i++)
		{
			for(int j = 0; j < this.meshes[i].getVertexCount(); j++)
			{
				for(int k = 0; k < this.meshes[i].getVertex(j).getWeightCount(); k++)
				{
					weight = this.meshes[i].getWeight(this.meshes[i].getVertex(j).getWeightIndex(k));
					this.skinNode.addBoneInfluence(i, j, this.joints[weight.getJointIndex()].getBone(), weight.getWeightValue());
				}
			}
		}
		return this.skinNode;
	}
	
	/**
	 * Cleanup the importer.
	 * @param instance True if the importer instance should be removed.
	 */
	public void cleanup(boolean instance) {
		this.joints = null;
		this.meshes = null;
		this.frames = null;
		if(instance)
		{
			MD5Importer.instance = null;
		}
	}
}
