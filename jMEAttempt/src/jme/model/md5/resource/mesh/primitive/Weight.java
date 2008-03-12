package jme.model.md5.resource.mesh.primitive;

import com.jme.math.Vector3f;

/**
 * Weight maintains the information of a weight in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Weight {
	// The index of the joint this weight applies to.
	private int jointIndex;
	// The value of this weight.
	private float value;
	// The position of this weight.
	private Vector3f position;
	
	/**
	 * Constructor of Weight.
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
}
