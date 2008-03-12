package jme.model.md5.resource.mesh.primitive;


import jme.model.md5.MD5Importer;
import jme.model.md5.resource.mesh.Mesh;

import com.jme.math.Vector3f;

/**
 * Triangle maintains the information of a tri in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Triangle {
	// The index of the mesh this triangle belongs to.
	private int meshIndex;
	// The vertex indices array.
	private int[] vertexIndices;
	
	/**
	 * Constructor of Triangle.
	 * @param meshIndex The index number of the Mesh this Vertex belongs to.
	 */
	public Triangle(int meshIndex) {
		this.meshIndex = meshIndex;
		this.vertexIndices = new int[3];
	}
	
	/**
	 * Process the normal of vertices and store the normal values in Vertex.
	 */
	public void processNormal() {
		Mesh mesh = MD5Importer.getInstance().getMesh(this.meshIndex);
		Vector3f temp1 = new Vector3f();
		Vector3f temp2 = new Vector3f();
		Vertex vert1 = mesh.getVertex(this.vertexIndices[0]);
		Vertex vert2 = mesh.getVertex(this.vertexIndices[1]);
		Vertex vert3 = mesh.getVertex(this.vertexIndices[2]);
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
	 * Retrieve the number of vertex indices held in this Triangle.
	 * @return Obviously it should return 3 or you are in trouble.
	 */
	public int getVertexCount() {
		return this.vertexIndices.length;
	}
	
	/**
	 * Retrieve the index of the Vertex in this Triangle with given index.
	 * @param index The index number of the Vertex.
	 * @return The index of the Vertex in this Triangle with given index.
	 */
	public int getVertexIndex(int index) {
		return this.vertexIndices[index];
	}
}
