package com.model.md5.interfaces.anim;

/**
 * <code>IBaseFrame</code> defines the the interface of the base frame of
 * a skeletal animation. It maintains the joint hierarchy of the animation.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 11-17-2008 22:05 EST
 * @version Modified date: 11-17-2008 22:07 EST
 */
public interface IBaseFrame extends IFrame {
	
	/**
	 * Retrieve the parent index of the joint with given index.
	 * @return The <code>Integer</code> index of the parent.
	 */
	public int getParent(int index);
}
