package com.model.md5.resource.mesh.primitive;

import java.io.IOException;
import java.io.Serializable;


import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.resource.mesh.Mesh;

/**
 * Triangle maintains the information of a tri in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Triangle implements Serializable, Savable{
	// Serial version.
	private static final long serialVersionUID = -6234457193386375719L;
	// The mesh this triangle belongs to.
	private Mesh mesh;
	// The vertex indices array.
	private int[] vertexIndices;
	
	/**
	 * Default constructor of Triangle.
	 */
	public Triangle() {}
	
	/**
	 * Constructor of Triangle.
	 * @param mesh The Mesh this Vertex belongs to.
	 */
	public Triangle(Mesh mesh) {
		this.mesh = mesh;
		this.vertexIndices = new int[3];
	}
	
	/**
	 * Process the normal of vertices and store the normal values in Vertex.
	 */
	public void processNormal() {
		Vector3f temp1 = new Vector3f();
		Vector3f temp2 = new Vector3f();
		Vertex vert1 = this.mesh.getVertex(this.vertexIndices[0]);
		Vertex vert2 = this.mesh.getVertex(this.vertexIndices[1]);
		Vertex vert3 = this.mesh.getVertex(this.vertexIndices[2]);
		temp1.set(vert2.getPosition()).subtractLocal(vert1.getPosition());
		temp2.set(vert3.getPosition()).subtractLocal(vert2.getPosition());
		temp1.crossLocal(temp2);
		temp1.normalizeLocal();
		vert1.setNormal(temp2.set(temp1).multLocal(1.0f/(float)vert1.getUsedTimes()));
		vert2.setNormal(temp2.set(temp1).multLocal(1.0f/(float)vert2.getUsedTimes()));
		vert3.setNormal(temp1.multLocal(1.0f/(float)vert3.getUsedTimes()));
	}
	
	/**
	 * Set the index of vertex of this Triangle.
	 * @param index The index of the vertex indices array.
	 * @param vertex The index of the vertex to be set.
	 */
	public void setVertexIndex(int index, int vertex) {
		this.vertexIndices[index] = vertex;
	}
	
	/**
	 * Retrieve the index of the Vertex in this Triangle with given index.
	 * @param index The index number of the Vertex.
	 * @return The index of the Vertex in this Triangle with given index.
	 */
	public int getVertexIndex(int index) {
		return this.vertexIndices[index];
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Triangle.class;
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.mesh = (Mesh)ic.readSavable("Mesh", null);
		this.vertexIndices = ic.readIntArray("VertexIndices", null);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.mesh, "Mesh", null);
		oc.write(this.vertexIndices, "VertexIndices", null);
	}
}
