package com.md5.viewer.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.md5.viewer.player.AnimationPlayer;

public class AnimationSelector {
	
	private final List<URL> urls;
	
	private boolean manual;
	
	public AnimationSelector() {
		this.urls = new ArrayList<URL>();
	}

	public void initialize() {
		
	}
	
	public void display() {
		
	}
	
	public void startPlayer() {
		AnimationPlayer player = new AnimationPlayer(this.urls, manual);
		player.start();
	}
}
