package jme.model.md5.resource.mesh.primitive;


import jme.model.md5.MD5Importer;
import jme.model.md5.resource.mesh.Mesh;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;

/**
 * Vertex maintains the information of a vert in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Vertex {
	// The index of the mesh this vertex belongs to.
	private int meshIndex;
	// The texture coordinates of this vertex.
	private Vector2f textureCoords;
	// The weight indices array.
	private int[] weightIndices;
	// The number of times this vertex has been used.
	private int usedTimes;
	// The normal of this vertex.
	private Vector3f normal;
	// The object position of this vertex.
	private Vector3f position;
	
	/**
	 * Constructor of Vertex.
	 * @param meshIndex The index number of the Mesh this Vertex belongs to.
	 */
	public Vertex(int meshIndex) {
		this.meshIndex = meshIndex;
	}
	
	/**
	 * Process the Vertex position.
	 */
	public void processPosition() {
		this.position = new Vector3f();
		Vector3f temp = new Vector3f();
		MD5Importer instance = MD5Importer.getInstance();
		Mesh mesh = instance.getMesh(this.meshIndex);
		for(int i = 0; i < this.weightIndices.length; i++)
		{
			temp.set(mesh.getWeight(this.weightIndices[i]).getPosition());
			instance.getJoint(mesh.getWeight(this.weightIndices[i]).getJointIndex()).getRelativeTransform().multPoint(temp);
			temp.multLocal(mesh.getWeight(this.weightIndices[i]).getWeightValue());
			this.position.addLocal(temp);
		}
	}
	
	/**
	 * Increment the number of times this Vertex has been used by a Triangle.
	 */
	public void incrementUsedTimes() {
		this.usedTimes++;
	}

	/**
	 * Set the texture coordinates of this vertex.
	 * @param u The u value.
	 * @param v The un-inverted v value.
	 */
	public void setTextureCoords(float u, float v) {
		// Invert the v value.
		float invertV = 1.0f - v;
		this.textureCoords = new Vector2f(u, invertV);
	}
	
	/**
	 * Set the indices of Weight that affects this Vertex.
	 * @param start The starting index number.
	 * @param length The number of weights that affect this Vertex.
	 */
	public void setWeightIndices(int start, int length) {
		this.weightIndices = new int[length];
		for(int i = 0; i < this.weightIndices.length; i++)
		{
			this.weightIndices[i] = start + i;
		}
	}
	
	/**
	 * Set the normal Vector3f of this Vertex.
	 * @param normal The normal vector to be set.
	 */
	public void setNormal(Vector3f normal) {
		if(this.normal == null) this.normal = new Vector3f(normal);
		// If this vertex has been used, add the new value.
		else this.normal.addLocal(normal);
	}
	
	/**
	 * Retrieve the number of Weights that affect this Vertex.
	 * @return The number of weights.
	 */
	public int getWeightCount() {
		return this.weightIndices.length;
	}
	
	/**
	 * Retrieve the index of the Weight with given index.
	 * @param index The index number of the weights array.
	 * @return The index of the Weight with given index.
	 */
	public int getWeightIndex(int index) {
		return this.weightIndices[index];
	}
	
	/**
	 * Retrieve the texture coordinates of this Vertex.
	 * @return The Vector2f texture coordinates of this Vertex.
	 */
	public Vector2f getTextureCoords() {
		return this.textureCoords;
	}
	
	/**
	 * Retrieve the number of times this Vertex has been used by Triangle.
	 * @return The number of times this Vertex has been used by Triangle.
	 */
	public int getUsedTimes() {
		return this.usedTimes;
	}
	
	/**
	 * Retrieve the position of this Vertex.
	 * @return The Vector3f position of this Vertex.
	 */
	public Vector3f getPosition() {
		return this.position;
	}
	
	/**
	 * Retrieve the normal of this Vertex.
	 * @return The Vector3f normal of this Vertex.
	 */
	public Vector3f getNormal() {
		return this.normal;
	}
}
