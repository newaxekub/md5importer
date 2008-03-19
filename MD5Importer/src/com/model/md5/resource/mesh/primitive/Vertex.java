package com.model.md5.resource.mesh.primitive;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.resource.mesh.Mesh;

/**
 * Vertex maintains the information of a vert in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Vertex implements Serializable, Savable{
	// Serial version.
	private static final long serialVersionUID = 6774812007144718188L;
	// The mesh this vertex belongs to.
	private Mesh mesh;
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
	 * Default constructor of Vertex.
	 */
	public Vertex() {
		this.position = new Vector3f();
	}
	
	/**
	 * Constructor of Vertex.
	 * @param mesh The Mesh this Vertex belongs to.
	 */
	public Vertex(Mesh mesh) {
		this.mesh = mesh;
		this.position = new Vector3f();
	}
	
	/**
	 * Process the Vertex position.
	 */
	public void processPosition() {
		this.position.zero();
		Vector3f temp = new Vector3f();
		for(int i = 0; i < this.weightIndices.length; i++)
		{
			temp.set(this.mesh.getWeight(this.weightIndices[i]).getPosition());
			this.mesh.getModelNode().getJoint(this.mesh.getWeight(this.weightIndices[i]).getJointIndex()).getTransform().multPoint(temp);
			temp.multLocal(this.mesh.getWeight(this.weightIndices[i]).getWeightValue());
			this.position.addLocal(temp);
		}
	}
	
	/**
	 * Reset the normal and position information of this vertex.
	 */
	public void resetInformation() {
		this.normal.zero();
		this.position.zero();
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

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Vertex.class;
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.mesh = (Mesh)ic.readSavable("Mesh", null);
		this.textureCoords = (Vector2f)ic.readSavable("TextureCoords", null);
		this.weightIndices = ic.readIntArray("WeightIndices", null);
		this.usedTimes = ic.readInt("UsedTimes", 0);
		this.normal = (Vector3f)ic.readSavable("Normal", null);
		this.position = (Vector3f)ic.readSavable("Position", null);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.mesh, "Mesh", null);
		oc.write(this.textureCoords, "TextureCoords", null);
		oc.write(this.weightIndices, "WeightIndices", null);
		oc.write(this.usedTimes, "UsedTimes", 0);
		oc.write(this.normal, "Normal", null);
		oc.write(this.position, "Position", null);
	}
}
