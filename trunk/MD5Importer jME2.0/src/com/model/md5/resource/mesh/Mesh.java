package com.model.md5.resource.mesh;

import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;
import com.model.md5.resource.mesh.primitive.Triangle;
import com.model.md5.resource.mesh.primitive.Vertex;
import com.model.md5.resource.mesh.primitive.Weight;

/**
 * <code>Mesh</code> represents a mesh in md5mesh file.
 * <p>
 * <code>Mesh</code> maintains a number of <code>Vertex</code>, <code>Weight</code>
 * and <code>Triangle</code> to represent the basic geometry of a <code>ModelNode</code>.
 * It extends <code>TriMesh</code> to interface with jME rendering system.
 * <p>
 * <code>Mesh</code> does not directly process any geometric information but delegates
 * the process down to the primitive elements it maintains.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-09-2008 17:49 EST
 */
public class Mesh extends TriMesh {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -6431941710991131243L;
	/**
	 * The <code>ModelNode</code> this <code>Mesh</code> belongs to.
	 */
	private ModelNode modelNode;
	/**
	 * The texture file name without extension.
	 */
	private String texture;
	/**
	 * The array of <code>Vertex</code> in this <code>Mesh</code>.
	 */
	private Vertex[] vertices;
	/**
	 * The array of <code>Triangle</code> in this <code>Mesh</code>.
	 */
	private Triangle[] triangles;
	/**
	 * The array of <code>Weight</code> in this <code>Mesh</code>.
	 */
	private Weight[] weights;
	
	/**
	 * Default constructor of <code>Mesh</code>.
	 */
	public Mesh() {
		super();
	}
	
	/**
	 * Constructor of <code>Mesh</code>.
	 * @param modelNode The <code>ModelNode</code> this <code>Mesh</code> belongs to.
	 */
	public Mesh(ModelNode modelNode) {
		this.modelNode = modelNode;
	}
	
	/**
	 * Initialize this <code>Mesh</code> and its geometric data.
	 */
	public void initializeMesh() {
		this.setNormalsMode(Spatial.NormalsMode.AlwaysNormalize);
		this.processIndex();
		this.processVertex();
		this.processNormal();
		this.processTexture();
		this.processBounding();
	}
	
	/**
	 * Update this <code>Mesh</code> and its geometric data.
	 */
	public void updateMesh() {
		for(int i = 0; i < this.vertices.length; i++) {
			this.vertices[i].resetInformation();
		}
		this.processVertex();
		this.processNormal();
		this.updateModelBound();
	}
	
	/**
	 * Process and setup the index buffer.
	 */
	private void processIndex() {
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(this.triangles.length*3);
		indexBuffer.clear();
		for(int i = 0; i < this.triangles.length; i++) {
			for(int j = 0; j < 3; j++) {
				indexBuffer.put(this.triangles[i].getVertexIndex(j));
			}
		}
		indexBuffer.flip();
		this.setIndexBuffer(indexBuffer);
	}
	
