package com.model.md5.importer.resource;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import com.jme.math.Vector3f;
import com.model.md5.MD5Node;
import com.model.md5.exception.InvalidVersionException;
import com.model.md5.importer.MD5Importer;
import com.model.md5.interfaces.mesh.IJoint;
import com.model.md5.interfaces.mesh.IMesh;
import com.model.md5.interfaces.mesh.primitive.ITriangle;
import com.model.md5.interfaces.mesh.primitive.IVertex;
import com.model.md5.interfaces.mesh.primitive.IWeight;
import com.model.md5.resource.mesh.Joint;
import com.model.md5.resource.mesh.Mesh;
import com.model.md5.resource.mesh.primitive.Triangle;
import com.model.md5.resource.mesh.primitive.Vertex;
import com.model.md5.resource.mesh.primitive.Weight;

/**
 * <code>MeshImporter</code> is responsible for importing MD5Mesh resources and
 * constructing the final <code>IMD5Node</code> instance.
 * <p>
 * <code>MeshImporter</code> is used by <code>MD5Importer</code> internally only.
 *
 * @author Yi Wang (Neakor)
 * @version Modified date: 11-19-2008 17:13 EST
 */
public class MeshImporter extends Importer {
	/**
	 * The array of <code>IJoint</code> that form the skeleton.
	 */
	private IJoint[] joints;
	/**
	 * The array of <code>IMesh</code> which represents the actual geometry.
	 */
	private IMesh[] meshes;
	/**
	 * The <code>String</code> texture file for a single mesh.
	 */
	private String texture;
	/**
	 * The array of <code>IVertex</code> for a single mesh.
	 */
	private IVertex[] vertices;
	/**
	 * The array of <code>ITriangle</code> for a single mesh.
	 */
	private ITriangle[] triangles;
	/**
	 * The array of <code>IWeight</code> for a single mesh.
	 */
	private IWeight[] weights;
	/**
	 * The <code>List</code> of weight <code>Integer</code> indices array.
	 */
	private List<int[]> weightIndices;
	/**
	 * The final <code>ModelNode</code> instance.
	 */
	private MD5Node node;

	/**
	 * Constructor of <code>MeshImporter</code>.
	 * @param reader The <code>StreamTokenizer</code> instance setup for reading file.
	 */
	public MeshImporter(StreamTokenizer reader) {
		super(reader);
		this.weightIndices = new ArrayList<int[]>();
	}

