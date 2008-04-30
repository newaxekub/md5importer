package test.model.md5;

import java.net.URISyntaxException;


import com.jme.app.SimpleGame;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;

public abstract class Test extends SimpleGame{

	@Override
	protected void simpleInitGame() {
		this.overrideTextureKey();
		this.rootNode.attachChild(this.loadModel());
		this.setupGame();
		MD5Importer.getInstance().cleanup();
	}
	
	protected void overrideTextureKey() {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
					new SimpleResourceLocator(TestMesh.class.getClassLoader().getResource("test/model/md5/data/texture/")));
		} catch (URISyntaxException e) {e.printStackTrace();}
	}

	abstract protected ModelNode loadModel();
	
	abstract protected void setupGame();
}
