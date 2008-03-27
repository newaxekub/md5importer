package com.model.md5.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.BitSet;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.model.md5.JointAnimation;
import com.model.md5.ModelNode;
import com.model.md5.controller.JointController;
import com.model.md5.exception.InvalidAnimationException;
import com.model.md5.exception.InvalidVersionException;
import com.model.md5.resource.anim.Frame;
import com.model.md5.resource.mesh.Joint;
import com.model.md5.resource.mesh.Mesh;
import com.model.md5.resource.mesh.primitive.Triangle;
import com.model.md5.resource.mesh.primitive.Vertex;
import com.model.md5.resource.mesh.primitive.Weight;

/**
 * MD5Importer provides a machanism to load models and animations of MD5 format.
 * The importer is a singleton object which should be cleaned after importing
 * process. For details on MD5 format, please go to official MD5 wiki at
 * http://www.modwiki.net/wiki/MD5_(file_format).
 * 
 * @author Yi Wang (Neakor)
 */
public class MD5Importer {
	// The logger object.
	private static final Logger logger = Logger.getLogger(MD5Importer.class.getName());
	// The base orientation value.
	private static final Quaternion base = new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f);
	// The current support versions of MD5 format.
	private static final int version = 10;
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
	// The joint resource library.
	private Joint[] joints;
	// The mesh resource library.
	private Mesh[] meshes;
	// The frame rate of the animatin.
	private float frameRate;
	// The animation bone name hierarchy.
	private String[] idHierarchy;
	// The animation bone parent hierarchy.
	private int[] parentHierarchy;
	// The bit set frame flags.
	private BitSet frameflags;
	// The base frame of the animation.
	private Frame baseframe;
	// The frame resource library.
	private Frame[] frames;
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
		this.modelNode = new ModelNode(name);
		this.setupReader(md5mesh.openStream());
		this.processSkin(md5mesh);
		this.buildSkinMesh();
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
					this.joints[jointIndex] = new Joint(this.reader.sval, this.modelNode);
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
				this.meshes[i] = new Mesh(this.modelNode);
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
					this.processVertex(this.meshes[meshIndex]);
				}
				else if(this.reader.sval.equals("numtris"))
				{
					this.reader.nextToken();
					this.meshes[meshIndex].setTrianglesCount((int)this.reader.nval);
				}
				else if(this.reader.sval.equals("tri"))
				{
					this.processTriangle(this.meshes[meshIndex]);
				}
				else if(this.reader.sval.equals("numweights"))
				{
					this.reader.nextToken();
					this.meshes[meshIndex].setWeightCount((int)this.reader.nval);
				}
				else if(this.reader.sval.equals("weight"))
				{
					this.processWeight(this.meshes[meshIndex]);
				}
			}
		}
	}
	
	/**
	 * Process the information to read in a single vertex.
	 * @param mesh The Mesh that is being processed.
	 * @throws IOException 
	 */
	private void processVertex(Mesh mesh) throws IOException {
		int pointer = 0;
		Vertex vertex = new Vertex(mesh);
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL)
		{
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER)
			{
				switch(pointer)
				{
					case 0:
						mesh.setVertex((int)this.reader.nval, vertex);
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
	 * @param mesh The Mesh that is being processed.
	 * @throws IOException 
	 */
	private void processTriangle(Mesh mesh) throws IOException {
		int pointer = 0;
		int index = -1;
		Triangle triangle = new Triangle(mesh);
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL)
		{
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER)
			{
				if(pointer == 0)
				{
					mesh.setTriangle((int)this.reader.nval, triangle);
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
					mesh.getVertex((int)this.reader.nval).incrementUsedTimes();
					pointer++;
				}
			}
		}
	}
	
	/**
	 * Process the information to read in a single weight.
	 * @param mesh The Mesh that is being processed.
	 * @throws IOException 
	 * @throws IOException 
	 */
	private void processWeight(Mesh mesh) throws IOException {
		int pointer = 0;
		Weight weight = new Weight();
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL)
		{
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER)
			{
				if(pointer == 0)
				{
					mesh.setWeight((int)this.reader.nval, weight);
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
	 */
	private void buildSkinMesh() {
		Joint parent = null;
		for(int i = this.joints.length - 1; i >= 0; i--)
		{
			if(this.joints[i].getParent() < 0) this.joints[i].processTransform(null, null);
			else
			{
				parent = this.joints[this.joints[i].getParent()];
				this.joints[i].processTransform(parent.getTranslation(), parent.getOrientation());
			}
		}
		for(int i = 0; i < this.joints.length; i++)
		{
			if(this.joints[i].getParent() < 0)
			{
				this.joints[i].getOrientation().set(MD5Importer.base.mult(this.joints[i].getOrientation()));
			}
		}
		this.modelNode.setJoints(this.joints);
		this.modelNode.setMeshes(this.meshes);
		this.modelNode.initialize();
		this.joints = null;
		this.meshes = null;
	}
	
	/**
	 * Load the given md5anim file.
	 * @param md5anim The URL points to the md5anim file.
	 * @param name The name of the loaded animation.
	 * @throws IOException 
	 */
	public void loadAnim(URL md5anim, String name) throws IOException {
		this.animation = new JointAnimation(name);
		this.setupReader(md5anim.openStream());
		this.processAnim(md5anim);
		this.buildAnimation();
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
							break;
					}
					break;
				case StreamTokenizer.TT_EOL:
					pointer = 0;
					joint++;
					break;
			}
		}
		this.baseframe.setParents(this.parentHierarchy);
		for(int i = 0; i < this.frames.length; i++)
		{
			this.frames[i].setParents(this.parentHierarchy);
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
		for(int i = 0 ; i < this.parentHierarchy.length; i++)
		{
			if(this.baseframe.getParent(i) < 0)
			{
				this.baseframe.getOrientation(i).set(MD5Importer.base.mult(this.baseframe.getOrientation(i)));
			}
		}
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
	}
	
	/**
	 * Build MD5Animation based on information read in.
	 */
	private void buildAnimation() {
		this.animation.setJointIDs(this.idHierarchy);
		this.animation.setFrames(this.frames);
		this.animation.setFrameRate(this.frameRate);
		this.idHierarchy = null;
		this.parentHierarchy = null;
		this.frameflags = null;
		this.baseframe = null;
		this.frames = null;
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
		else
		{
			MD5Importer.logger.info("Invalid MM_Texture filter");
		}
	}

	/**
	 * Set the FM texture filter the importer uses when loading textures.
	 * @param fm The FM texture filter.
	 */
	public void setFMFilter(int fm) {
		if(fm == Texture.FM_LINEAR || fm == Texture.FM_NEAREST)
		{
			this.FM_Filter = fm;
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
	public void setAnisotropic(int aniso) {
		if(aniso >= 0) this.anisotropic = aniso;
		else MD5Importer.logger.info("Invalid Anisotropic filter level");
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
	public boolean isOrientedBounding() {
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