	/**
	 * Load the md5mesh file and construct the final <code>ModelNode</code>.
	 * @param name The name of the loaded <code>ModelNode</code>.
	 * @return The loaded <code>ModelNode</code> instance.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	public MD5Node loadMesh(String name) throws IOException {
		this.processSkin();
		this.constructSkin(name);
		return this.node;
	}

	/**
	 * Process the information in md5mesh file.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processSkin() throws IOException {
		String sval = null;
		int meshIndex = 0;
		while(this.reader.nextToken() != StreamTokenizer.TT_EOF) {
			sval = this.reader.sval;
			if(sval != null) {
				if(sval.equals("MD5Version")) {
					this.reader.nextToken();
					if(this.reader.nval != MD5Importer.version) throw new InvalidVersionException((int)this.reader.nval);
				} else if(sval.equals("numJoints")) {
					this.reader.nextToken();
					this.joints = new IJoint[(int)this.reader.nval];
				} else if(sval.equals("numMeshes")) {
					this.reader.nextToken();
					this.meshes = new IMesh[(int)this.reader.nval];
				} else if(sval.equals("joints")) {
					this.reader.nextToken();
					this.processJoints();
				} else if(sval.equals("mesh")) {
					this.reader.nextToken();
					this.processMesh(meshIndex);
					meshIndex++;
				}
			}
		}
	}

	/**
	 * Process the information to construct all <code>Joint</code>.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processJoints() throws IOException {
		int jointIndex = -1;
		String id = null;
		Vector3f translation = null;
		Vector3f orientation = null;
		IJoint parent = null;
		while(this.reader.nextToken() != '}' && jointIndex < this.joints.length) {
			switch(this.reader.ttype) {
			case '"':
				id = this.reader.sval;
				break;
			case StreamTokenizer.TT_NUMBER:
				int index = (int)this.reader.nval;
				if(index >= 0) parent = this.joints[index];
				break;
			case '(':
				translation = this.readVector();
				orientation = this.readVector();
				break;
			case StreamTokenizer.TT_EOL:
				if(jointIndex >= 0) {
					this.joints[jointIndex] = new Joint(jointIndex, id, translation, orientation);
					this.joints[jointIndex].setParent(parent);
				}
				jointIndex++;
				break;
			}
		}
	}

	/**
	 * Process the information to construct a single <code>Mesh</code>.
	 * @param meshIndex The <code>Integer</code> index of the mesh.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processMesh(int meshIndex) throws IOException {
		// Make sure we clear the weight indices list since meshes are separated.
		this.weightIndices.clear();
		// Load in the mesh.
		while(this.reader.nextToken() != '}') {
			if(this.reader.ttype == StreamTokenizer.TT_WORD) {
				if(this.reader.sval.equals("shader")) {
					this.reader.nextToken();
					this.texture = this.reader.sval;
				} else if(this.reader.sval.equals("numverts")) {
					this.reader.nextToken();
					this.vertices = new IVertex[(int)this.reader.nval];
				} else if(this.reader.sval.equals("vert")) {
					this.processVertex();
				} else if(this.reader.sval.equals("numtris")) {
					this.reader.nextToken();
					this.triangles = new ITriangle[(int)this.reader.nval];
				} else if(this.reader.sval.equals("tri")) {
					this.processTriangle();
				} else if(this.reader.sval.equals("numweights")) {
					this.reader.nextToken();
					this.weights = new IWeight[(int)this.reader.nval];
				} else if(this.reader.sval.equals("weight")) {
					this.processWeight();
				}
			}
		}
		// Set the weights for the vertices in this mesh.
		for(IVertex vertex : this.vertices) {
			int[] indices = this.weightIndices.get(vertex.getIndex());
			IWeight[] weights = new IWeight[indices.length];
			for(int i = 0; i < weights.length; i++) {
				weights[i] = this.weights[indices[i]];
			}
			vertex.setWeights(weights);
		}
		// Construct the mesh.
		this.meshes[meshIndex] = new Mesh(this.texture, this.vertices, this.triangles, this.weights);
	}

	/**
	 * Process the information to construct a single vertex.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processVertex() throws IOException {
		int pointer = 0;
		IVertex vertex = null;
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL) {
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER) {
				if(pointer == 0) {
					int index = (int)this.reader.nval;
					vertex = new Vertex(index);
					this.vertices[index] = vertex;
					pointer++;
				} else if(pointer == 1) {
					float u = (float)this.reader.nval;
					pointer++;
					this.reader.nextToken();
					float v = (float)this.reader.nval;				
					vertex.setTextureCoords(u, v);
					pointer++;
				} else if(pointer == 3) {
					int start = (int)this.reader.nval;
					pointer++;
					this.reader.nextToken();
					int length = (int)this.reader.nval;
					int[] indices = new int[length];
					for(int i = 0; i < length; i++) indices[i] = start + i;
					this.weightIndices.add(vertex.getIndex(), indices);
					pointer++;
					break;
				}
			}
		}
	}

	/**
	 * Process the information to construct in a single triangle.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processTriangle() throws IOException {
		int pointer = 0;
		int index = -1;
		IVertex[] vertices = new IVertex[3];
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL) {
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER) {
				if(pointer == 0) {
					index = (int)this.reader.nval;
					pointer++;
				} else if(pointer >= 1 && pointer <= 3) {
					IVertex vertex = this.vertices[(int)this.reader.nval];
					// This is an important trick to make sure the triangles are winded correctly.
					switch(pointer) {
					case 1: vertices[0] = vertex; break;
					case 2: vertices[2] = vertex; break;
					case 3: vertices[1] = vertex; break;
					}
					vertex.incrementUsedTimes();
					pointer++;
				}
			}
		}
		this.triangles[index] = new Triangle(index, vertices);
	}

	/**
	 * Process the information to construct in a single weight.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processWeight() throws IOException {
		int pointer = 0;
		int index = -1;
		IJoint joint = null;
		float value = 0;
		Vector3f position = null;
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL) {
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER) {
				if(pointer == 0) {
					index = (int)this.reader.nval;
					pointer++;
				} else if(pointer == 1) {
					joint = this.joints[(int)this.reader.nval];
					pointer++;
				} else if(pointer == 2) {
					value = (float)this.reader.nval;
					pointer++;
				} else if(pointer ==3) {
					position = this.readVector();
					pointer++;
					break;
				}
			}
		}
		this.weights[index] = new Weight(index, value, position);
		this.weights[index].setJoint(joint);
	}

	/**
	 * Construct the skin based on information read in.
	 * @param The <code>String</code> name for the node.
	 */
	private void constructSkin(String name) {
		// Process the joints.
		for(int i = this.joints.length - 1; i >= 0; i--) {
			this.joints[i].processTransform();
		}
		for(int i = 0; i < this.joints.length; i++) {
			if(this.joints[i].getParent() == null) {
				this.joints[i].getOrientation().set(MD5Importer.base.mult(this.joints[i].getOrientation()));
			}
		}
		// Construct the node.
		this.node = new MD5Node(name, this.joints, this.meshes);
		this.node.initialize();
		this.joints = null;
		this.meshes = null;
	}
}
