package com.model.md5;

import java.io.IOException;

import com.jme.scene.Node;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.resource.mesh.Joint;
import com.model.md5.resource.mesh.Mesh;

/**
 * <code>ModelNode</code> is the final product of MD5 loading process.
 * <p>
 * <code>ModelNode</code> maintains the loaded <code>Joint</code> and <code>Mesh</code>
 * objects and update them accordingly.
 *
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-09-2008 17:52 EST
 */
public class ModelNode extends Node{
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -2799207065296472869L;
	/**
	 * The flag indicates if an geometric update is needed.
	 */
	private boolean update;
	/**
	 * The flag indicates if model node shares skeleton with its parent.
	 */
	private boolean dependent;
	/**
	 * The array of <code>Joint</code> of this <code>ModelNode</code>.
	 */
	private Joint[] joints;
	/**
	 * The array of <code>Mesh</code> of this <code>ModelNode</code>.
	 */
	private Mesh[] meshes;
	
	/**
	 * Default constructor of <code>ModelNode</code>.
	 */
	public ModelNode() {
		super();
	}
	
	/**
	 * Constructor of <code>ModelNode</code>.
	 * @param name The name of this <code>ModelNode</code>.
	 */
	public ModelNode(String name) {
		super(name);
	}
	
	/**
	 * Initialize the <code>ModelNode</code>.
	 */
	public void initialize() {
		if(this.meshes == null) {
			for(int i = 0; i < this.meshes.length; i++) {
				this.detachChild(this.meshes[i]);
			}
		}
		if(!this.dependent) this.processJoints();
		for(int i = 0; i < this.meshes.length; i++) {
			this.meshes[i].initializeMesh();
			this.attachChild(this.meshes[i]);
		}
	}

	/**
	 * Process the <code>Joint</code> relative transformations.
	 */
	private void processJoints() {
		for(int i = 0; i < this.joints.length; i++) {
			this.joints[i].processRelative();
		}
	}

	/**
	 * Updates all the geometric information for the <code>ModelNode</code>.
	 * @param time The frame time.
	 * @param initiator True if this <code>Node</code> started the update process.
	 */
	public void updateGeometricState(float time, boolean initiator) {
		if(this.update) {
			if(!this.dependent) this.processJoints();
			for(int i = 0; i < this.meshes.length; i++) {
				this.meshes[i].updateMesh();
			}
			this.update = false;
		}
		super.updateGeometricState(time, initiator);
	}
	
	/**
	 * Attach the given <code>ModelNode</code> to the <code>Joint</code> with given ID.
	 * @param node The <code>ModelNode</code> needs to be attached.
	 * @param jointID The ID of the <code>Joint</code> to attach to.
	 */
	public void attachChild(ModelNode node, String jointID) {
		int jointIndex = -1;
		for(int i = 0; i < this.joints.length && jointIndex == -1; i++) {
			if(this.joints[i].getName().equals(jointID)) jointIndex = i;
		}
		this.attachChild(node, jointIndex);
	}
	
	/**
	 * Attach the given <code>ModelNode</code> to the <code>Joint</code> with given index.
	 * @param node The <code>ModelNode</code> needs to be attached.
	 * @param jointIndex The index of the <code>Joint</code> to attach to.
	 */
	public void attachChild(ModelNode node, int jointIndex) {
		this.getRootJoint(node).setNodeParent(jointIndex);
		this.attachChild(node);
		node.initialize();
	}
	
	/**
	 * Attach the given <code>ModelNode</code> as a dependent child which shares the
	 * skeleton with this <code>ModelNode</code>.
	 * @param node The dependent <code>ModelNode</code> needs to be attached.
	 */
	public void attachDependent(ModelNode node) {
		node.dependent = true;
		node.setJoints(this.joints);
		this.attachChild(node);
		node.initialize();
	}
	
	/**
	 * Get the root <code>Joint</code> of the given <code>ModelNode</code>.
	 * @param node The <code>ModelNode</code> to check from.
	 * @return The root <code>Joint</code> object of given <code>ModelNode</code>.
	 */
	private Joint getRootJoint(ModelNode node) {
		for(int i = 0; i < node.getJoints().length; i++) {
			if(node.getJoint(i).getParent() < 0) return node.getJoint(i);
		}
		return null;
	}
	
	/**
	 * Notify the <code>ModelNode</code> to update its geometric information.
	 */
	public void flagUpdate() {
		this.update = true;
	}

	/**
	 * Set the <code>Joint</code> of this <code>ModelNode</code>.
	 * @param joints The <code>Joint</code> array.
	 */
	public void setJoints(Joint[] joints) {
		this.joints = joints;
	}
	
	/**
	 * Set the <code>Mesh</code> of this <code>ModelNode</code>.
	 * @param meshes The <code>Mesh</code> array.
	 */
	public void setMeshes(Mesh[] meshes) {
		this.meshes = meshes;
	}
	
	/**
	 * Retrieve the <code>Joint</code> array of this <code>ModelNode</code>.
	 * @return The array of <code>Joint</code> of this <code>ModelNode</code>.
	 */
	public Joint[] getJoints() {
		return this.joints;
	}

	/**
	 * Retrieve the <code>Joint</code> with given index.
	 * @param index The index number of the <code>Joint</code>.
	 * @return The <code>Joint</code> instance with given index number.
	 */
	public Joint getJoint(int index) {
		return this.joints[index];
	}
	
	/**
	 * Retrieve the <code>Mesh</code> with given index.
	 * @param index The index number of the <code>Mesh</code>.
	 * @return The <code>Mesh</code> instance with given index number.
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
		InputCapsule ic = im.getCapsule(this);
		this.dependent = ic.readBoolean("Dependent", false);
		Savable[] temp = null;
		temp = ic.readSavableArray("Joints", null);
		this.joints = new Joint[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.joints[i] = (Joint)temp[i];
		}
		temp = ic.readSavableArray("Meshes", null);
		this.meshes = new Mesh[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.meshes[i] = (Mesh)temp[i];
		}
		this.initialize();
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		for(int i = 0; i < this.meshes.length; i++) {
			this.detachChild(this.meshes[i]);
		}
		super.write(ex);
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.dependent, "Dependent", false);
		oc.write(this.joints, "Joints", null);
		oc.write(this.meshes, "Meshes", null);
		for(int i = 0; i < this.meshes.length; i++) {
			this.attachChild(this.meshes[i]);
		}
	}
}
