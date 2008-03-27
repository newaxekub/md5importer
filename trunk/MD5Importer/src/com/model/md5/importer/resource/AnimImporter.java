package com.model.md5.importer.resource;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.BitSet;

import com.model.md5.JointAnimation;
import com.model.md5.exception.InvalidVersionException;
import com.model.md5.importer.MD5Importer;
import com.model.md5.resource.anim.Frame;

/**
 * AnimImporter imports MD5Anim resources. This class is used by MD5Importer
 * internally only.
 *
 * @author Yi Wang (Neakor)
 */
public class AnimImporter {
	// The stream tokenizer object.
	private StreamTokenizer reader;
	// The frame rate of the animatin.
	private float frameRate;
	// The animation bone name hierarchy.
	private String[] idHierarchy;
	// The animation bone parent hierarchy.
	private int[] parentHierarchy;
	// The bit set frame flags.
	private BitSet frameflags;
	// The base frame of the animation.
	private Frame baseframe;
	// The frame resource library.
	private Frame[] frames;
	// The skeleton animation object.
	private JointAnimation animation;
	
	/**
	 * Constructor of MeshImporter.
	 * @param reader The StreamTokenizer object setup for the loading process.
	 */
	public AnimImporter(StreamTokenizer reader) {
		this.reader = reader;
	}
	
	/**
	 * Load the md5anim file and construct a JointAnimation.
	 * @param name The name of the loaded animation.
	 * @return The loaded JointAnimation object.
	 * @throws IOException
	 */
	public JointAnimation loadAnim(String name) throws IOException {
		this.animation = new JointAnimation(name);
		this.processAnim();
		this.constructAnimation();
		return this.animation;
	}
	
	/**
	 * Process the information in md5anim file.
	 * @throws IOException
	 */
	private void processAnim() throws IOException {
		String sval = null;
		while(this.reader.nextToken() != StreamTokenizer.TT_EOF)
		{
			sval = this.reader.sval;
			if(sval != null)
			{
				if(sval.equals("MD5Version"))
				{
					this.reader.nextToken();
					if(this.reader.nval != MD5Importer.version) throw new InvalidVersionException((int)this.reader.nval);
				}
				else if(sval.equals("numFrames"))
				{
					this.reader.nextToken();
					this.frames = new Frame[(int)this.reader.nval];
				}
				else if(sval.equals("numJoints"))
				{
					this.reader.nextToken();
					int numJoints = (int)this.reader.nval;
					this.baseframe = new Frame(true, numJoints);
					for(int i = 0; i < this.frames.length; i++)
					{
						this.frames[i] = new Frame(false, numJoints);
					}
					this.idHierarchy = new String[numJoints];
					this.parentHierarchy = new int[numJoints];
				}
				else if(sval.equals("frameRate"))
				{
					this.reader.nextToken();
					this.frameRate = (int)this.reader.nval;
				}
				else if(sval.equals("hierarchy"))
				{
					this.reader.nextToken();
					this.processHierarchy();
				}
				else if(sval.equals("baseframe"))
				{
					this.reader.nextToken();
					this.processBaseframe();
				}
				else if(sval.equals("frame"))
				{
					this.reader.nextToken();
					this.processFrame((int)this.reader.nval);
				}
			}
		}
	}
	
	/**
	 * Process the hierarchy section to obtain the BitSet flags.
	 * @throws IOException
	 */
	private void processHierarchy() throws IOException {
		this.frameflags = new BitSet();
		int pointer = -1;
		int joint = -1;
		int flag = -1;
		while(this.reader.nextToken() != '}')
		{
			pointer++;
			switch(this.reader.ttype)
			{
				case '"':
					this.idHierarchy[joint] = this.reader.sval;
					break;
				case StreamTokenizer.TT_NUMBER:
					switch(pointer)
					{
						case 2:
							this.parentHierarchy[joint] = (int)this.reader.nval;
							break;
						case 3:
							flag = (int)this.reader.nval;
							for(int i = 0; i < 6; i++)
							{
								this.frameflags.set(joint * 6 + i, (flag & (1 << i)) != 0);
							}
							break;
					}
					break;
				case StreamTokenizer.TT_EOL:
					pointer = 0;
					joint++;
					break;
			}
		}
		this.baseframe.setParents(this.parentHierarchy);
		for(int i = 0; i < this.frames.length; i++)
		{
			this.frames[i].setParents(this.parentHierarchy);
		}
	}
	
	/**
	 * Process information to read in the base frame.
	 * @throws IOException 
	 */
	private void processBaseframe() throws IOException {
		int pointer = -1;
		int jointIndex = -1;
		while(this.reader.nextToken() != '}')
		{
			switch(this.reader.ttype)
			{
				case '(':
					while(this.reader.nextToken() != ')')
					{
						this.baseframe.setTransform(jointIndex, pointer, (float)this.reader.nval);
						pointer++;
					}
					break;
				case StreamTokenizer.TT_EOL:
					pointer = 0;
					jointIndex++;
					break;
				default:
					break;
			}
		}
		for(int i = 0 ; i < this.parentHierarchy.length; i++)
		{
			if(this.baseframe.getParent(i) < 0)
			{
				this.baseframe.getOrientation(i).set(MD5Importer.base.mult(this.baseframe.getOrientation(i)));
			}
		}
	}
	
	/**
	 * Process information to read in a frame.
	 * @param index The index number of the frame.
	 * @throws IOException 
	 */
	private void processFrame(int index) throws IOException {
		float[] values = new float[6];
		for(int i = 0; i < this.parentHierarchy.length; i++)
		{
			for(int j = 0; j < values.length; j++)
			{
				if(this.frameflags.get(i * 6 + j))
				{
					while(this.reader.nextToken() != StreamTokenizer.TT_NUMBER);
					values[j] = (float)this.reader.nval;
				}
				else
				{
					values[j] = this.baseframe.getTransformValue(i, j);
				}
			}
			if(this.parentHierarchy[i] < 0)
			{
				this.frames[index].setTransform(i, 0, values[2]);
				this.frames[index].setTransform(i, 1, values[1]);
				this.frames[index].setTransform(i, 2, values[0]);
				this.frames[index].setTransform(i, 3, values[5]);
				this.frames[index].setTransform(i, 4, values[4]);
				this.frames[index].setTransform(i, 5, values[3]);
			}
			else
			{
				for(int t = 0; t < values.length; t++)
				{
					this.frames[index].setTransform(i, t, values[t]);
				}
			}
		}
	}
	
	/**
	 * Construct JointAnimation based on information read in.
	 */
	private void constructAnimation() {
		this.animation.setJointIDs(this.idHierarchy);
		this.animation.setFrames(this.frames);
		this.animation.setFrameRate(this.frameRate);
		this.idHierarchy = null;
		this.parentHierarchy = null;
		this.frameflags = null;
		this.baseframe = null;
		this.frames = null;
	}
}
