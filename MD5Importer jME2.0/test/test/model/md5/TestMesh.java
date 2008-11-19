package test.model.md5;

import java.io.IOException;
import java.net.URL;

import com.model.md5.MD5Node;
import com.model.md5.importer.MD5Importer;
import com.model.md5.interfaces.IMD5Node;


/**
 * A simple test for loading a static model.
 * @author Yi Wang (Neakor)
 */
public class TestMesh extends Test{

	public static void main(String[] args) {
		new TestMesh().start();
	}
	
	protected IMD5Node loadModel() {
		URL md5mesh = TestMesh.class.getClassLoader().getResource("test/model/md5/data/marine.md5mesh");
		try {
			MD5Importer.getInstance().loadMesh(md5mesh, "ModelNode");
		} catch (IOException e) {
			e.printStackTrace();
		}
		IMD5Node body = MD5Importer.getInstance().getModelNode();
		MD5Importer.getInstance().cleanup();
		URL head = TestMesh.class.getClassLoader().getResource("test/model/md5/data/sarge.md5mesh");
		try {
			MD5Importer.getInstance().loadMesh(head, "Head");
		} catch (IOException e) {
			e.printStackTrace();
		}
		body.attachChild(MD5Importer.getInstance().getModelNode(), "Shoulders");
		body.flagUpdate();
		MD5Importer.getInstance().cleanup();
		return body;
	}

	@Override
	protected void setupGame() {
		MD5Node node = (MD5Node)this.rootNode.getChild("ModelNode");
		node.setLocalScale(1);
	}
}
