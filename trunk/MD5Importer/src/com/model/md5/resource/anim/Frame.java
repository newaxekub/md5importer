package com.model.md5.resource.anim;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * Frame maintains the information of a frame in md5anim file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Frame implements Serializable, Savable{
	// Serial version.
	private static final long serialVersionUID = 8891271219195292580L;
	// The flag indicates if this frame is a baseframe.
	private boolean baseframe;
	// The animation bone parent hierarchy.
	private int[] parents;
	// The translations of joints in this frame.
	private Vector3f[] translations;
	// The orientations of joints in this frame.
	private Quaternion[] orientations;
	
	/**
	 * Default constructor of Frame.
	 */
	public Frame() {}
	
	/**
	 * Constructor of Frame.
	 * @param baseframe True if this frame is a baseframe. False otherwise.
	 * @param numJoints The total number of Joints in the animation.
	 */
	public Frame(boolean baseframe, int numJoints) {
		this.baseframe = baseframe;
		this.translations = new Vector3f[numJoints];
		this.orientations = new Quaternion[numJoints];
		for(int i = 0; i < this.translations.length && i < this.orientations.length; i++)
		{
			this.translations[i] = new Vector3f();
			this.orientations[i] = new Quaternion();
		}
	}
	
	/**
	 * Set the bone hierarchy of the Frame.
	 * @param parents The integer array of bone hierarchy.
	 */
	public void setParents(int[] parents) {
		this.parents = parents;
	}
	
	/**
	 * Set the transform of this frame.
	 * @param jointIndex The index of the joint.
	 * @param index The transform index number.
	 * @param value The transform value to be set.
	 */
	public void setTransform(int jointIndex, int index, float value) {
		switch(index)
		{
			case 0:
				this.translations[jointIndex].x = value;
				break;
			case 1:
				this.translations[jointIndex].y = value;
				break;
			case 2:
				this.translations[jointIndex].z = value;
				break;
			case 3:
				this.orientations[jointIndex].x = value;
				break;
			case 4:
				this.orientations[jointIndex].y = value;
				break;
			case 5:
				this.orientations[jointIndex].z = value;
				this.processOrientation(jointIndex, this.orientations[jointIndex]);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Process the Quaternion orientation to finalize it.
	 * @param jointIndex The index of the joint.
	 * @param raw The raw orientation value.
	 */
	private void processOrientation(int jointIndex, Quaternion raw) {
		float t = 1.0f - (raw.x * raw.x) - (raw.y * raw.y) - (raw.z * raw.z);
		if (t < 0.0f) raw.w = 0.0f;
		else raw.w = -(FastMath.sqrt(t));
	}
	
	/**
	 * Retrieve the index of the parent Joint of the Joint with given index.
	 * @return The index of the parent Joint.
	 */
	public int getParent(int index) {
		return this.parents[index];
	}
	
	/**
	 * Retrieve the transform value with given indices.
	 * @param jointIndex The joint index.
	 * @param transIndex The transform index.
	 * @return The transform value.
	 */
	public float getTransformValue(int jointIndex, int transIndex) {
		switch(transIndex)
		{
			case 0:
				return this.translations[jointIndex].x;
			case 1:
				return this.translations[jointIndex].y;
			case 2:
				return this.translations[jointIndex].z;
			case 3:
				return this.orientations[jointIndex].x;
			case 4:
				return this.orientations[jointIndex].y;
			case 5:
				return this.orientations[jointIndex].z;
			default:
				return 0;
		}
	}
	
	/**
	 * Retrieve the Vector3f translation value with given joint index.
	 * @param jointIndex The joint number.
	 * @return The Vector3f translation value.
	 */
	public Vector3f getTranslation(int jointIndex) {
		return this.translations[jointIndex];
	}
	
	/**
	 * Retrieve the Quaternion orientation value with given joint index.
	 * @param jointIndex The joint number.
	 * @return The Quaternion orientation value.
	 */
	public Quaternion getOrientation(int jointIndex) {
		return this.orientations[jointIndex];
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Frame.class;
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		Savable[] temp = null;
		InputCapsule ic = im.getCapsule(this);
		this.baseframe = ic.readBoolean("Baseframe", false);
		this.parents = ic.readIntArray("Parents", null);
		temp = ic.readSavableArray("Translations", null);
		this.translations = new Vector3f[temp.length];
		for(int i = 0; i < temp.length; i++)
		{
			this.translations[i] = (Vector3f)temp[i];
		}
		temp = ic.readSavableArray("Orientations", null);
		this.orientations = new Quaternion[temp.length];
		for(int i = 0; i < temp.length; i++)
		{
			this.orientations[i] = (Quaternion)temp[i];
		}
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.baseframe, "Baseframe", false);
		oc.write(this.parents, "Parents", null);
		oc.write(this.translations, "Translations", null);
		oc.write(this.orientations, "Orientations", null);
	}
}
