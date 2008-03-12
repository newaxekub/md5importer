package com.model.md5.resource.mesh;

import java.io.IOException;
import java.io.Serializable;


import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.TransformMatrix;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.ModelNode;

/**
 * Joint maintains the information of a joint in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Joint implements Serializable, Savable{
	// Serial version.
	private static final long serialVersionUID = -926371530130383637L;
	// The model node this joint belongs.
	private ModelNode modelNode;
	// The name of the joint.
	private String name;
	// The parent joint index.
	private int parent;
	// The parent joint index of this joint in the parent ModelNode.
	private int nodeParent;
	// The translation transform.
	private Vector3f translation;
	// The orientation transform.
	private Quaternion orientation;
	// The relative transform matrix of this joint.
	private TransformMatrix transform;

	/**
	 * Default constructor of Joint.
	 */
	public Joint() {}
	
	/**
	 * Constructor of Joint.
	 * @param name
	 */
	public Joint(String name, ModelNode modelNode) {
		this.name = name;
		this.modelNode = modelNode;
		this.parent = -1;
		this.nodeParent = -1;
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
		this.transform = new TransformMatrix();
	}
	
	/**
	 * Update the translation and orientation of this Joint.
	 * @param translation The new Vector3f translation of this Joint.
	 * @param orientation The new Quaternion orientation of this Joint.
	 */
	public void updateTransform(Vector3f translation, Quaternion orientation) {
		this.translation.set(translation);
		this.orientation.set(orientation);
		this.modelNode.flagUpdate();
	}
	
	/**
	 * Process the translation and orientation of this Joint This process has to
	 * be started from the bottom of Joint tree up to the root Joint.
	 * @param parentTrans The parent Joint Vector3f translation.
	 * @param parentOrien The parent Joint Quaternion orientation.
	 */
	public void processTransform(Vector3f parentTrans, Quaternion parentOrien) {
		if(parentTrans == null || parentOrien == null)
		{
			parentOrien = new Quaternion();
			parentTrans = new Vector3f();
		}
		this.orientation.set(parentOrien.inverse().multLocal(this.orientation));
		this.translation.subtractLocal(parentTrans);
		parentOrien.inverse().multLocal(this.translation);
		if(this.parent < 0)
		{
			this.orientation.set(ModelNode.base.mult(this.orientation));
		}
	}
	
	/**
	 * Process the relative transforms of this Joint.
	 */
	public void processRelative() {
		this.transform.loadIdentity();
		if(this.parent >= 0) this.transform.set(this.modelNode.getJoint(this.parent).getTransform());
		else this.transform.set(this.getBaseTransform());
		this.transform.multLocal(new TransformMatrix(this.orientation, this.translation), new Vector3f());
	}
	
	/**
	 * Get the base transform based on parent Joint of this Joint in the parent ModelNode.
	 * @return The base TransformMatrix.
	 */
	private TransformMatrix getBaseTransform() {
		if(this.nodeParent < 0) return new TransformMatrix();
		else
		{
			TransformMatrix matrix = new TransformMatrix();
			matrix.combineWithParent(((ModelNode)this.modelNode.getParent()).getJoint(this.nodeParent).getTransform());
			return matrix;
		}
	}
	
	/**
	 * Set the parent Joint index of this Joint.
	 * @param parent The index of the parent Joint.
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}
	
	/**
	 * Set the parent Joint index of this Joint in the parent ModelNode.
	 * @param outerParent The index of the parent Joint.
	 */
	public void setNodeParent(int outerParent) {
		this.nodeParent = outerParent;
	}
	
	/**
	 * Set one of the 6 transform values.
	 * @param index The index of the transform values.
	 * @param value The actual value to be set.
	 */
	public void setTransform(int index, float value) {
		switch(index)
		{
			case 0: this.translation.setX(value); break;
			case 1: this.translation.setY(value); break;
			case 2: this.translation.setZ(value); break;
			case 3: this.orientation.x = value; break;
			case 4: this.orientation.y = value; break;
			case 5:
				this.orientation.z = value;
				this.processOrientation();
				break;
			default: break;
		}
	}
	
	/**
	 * Compute the w value of the orientation.
	 */
	private void processOrientation() {
		float t = 1.0f-(this.orientation.x*this.orientation.x)-(this.orientation.y*this.orientation.y)-(this.orientation.z*this.orientation.z);
		if (t < 0.0f) this.orientation.w = 0.0f;
		else this.orientation.w = -(FastMath.sqrt(t));
	}
	
	/**
	 * Retrieve the translation of this Joint read from MD5 file.
	 * @return The Vector3f translation read directly from MD5 file.
	 */
	public Vector3f getTranslation() {
		return this.translation;
	}
	
	/**
	 * Retrieve the orientation of this Joint read from MD5 file.
	 * @return The Quaternion orientation read directly from MD5 file.
	 */
	public Quaternion getOrientation() {
		return this.orientation;
	}

	/**
	 * Retrieve the relative TransformMatrix of this Joint.
	 * @return The relative TransformMatrix of this Joint.
	 */
	public TransformMatrix getTransform() {
		return this.transform;
	}
	
	/**
	 * Retrieve the index of the parent Joint.
	 * @return The index of the parent Joint.
	 */
	public int getParent() {
		return this.parent;
	}
	
	/**
	 * Retrieve the name of this Joint.
	 * @return The name of this Joint.
	 */
	public String getName() {
		return this.name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Joint.class;
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.modelNode = (ModelNode)ic.readSavable("ModelNode", null);
		this.name = ic.readString("Name", null);
		this.parent = ic.readInt("Parent", -1);
		this.nodeParent = ic.readInt("NodeParent", -1);
		this.translation = (Vector3f)ic.readSavable("Translation", null);
		this.orientation = (Quaternion)ic.readSavable("Orientation", null);
		this.transform = (TransformMatrix)ic.readSavable("Transform", null);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.modelNode, "ModelNode", null);
		oc.write(this.name, "Name", null);
		oc.write(this.parent, "Parent", -1);
		oc.write(this.nodeParent, "NodeParent", -1);
		oc.write(this.translation, "Translation", null);
		oc.write(this.orientation, "Orientation", null);
		oc.write(this.transform, "Transform", null);
	}
}