	/**
	 * Process and setup the vertex position buffer.
	 */
	private void processVertex() {
		FloatBuffer vertexBuffer = this.getVertexBuffer();
		if(vertexBuffer == null) vertexBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		vertexBuffer.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			this.vertices[i].processPosition();
			BufferUtils.setInBuffer(this.vertices[i].getPosition(), vertexBuffer, i);
		}
		this.setVertexBuffer(vertexBuffer);
	}
	
	/**
	 * Process and setup the normal position buffer.
	 */
	private void processNormal() {
		// Triangles have to process thr normal first incase the vertices are not in order.
		for(int i = 0; i < this.triangles.length; i++) {
			this.triangles[i].processNormal();
		}
		FloatBuffer normalBuffer = this.getNormalBuffer();
		if(normalBuffer == null) normalBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		normalBuffer.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			BufferUtils.setInBuffer(this.vertices[i].getNormal(), normalBuffer, i);
		}
		this.setNormalBuffer(normalBuffer);
	}
	
	/**
	 * Process and setup the <code>TextureState</code> and texture UV buffer.
	 */
	private void processTexture() {
		MD5Importer instance = MD5Importer.getInstance();
		FloatBuffer textureBuffer = BufferUtils.createVector2Buffer(this.vertices.length);
		float maxU = 1; float maxV = 1; float minU = 0; float minV = 0;
		for(int i = 0; i < this.vertices.length; i++) {
			BufferUtils.setInBuffer(this.vertices[i].getTextureCoords(), textureBuffer, i);
			if(this.vertices[i].getTextureCoords().x > maxU) maxU = this.vertices[i].getTextureCoords().x;
			else if(this.vertices[i].getTextureCoords().x < minU) minU = this.vertices[i].getTextureCoords().x;
			if(this.vertices[i].getTextureCoords().y > maxV) maxV = this.vertices[i].getTextureCoords().y;
			else if(this.vertices[i].getTextureCoords().y < minV) minV = this.vertices[i].getTextureCoords().y;
		}
		this.setTextureCoords(new TexCoords(textureBuffer));
		URL url = null;
		for(int i = 0; i < instance.getExtensions().length && url == null; i++) {
			url = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, this.texture+instance.getExtensions()[i]);
		}
		Texture color = TextureManager.loadTexture(url,instance.getMiniFilter(),instance.getMagFilter(),instance.getAnisotropic(),true);
		if(color != null) {
			if(maxU > 1) color.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
			else color.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Clamp);
			if(maxV > 1) color.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
			else color.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Clamp);
		}
		TextureState state = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		state.setTexture(color);
		this.setRenderState(state);
	}
	
	/**
	 * Process and setup the bounding volume of the <code>Mesh</code>.
	 */
	private void processBounding() {
		if(MD5Importer.getInstance().isOriented()) this.setModelBound(new OrientedBoundingBox());
		else this.setModelBound(new BoundingBox());
		this.updateModelBound();
		this.updateGeometricState(0, true);
	}

	/**
	 * Set the texture file name of this <code>Mesh</code>.
	 * @param texture The texture file name without extension.
	 */
	public void setTexture(String texture) {
		this.texture = texture;
	}
	
	/**
	 * Setup the vertices array based on the given count.
	 * @param count The number of vertices in this <code>Mesh</code>.
	 */
	public void setVrticesCount(int count) {
		this.vertices = new Vertex[count];
	}
	
	/**
	 * Set the <code>Vertex</code> with given index number.
	 * @param index The index of the <code>Vertex</code>.
	 * @param vertex The <code>Vertex</code> to be set.
	 */
	public void setVertex(int index, Vertex vertex) {
		this.vertices[index] = vertex;
	}
	
	/**
	 * Setup the triangles array based on the given count.
	 * @param count The number of triangles in this <code>Mesh</code>.
	 */
	public void setTrianglesCount(int count) {
		this.triangles = new Triangle[count];
	}
	
	/**
	 * Set the <code>Triangle</code> with given index number.
	 * @param index The index of the <code>Triangle</code>.
	 * @param triangle The <code>Triangle</code> to be set.
	 */
	public void setTriangle(int index, Triangle triangle) {
		this.triangles[index] = triangle;
	}
	
	/**
	 * Setup the weights array based on the given count.
	 * @param count The number of weights in this <code>Mesh</code>.
	 */
	public void setWeightCount(int count) {
		this.weights = new Weight[count];
	}
	
	/**
	 * Set the <code>Weight</code> with given index number.
	 * @param index The index of the <code>Weight</code>.
	 * @param weight The <code>Weight</code> to be set.
	 */
	public void setWeight(int index, Weight weight) {
		this.weights[index] = weight;
	}
	
	/**
	 * Retrieve the <code>ModelNode</code> this <code>Mesh</code> belongs to.
	 * @return The <code>ModelNode</code> this <code>Mesh</code> belongs to.
	 */
	public ModelNode getModelNode() {
		return this.modelNode;
	}
	
	/**
	 * Retrieve the <code>Vertex</code> with given index number.
	 * @param index The index number of the <code>Vertex</code>.
	 * @return The <code>Vertex</code> instance with given index number.
	 */
	public Vertex getVertex(int index) {
		return this.vertices[index];
	}
	
	/**
	 * Retrieve the <code>Weight</code> with given index number.
	 * @param index The index number of the <code>Weight</code>.
	 * @return The <code>Weight</code> instance with given index number.
	 */
	public Weight getWeight(int index) {
		return this.weights[index];
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Mesh.class;
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);
		Savable[] temp = null;
		InputCapsule ic = im.getCapsule(this);
		this.modelNode = (ModelNode)ic.readSavable("ModelNode", null);
		this.texture = ic.readString("Texture", null);
		temp = ic.readSavableArray("Vertices", null);
		this.vertices = new Vertex[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.vertices[i] = (Vertex)temp[i];
		}
		temp = ic.readSavableArray("Triangles", null);
		this.triangles = new Triangle[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.triangles[i] = (Triangle)temp[i];
		}
		temp = ic.readSavableArray("Weights", null);
		this.weights = new Weight[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.weights[i] = (Weight)temp[i];
		}
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.modelNode, "ModelNode", null);
		oc.write(this.texture, "Texture", null);
		oc.write(this.vertices, "Vertices", null);
		oc.write(this.triangles, "Triangles", null);
		oc.write(this.weights, "Weights", null);
	}
}
