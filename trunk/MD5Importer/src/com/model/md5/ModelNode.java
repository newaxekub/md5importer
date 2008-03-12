package com.model.md5;

import java.io.IOException;


import com.jme.math.Quaternion;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.resource.mesh.Joint;
import com.model.md5.resource.mesh.Mesh;

/**
 * ModelNode is the final product of MD5 loading process. It manages the loaded
 * Joint and Mesh. It is updated when Joint transforms are changed.
 * 
 * @author Yi Wang (Neakor)
 */
public class ModelNode extends Node{
	// The base orientation value.
	public static final Quaternion base = new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f);
	// Serial version.
	private static final long serialVersionUID = -2799207065296472869L;
	// The flag indicates if an geometric update is needed.
	private boolean update;
	// The joints of this model.
	private Joint[] joints;
	// The meshes of this model.
	private Mesh[] meshes;
	// The skin of this model node.
	private TriMesh skin;
	
	/**
	 * Default constructor of ModelNode.
	 */
	public ModelNode() {
		super();
		this.skin = new TriMesh(name + "Skin");
	}
	
	/**
	 * Constructor of MD5ModelNode.
	 * @param name The name of this MD5ModelNode.
	 */
	public ModelNode(String name) {
		super(name);
		this.skin = new TriMesh(name + "Skin");
	}
	
	/**
	 * Initialize the MD5ModelNode.
	 */
	public void initialize() {
		this.processJoints();
		this.skin.clearBatches();
		for(int i = 0; i < this.meshes.length; i++)
		{
			this.meshes[i].generateBatch();
			this.skin.addBatch(this.meshes[i].getTriangleBatch());
		}
		this.attachChild(this.skin);
	}

	/**
	 * Process the Joint relative transformations.
	 */
	private void processJoints() {
		for(int i = 0; i < this.joints.length; i++)
		{
			this.joints[i].processRelative();
		}
	}

	/**
	 * Updates all the geometry information for the node.
	 * @param time The frame time.
	 * @param initiator True if this node started the update process.
	 */
	public void updateGeometricState(float time, boolean initiator) {
		if(this.update)
		{
			this.processJoints();
			for(int i = 0; i < this.meshes.length; i++)
			{
				this.meshes[i].updateBatch();
			}
			this.update = false;
		}
		super.updateGeometricState(time, initiator);
	}
	
	/**
	 * Attach the given ModelNode to the Joint with given ID.
	 * @param node The ModelNode needs to be attached.
	 * @param jointID The ID of the Joint to attach to.
	 */
	public void attachChild(ModelNode node, String jointID) {
		int jointIndex = -1;
		for(int i = 0; i < this.joints.length && jointIndex == -1; i++)
		{
			if(this.joints[i].getName().equals(jointID)) jointIndex = i;
		}
		this.attachChild(node, jointIndex);
	}
	
	/**
	 * Attach the given ModelNode to the Joint with given index.
	 * @param node The ModelNode needs to be attached.
	 * @param jointIndex The index of the Joint to attach to.
	 */
	public void attachChild(ModelNode node, int jointIndex) {
		this.getRootJoint(node).setNodeParent(jointIndex);
		this.attachChild(node);
		node.initialize();
	}
	
	/**
	 * Get the root Joint of the given ModelNode.
	 * @param node The ModelNode to check from.
	 * @return The root Joint object of given ModelNode.
	 */
	private Joint getRootJoint(ModelNode node) {
		for(int i = 0; i < node.getJoints().length; i++)
		{
			if(node.getJoint(i).getParent() < 0) return node.getJoint(i);
		}
		return null;
	}
	
	/**
	 * Notify the MD5ModelNode to update geometric information.
	 */
	public void flagUpdate() {
		this.update = true;
	}

	/**
	 * Set the Joint of this model node.
	 * @param joints The Joint array.
	 */
	public void setJoints(Joint[] joints) {
		this.joints = new Joint[joints.length];
		System.arraycopy(joints, 0, this.joints, 0, this.joints.length);
	}
	
	/**
	 * Set the Mesh of this model node.
	 * @param meshes The Mesh array.
	 */
	public void setMeshes(Mesh[] meshes) {
		this.meshes = new Mesh[meshes.length];
		System.arraycopy(meshes, 0, this.meshes, 0, this.meshes.length);
	}
	
	/**
	 * Retrieve the Joint array of this node.
	 * @return The array of Joint in this model node.
	 */
	public Joint[] getJoints() {
		return this.joints;
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
	
	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return ModelNode.class;
	}
	
	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);
		Savable[] temp = null;
		InputCapsule ic = im.getCapsule(this);
		temp = ic.readSavableArray("Joints", null);
		this.joints = new Joint[temp.length];
		for(int i = 0; i < temp.length; i++)
		{
			this.joints[i] = (Joint)temp[i];
		}
		temp = ic.readSavableArray("Meshes", null);
		this.meshes = new Mesh[temp.length];
		for(int i = 0; i < temp.length; i++)
		{
			this.meshes[i] = (Mesh)temp[i];
		}
		this.skin = (TriMesh)ic.readSavable("Skin", null);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.joints, "Joints", null);
		oc.write(this.meshes, "Meshes", null);
		oc.write(this.skin, "Skin", null);
	}
}
