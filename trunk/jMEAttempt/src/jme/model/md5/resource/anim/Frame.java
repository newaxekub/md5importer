package jme.model.md5.resource.anim;


import jme.model.md5.MD5Importer;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * Frame maintains the information of a frame in md5anim file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Frame {
	// The flag indicates if this frame is a baseframe.
	private boolean baseframe;
	// The translations of joints in this frame.
	private Vector3f[] translations;
	// The orientations of joints in this frame.
	private Quaternion[] orientations;
	
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
	 * Process the translations and orientations of this Frame into local space.
	 */
	public void processTransform() {
//		int[] parents = MD5Importer.getInstance().getParents();
//		Vector3f parentTrans = null;
//		Quaternion parentOrien = null;
//
//		
//		Matrix4f matrix = new Matrix4f();
//		for(int i = 0; i < parents.length; i++)
//		{
//			if(parents[i] >= 0)
//			{
//				parentOrien = this.orientations[parents[i]];
//				parentTrans = this.translations[parents[i]];
//				parentOrien.toRotationMatrix(matrix);
//				matrix.rotateVect(this.translations[i]);
//				this.translations[i].addLocal(parentTrans);
//				this.orientations[i].multLocal(parentOrien);
//			}
//		}
//		
//		for(int i = parents.length - 1; i >= 0; i--)
//		{
//			if(parents[i] >= 0)
//			{
//				parentOrien = this.orientations[parents[i]];
//				parentTrans = this.translations[parents[i]];
//			}
//			else
//			{
//				parentOrien = new Quaternion();
//				parentTrans = new Vector3f();
//			}
//			this.orientations[i].set(parentOrien.inverse().multLocal(this.orientations[i]));
//			this.translations[i].subtractLocal(parentTrans);
//			parentOrien.inverse().multLocal(this.translations[i]);
//			if(parents[i] < 0)
//			{
//				this.orientations[i].set(MD5Importer.base.mult(this.orientations[i]));
//			}
//		}
//		
//		for(int i = 0; i < parents.length; i++)
//		{
//			if(parents[i] >= 0)
//			{
//				parentTrans = this.translations[parents[i]];
//				parentOrien = this.orientations[parents[i]];
//				Quaternion inverse = new Quaternion();
//				inverse.set(parentOrien.inverse());
//				inverse.normalize();
//				float w = -parentOrien.x*this.translations[i].x-parentOrien.y*this.translations[i].y-parentOrien.z*this.translations[i].z;
//				float x = parentOrien.w*this.translations[i].x+parentOrien.y*this.translations[i].z-parentOrien.z*this.translations[i].y;
//				float y = parentOrien.w*this.translations[i].y+parentOrien.z*this.translations[i].x-parentOrien.x*this.translations[i].z;
//				float z = parentOrien.w*this.translations[i].z+parentOrien.x*this.translations[i].y-parentOrien.y*this.translations[i].x;
//				Quaternion temp = new Quaternion(x, y, z, w);
//				Quaternion result = temp.mult(inverse);
//				this.translations[i].set(result.x, result.y, result.z);
//				this.translations[i].addLocal(parentTrans);
//				this.orientations[i].multLocal(parentOrien);
//				this.orientations[i].normalize();
//			}
//		}
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
//		if(this.baseframe && MD5Importer.getInstance().getParents()[jointIndex] < 0)
//		{
//			raw.set(MD5Importer.base.mult(raw));
//		}
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
}
