package com.model.md5.resource.mesh.primitive;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * Weight maintains the information of a weight in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Weight implements Serializable, Savable{
	// Serial version.
	private static final long serialVersionUID = 4719214599414606855L;
	// The index of the joint this weight applies to.
	private int jointIndex;
	// The value of this weight.
	private float value;
	// The position of this weight.
	private Vector3f position;
	
	/**
	 * Default constructor of Weight.
	 */
	public Weight() {
		this.position = new Vector3f();
	}
	
	/**
	 * Set the index of the joint this weight applies to.
	 * @param index The index of the joint this weight applies to.
	 */
	public void setJointIndex(int index) {
		this.jointIndex = index;
	}
	
	/**
	 * Set the value of this Weight.
	 * @param value The value of this Weight.
	 */
	public void setWeightValue(float value) {
		this.value = value;
	}
	
	/**
	 * Set the position of this Weight.
	 * @param index The index of position value to set.
	 * @param value The float value to be set.
	 */
	public void setPosition(int index, float value) {
		switch(index)
		{
			case 0:	this.position.setX(value); break;
			case 1: this.position.setY(value); break;
			case 2: this.position.setZ(value); break;
			default: break;
		}
	}
	
	/**
	 * Retrieve the index of the Joint this Weight affects.
	 * @return The index number of the Joint.
	 */
	public int getJointIndex() {
		return this.jointIndex;
	}
	
	/**
	 * Retrieve the weight value of this Weight.
	 * @return The float weight value.
	 */
	public float getWeightValue() {
		return this.value;
	}
	
	/**
	 * Retrieve the position of this Weight.
	 * @return The Vector3f position fo this Weight.
	 */
	public Vector3f getPosition() {
		return this.position;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Weight.class;
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.jointIndex = ic.readInt("JointIndex", -1);
		this.value = ic.readFloat("Value", 0);
		this.position = (Vector3f)ic.readSavable("Position", null);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.jointIndex, "JointIndex", -1);
		oc.write(this.value, "Value", 0);
		oc.write(this.position, "Position", null);
	}
}
