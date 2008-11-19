package com.model.md5.resource.anim;

import java.io.IOException;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.model.md5.interfaces.IBaseFrame;

/**
 * <code>BaseFrame</code> defines the concrete implementation of a base frame.
 *<p>
 * <code>BaseFrame</code> should not be cloned directly. The cloning process of
 * a <code>BaseFrame</code> should be initiated by the cloning process of the
 * parent <code>IMD5Animation</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Creation date: 11-17-2008 22:16 EST
 * @version Modified date: 11-18-2008 23:22 EST
 */
public class BaseFrame extends Frame implements IBaseFrame {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -8995933212142378434L;
	/**
	 * The <code>Integer</code> array of joint hierarchy.
	 */
	private int[] hierarchy;
	
	/**
	 * Constructor of <code>BaseFrame</code>.
	 */
	public BaseFrame() {
		super();
	}

	/**
	 * Constructor of <code>BaseFrame</code>.
	 * @param numJoints The <code>Integer</code> number of joints.
	 * @param hierarchy The <code>Integer</code> array hierarchy.
	 */
	public BaseFrame(int numJoints, int[] hierarchy) {
		super(numJoints);
		this.hierarchy = hierarchy;
	}

	@Override
	public int getParent(int index) {
		return this.hierarchy[index];
	}
	
	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule output = ex.getCapsule(this);
		output.write(this.hierarchy, "Hierarchy", null);
	}
	
	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);
		InputCapsule input = im.getCapsule(this);
		this.hierarchy = input.readIntArray("Hierarchy", null);
	}
}
