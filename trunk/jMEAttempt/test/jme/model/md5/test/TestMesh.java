package jme.model.md5.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import jme.model.md5.MD5Importer;

import com.jme.animation.SkinNode;
import com.jme.app.SimpleGame;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;


public class TestMesh extends SimpleGame{

	public static void main(String[] args) {
		new TestMesh().start();
	}

	@Override
	protected void simpleInitGame() {
		this.overrideTextureKey();
		this.loadModel();
		SkinNode skinNode = MD5Importer.getInstance().getSkinNode();
		skinNode.setLocalScale(1);
		this.rootNode.attachChild(skinNode);
	}
	
	private void overrideTextureKey() {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
					new SimpleResourceLocator(TestMesh.class.getClassLoader().getResource("jme/model/md5/test/data/texture/")));
		} catch (URISyntaxException e) {e.printStackTrace();}
	}
	
	private void loadModel() {
		URL md5mesh = TestMesh.class.getClassLoader().getResource("jme/model/md5/test/data/marine.md5mesh");
		try {
			MD5Importer.getInstance().loadMesh(md5mesh, "Marine");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
