package se.spaced.client.environment.components;

import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Node;

public interface Sky {
	void update(Camera cam);

	void init(Node node);

	Node getSkyNode();
}
