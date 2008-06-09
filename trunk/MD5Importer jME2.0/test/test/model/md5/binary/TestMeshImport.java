package test.model.md5.binary;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import test.model.md5.TestMesh;


import com.jme.app.SimpleGame;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.model.md5.ModelNode;

/**
 * Simple test to show how to load in the exported mesh.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshImport extends SimpleGame{
	private final String body = "bodymesh.jme";
	private final String head = "headmesh.jme";
	protected ModelNode bodyNode;
	protected ModelNode headNode;
	
	public static void main(String[] args) {
		new TestMeshImport().start();
	}

	@Override
	protected void simpleInitGame() {
		this.overrideTextureKey();
		URL bodyURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.body);
		URL headURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.head);
		try {
			this.bodyNode = (ModelNode)BinaryImporter.getInstance().load(bodyURL);
			this.headNode = (ModelNode)BinaryImporter.getInstance().load(headURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bodyNode.attachChild(this.headNode, "Shoulders");
		this.rootNode.attachChild(this.bodyNode);
	}
	
	private void overrideTextureKey() {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
					new SimpleResourceLocator(TestMesh.class.getClassLoader().getResource("test/model/md5/data/texture/")));
		} catch (URISyntaxException e) {e.printStackTrace();}
	}
}
