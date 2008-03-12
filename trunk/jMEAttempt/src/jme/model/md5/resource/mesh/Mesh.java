package jme.model.md5.resource.mesh;

import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import jme.model.md5.MD5Importer;
import jme.model.md5.resource.mesh.primitive.Triangle;
import jme.model.md5.resource.mesh.primitive.Vertex;
import jme.model.md5.resource.mesh.primitive.Weight;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.scene.SceneElement;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;


/**
 * Mesh maintains the information of a mesh in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Mesh {
	// The texture file name without extension.
	private String texture;
	// The vertices array.
	private Vertex[] vertices;
	// The triangles array.
	private Triangle[] triangles;
	// The weights array.
	private Weight[] weights;
	// The generated triangle batch of this mesh.
	private TriangleBatch triangleBatch;
	
	/**
	 * Constructor of Mesh.
	 */
	public Mesh() {}
	
	/**
	 * Generate a TriangleBatch object based on the information stored.
	 */
	public void generateTriangleBatch() {
		this.triangleBatch = new TriangleBatch();
		this.triangleBatch.setNormalsMode(SceneElement.NM_GL_NORMALIZE_PROVIDED);
		this.processIndex();
		this.processVertex();
		this.processNormal();
		this.processTexture();
		this.processBounding();
	}
	
	/**
	 * Process and setup the index buffer.
	 */
	private void processIndex() {
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(this.triangles.length*3);
		indexBuffer.clear();
		for(int i = 0; i < this.triangles.length; i++)
		{
			for(int j = 0; j < this.triangles[i].getVertexCount(); j++)
			{
				indexBuffer.put(this.triangles[i].getVertexIndex(j));
			}
		}
		indexBuffer.flip();
		this.triangleBatch.setIndexBuffer(indexBuffer);
	}
	
	/**
	 * Process and setup the vertex position buffer.
	 */
	private void processVertex() {
		FloatBuffer vertexBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		for(int i = 0; i < this.vertices.length; i++)
		{
			this.vertices[i].processPosition();
			BufferUtils.setInBuffer(this.vertices[i].getPosition(), vertexBuffer, i);
		}
		this.triangleBatch.setVertexBuffer(vertexBuffer);
	}
	
	/**
	 * Process and setup the normal position buffer.
	 */
	private void processNormal() {
		// Triangles have to process thr normal first incase the vertices are not in order.
		for(int i = 0; i < this.triangles.length; i++)
		{
			this.triangles[i].processNormal();
		}
		FloatBuffer normalBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		for(int i = 0; i < this.vertices.length; i++)
		{
			BufferUtils.setInBuffer(this.vertices[i].getNormal(), normalBuffer, i);
		}
		this.triangleBatch.setNormalBuffer(normalBuffer);
	}
	
	/**
	 * Process and setup the TextureState and texture UV buffer.
	 */
	private void processTexture() {
		FloatBuffer textureBuffer = BufferUtils.createVector2Buffer(this.vertices.length);
		float maxU = 1; float maxV = 1; float minU = 0; float minV = 0;
		for(int i = 0; i < this.vertices.length; i++)
		{
			BufferUtils.setInBuffer(this.vertices[i].getTextureCoords(), textureBuffer, i);
			if(this.vertices[i].getTextureCoords().x > maxU) maxU = this.vertices[i].getTextureCoords().x;
			else if(this.vertices[i].getTextureCoords().x < minU) minU = this.vertices[i].getTextureCoords().x;
			if(this.vertices[i].getTextureCoords().y > maxV) maxV = this.vertices[i].getTextureCoords().y;
			else if(this.vertices[i].getTextureCoords().y < minV) minV = this.vertices[i].getTextureCoords().y;
		}
		this.triangleBatch.setTextureBuffer(textureBuffer, 0);
		URL url = null;
		for(int i = 0; i < MD5Importer.getExtensions().length && url == null; i++)
		{
			url = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, this.texture+MD5Importer.getExtensions()[i]);
		}
		Texture color = TextureManager.loadTexture(url,MD5Importer.getMMFilter(),MD5Importer.getFMFilter(),MD5Importer.getAnisotropic(),true);
		if(color != null)
		{
			if(maxU > 1 || minU < 0)
			{
				if (maxV > 1 || minV < 0) color.setWrap(Texture.WM_WRAP_S_WRAP_T);
				else
				{
					if(color.getWrap() != Texture.WM_WRAP_S_WRAP_T)
					{
						if (color.getWrap() == Texture.WM_CLAMP_S_WRAP_T) color.setWrap(Texture.WM_WRAP_S_WRAP_T);
						else color.setWrap(Texture.WM_WRAP_S_CLAMP_T);
					}
				}
			}
			else if(maxV > 1 || minV < 0)
			{
				if(color.getWrap() != Texture.WM_WRAP_S_WRAP_T)
				{
					if(color.getWrap() == Texture.WM_WRAP_S_CLAMP_T) color.setWrap(Texture.WM_WRAP_S_WRAP_T);
					else color.setWrap(Texture.WM_CLAMP_S_WRAP_T);
				}
			}
		}
		TextureState state = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		state.setTexture(color);
		this.triangleBatch.setRenderState(state);
	}
	
	/**
	 * Process and setup the bounding volume of the TriangleBatch.
	 */
	private void processBounding() {
		this.triangleBatch.setModelBound(new BoundingBox());
		this.triangleBatch.updateModelBound();
	}

	/**
	 * Set the texture file name of this mesh.
	 * @param texture The texture file name without extension.
	 */
	public void setTexture(String texture) {
		this.texture = texture;
	}
	
	/**
	 * Setup the vertices array based on the given count.
	 * @param count The number of vertices in this mesh.
	 */
	public void setVrticesCount(int count) {
		this.vertices = new Vertex[count];
	}
	
	/**
	 * Set the Vertex with given index number.
	 * @param index The index of the Vertex.
	 * @param vertex The Vertext to be set.
	 */
	public void setVertex(int index, Vertex vertex) {
		this.vertices[index] = vertex;
	}
	
	/**
	 * Setup the triangles array based on the given count.
	 * @param count The number of triangles in this mesh.
	 */
	public void setTrianglesCount(int count) {
		this.triangles = new Triangle[count];
	}
	
	/**
	 * Set the Triangle with given index number.
	 * @param index The index of the Triangle.
	 * @param triangle The Triangle to be set.
	 */
	public void setTriangle(int index, Triangle triangle) {
		this.triangles[index] = triangle;
	}
	
	/**
	 * Setup the weights array based on the given count.
	 * @param count The number of weights in this mesh.
	 */
	public void setWeightsCount(int count) {
		this.weights = new Weight[count];
	}
	
	/**
	 * Set the Weight with given index number.
	 * @param index The index of the Weight.
	 * @param weight The Weight to be set.
	 */
	public void setWeight(int index, Weight weight) {
		this.weights[index] = weight;
	}
	
	/**
	 * Retrieve the Vertex with given index number.
	 * @param index The index number of the Vertex.
	 * @return The Vertex object with given index number.
	 */
	public Vertex getVertex(int index) {
		return this.vertices[index];
	}
	
	/**
	 * Retrieve the number of Vertex in this mesh.
	 * @return The number of Vertex in this mesh.
	 */
	public int getVertexCount() {
		return this.vertices.length;
	}
	
	/**
	 * Retrieve the Weight with given index.
	 * @param index The weight index number.
	 * @return The Weight object with given index.
	 */
	public Weight getWeight(int index) {
		return this.weights[index];
	}
	
	/**
	 * Retrieve the TriangleBatch generated by this Mesh.
	 * @return The generated TriangleBatch object.
	 */
	public TriangleBatch getTriangleBatch() {
		return this.triangleBatch;
	}
}
