package com.model.md5.test.binary;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.util.export.binary.BinaryExporter;
import com.model.md5.ModelNode;
import com.model.md5.test.TestAnim;


/**
 * Simple test to show how to export an animated model.
 * @author Yi Wang (Neakor)
 */
public class TestAnimatedBinaryExport extends TestAnim{
	private final String filename = "animated.jme";
	private ModelNode node;
	private File file;

	public static void main(String[] args) {
		Texture.DEFAULT_STORE_TEXTURE = true;
		new TestAnimatedBinaryExport().start();
	}
	
	@Override
	protected void setupGame() {
		super.setupGame();
		this.node = (ModelNode)this.rootNode.getChild("ModelNode");
		URL url = this.getClass().getClassLoader().getResource("com/model/md5/test/data/binary/");
		String raw = url.toString().replaceAll("%20", " ");
		String path = raw.substring(raw.indexOf("/") + 1, raw.length()).replaceFirst("bin", "src");
		this.file = new File(path + this.filename);
		try {
			this.file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		KeyBindingManager.getKeyBindingManager().set("output", KeyInput.KEY_O);
	}
	
	protected void simpleUpdate() {
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("output", false))
		{
			this.export();
		}
	}
	
	private void export() {
		try {
			BinaryExporter.getInstance().save(this.node, this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
