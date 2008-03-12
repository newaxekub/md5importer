package com.model.md5;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


import com.jme.scene.Controller;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.resource.anim.Frame;

/**
 * JointAnimation is the final product of MD5 animation. It is added to a
 * JointController for animating the skeletal ModelNode.
 * 
 * @author Yi Wang (Neakor)
 */
public class JointAnimation implements Serializable, Savable{
	// Serial version.
	private static final long serialVersionUID = 3646737896444759738L;
	// The name of this animation.
	private String name;
	// The joint ids of this animation.
	private String[] jointIDs;
	// The keyframes of this animation.
	private Frame[] frames;
	// The frame rate.
	private float frameRate;
	// The flag indicates the direction.
	private boolean backward;
	// The time elapsed since last change in frame.
	private float time;
	// The index of the next frame.
	private int prevFrame;
	// The index of the p frame.
	private int nextFrame;
	// The sub animations.
	private ArrayList<JointAnimation> animations;
	
	/**
	 * Default constructor of JointAnimation.
	 */
	public JointAnimation() {}
	
	/**
	 * Constructor of MD5Animation.
	 * @param name The name of this MD5Animation.
	 */
	public JointAnimation(String name) {
		this.name = name;
	}

	/**
	 * Update the Frame index based on given values.
	 * @param time The time between last update and the current one.
	 * @param repeat The Controller repeat type.
	 * @param speed The speed of the Controller.
	 */
	public void update(float time, int repeat, float speed) {
		this.time = this.time + time * speed;
		switch(repeat)
		{
			case Controller.RT_CLAMP:
				this.updateClamp();
				break;
			case Controller.RT_CYCLE:
				this.updateCycle();
				break;
			case Controller.RT_WRAP:
				this.updateWrap();
				break;
		}
		if(this.animations != null)
		{
			for(JointAnimation anim : this.animations)
			{
				anim.update(time, repeat, speed);
			}
		}
	}
	
	/**
	 * Update Frame index when the animation is set to clamp.
	 */
	private void updateClamp() {
		if(this.time >= 1.0f/this.frameRate)
		{
			this.nextFrame++;
			this.prevFrame = this.nextFrame - 1;
			if(this.nextFrame > this.frames.length - 1)
			{
				this.nextFrame = this.frames.length - 1;
				this.prevFrame = this.nextFrame;
			}
			this.time = 0.0f;
		}
	}
	
	/**
	 * Update Frame index when the animation is set to cycle.
	 */
	private void updateCycle() {
		if(this.time >= 1.0f/this.frameRate)
		{
			if(!this.backward)
			{
				this.nextFrame++;
				this.prevFrame = this.nextFrame - 1;
				if(this.nextFrame > this.frames.length - 1)
				{
					this.backward = true;
					this.prevFrame = this.frames.length - 1;
					this.nextFrame = this.prevFrame - 1;
				}
			}
			else
			{
				this.nextFrame--;
				this.prevFrame = this.nextFrame + 1;
				if(this.nextFrame < 0)
				{
					this.backward = false;
					this.prevFrame = 0;
					this.nextFrame = this.prevFrame + 1;
				}
			}
			this.time = 0.0f;
		}
	}
	
	/**
	 * Update Frame index when the animation is set to wrap.
	 */
	private void updateWrap() {
		if(this.time >= 1.0f/this.frameRate)
		{
			this.nextFrame++;
			this.prevFrame = this.nextFrame - 1;
			if(this.nextFrame > this.frames.length - 1)
			{
				this.prevFrame = 0;
				this.nextFrame = this.prevFrame + 1;
			}
			this.time = 0.0f;
		}
	}
	
	/**
	 * Add a sub MD5Animation to this animation.
	 * @param animation The MD5Animation object to be added.
	 */
	public void addAnimation(JointAnimation animation) {
		if(this.animations == null) this.animations = new ArrayList<JointAnimation>();
		this.animations.add(animation);
	}
	
	/**
	 * Set the IDs of Joint of this animation.
	 * @param joints The array of IDs of Joint.
	 */
	public void setJointIDs(String[] joints) {
		this.jointIDs = new String[joints.length];
		System.arraycopy(joints, 0, this.jointIDs, 0, this.jointIDs.length);
	}
	
	/**
	 * Set the Frame of this animation.
	 * @param frames The array of Frame.
	 */
	public void setFrames(Frame[] frames) {
		this.frames = new Frame[frames.length];
		System.arraycopy(frames, 0, this.frames, 0, this.frames.length);
	}
	
	/**
	 * Set the frame rate of this animation.
	 * @param frameRate The float frame rate.
	 */
	public void setFrameRate(float frameRate) {
		this.frameRate = frameRate;
	}
	
	/**
	 * Retrieve the previous Frame.
	 * @return The previous Frame.
	 */
	public Frame getPreviousFrame() {
		return this.frames[this.prevFrame];
	}
	
	/**
	 * Retrieve the starting time of the previous Frame.
	 * @return The starting time of the previous Frame.
	 */
	public float getPreviousTime() {
		return ((float)this.prevFrame) * (1.0f/this.frameRate);
	}

	/**
	 * Retrieve the next Frame.
	 * @return The next Frame.
	 */
	public Frame getNextFrame() {
		return this.frames[this.nextFrame];
	}
	
	/**
	 * Retrieve the starting time of the next Frame.
	 * @return The starting time of the next Frame.
	 */
	public float getNextTime() {
		return ((float)this.nextFrame) * (1.0f/this.frameRate);
	}

	/**
	 * Retrieve the IDs of Joint of this animation.
	 * @return The array of IDs of Joint.
	 */
	public String[] getJointIDs() {
		return this.jointIDs;
	}

	/**
	 * Retrieve the name of this MD5Animation.
	 * @return The name of this MD5Animation.
	 */
	public String getName() {
		return this.name;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return JointAnimation.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.name = ic.readString("Name", null);
		this.jointIDs = ic.readStringArray("JointIDs", null);
		Savable[] temp = ic.readSavableArray("Frames", null);
		this.frames = new Frame[temp.length];
		for(int i = 0; i < temp.length; i++)
		{
			this.frames[i] = (Frame)temp[i];
		}
		this.frameRate = ic.readFloat("FrameRate", 0);
		this.animations = (ArrayList<JointAnimation>)ic.readSavableArrayList("Animations", null);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.name, "Name", null);
		oc.write(this.jointIDs, "JointIDs", null);
		oc.write(this.frames, "Frames", null);
		oc.write(this.frameRate, "FrameRate", 0);
		oc.writeSavableArrayList(this.animations, "Animations", null);
	}
}
