package test.model.md5.binary;

import java.io.IOException;
import java.net.URL;


import com.jme.app.SimpleGame;
import com.jme.util.export.binary.BinaryImporter;
import com.model.md5.ModelNode;

/**
 * Simple test to show how to load in the exported binary.
 * @author Yi Wang (Neakor)
 */
public class TestBinaryImport extends SimpleGame{
	private final String file = "animated.jme";
	private ModelNode node;
	
	public static void main(String[] args) {
		new TestBinaryImport().start();
	}

	@Override
	protected void simpleInitGame() {
		URL url = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.file);
		try {
			this.node = (ModelNode)BinaryImporter.getInstance().load(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.rootNode.attachChild(this.node);
	}
}
