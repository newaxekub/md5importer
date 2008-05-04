package test.model.md5;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import jme.model.md5.MD5Importer;

import com.jme.animation.SkinNode;
import com.jme.app.SimpleGame;
import com.jme.scene.Controller;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;


public class TestAnim extends SimpleGame{

	public static void main(String[] args) {
		new TestAnim().start();	
	}

	@Override
	protected void simpleInitGame() {
		this.overrideTextureKey();
		URL mesh = TestAnim.class.getClassLoader().getResource("test/model/md5/data/fish.md5mesh");
		URL anim = TestAnim.class.getClassLoader().getResource("test/model/md5/data/fish.md5anim");
		try {
			MD5Importer.getInstance().load(mesh, "Fish", anim, "Swim", Controller.RT_WRAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SkinNode node = MD5Importer.getInstance().getSkinNode();
		node.setLocalScale(10);
		this.rootNode.attachChild(node.getSkeleton());
		this.rootNode.attachChild(node);
	}
	
	private void overrideTextureKey() {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
					new SimpleResourceLocator(TestMesh.class.getClassLoader().getResource("test/model/md5/data/texture/")));
		} catch (URISyntaxException e) {e.printStackTrace();}
	}
}
