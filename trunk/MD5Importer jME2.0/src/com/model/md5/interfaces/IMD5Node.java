package com.model.md5.interfaces;

import com.jme.util.export.Savable;
import com.model.md5.interfaces.mesh.IJoint;
import com.model.md5.interfaces.mesh.IMesh;

/**
 * <code>IMD5Node</code> defines the interface of a completed loaded MD5 model.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 11-17-2008 22:27 EST
 * @version Modified date: 11-18-2008 14:40 EST
 */
public interface IMD5Node extends Savable {

	/**
	 * Initialize the <code>IMD5Node</code>.
	 */
	public void initialize();
	
	/**
	 * Add a controller to control this node.
	 * @param controller The <code>IMD5Controller</code> instance.
	 */
	public void addController(IMD5Controller controller);
	
	/**
	 * Attach the given MD5 node to the joint with given ID.
	 * @param node The <code>IMD5Node</code> needs to be attached.
	 * @param jointID The <code>String</code> ID of the joint.
	 */
	public void attachChild(IMD5Node node, String jointID);
	
	/**
	 * Attach the given MD5 node to the joint with given index.
	 * @param node The <code>IMD5Node</code> needs to be attached.
	 * @param jointIndex The <code>Integer</code> index of the joint.
	 */
	public void attachChild(IMD5Node node, int jointIndex);
	
	/**
	 * Attach the given MD5 node as a dependent child which shares the
	 * skeleton with this node.
	 * @param node The dependent <code>IMD5Node</code> needs to be attached.
	 */
	public void attachDependent(IMD5Node node);
	
	/**
	 * Notify the <code>IMD5Node</code> that its skeleton has been modified.
	 */
	public void flagUpdate();
	
	/**
	 * Retrieve the skeleton of this MD5 node.
	 * @return The array of <code>IJoint</code>.
	 */
	public IJoint[] getJoints();
	
	/**
	 * Retrieve the joint with given index.
	 * @param index The <code>Integer</code> index number.
	 * @return The <code>IJoint</code> instance.
	 */
	public IJoint getJoint(int index);
	
	/**
	 * Retrieve the mesh with given index.
	 * @param index The <code>Integer</code> index number.
	 * @return The <code>IMesh</code> instance.
	 */
	public IMesh getMesh(int index);
	
	/**
	 * Clone this MD5 node.
	 * @return The cloned <code>IMD5Node</code> instance.
	 */
	public IMD5Node clone();
}
