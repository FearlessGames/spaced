package se.spaced.client.resources.zone;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.model.Prop;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.util.QueueRunner;
import se.spaced.shared.xml.XmlIOException;

public class PropXmoEntityCreator implements QueueRunner.Runner<Prop, Void> {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final ScenegraphService scenegraphService;
	private final XmoEntityFactory xmoEntityFactory;
	private final PhysicsWorld<?> physics;

	@Inject
	public PropXmoEntityCreator(
			ScenegraphService scenegraphService,
			XmoEntityFactory xmoEntityFactory,
			PhysicsWorld<?> physics) {
		this.scenegraphService = scenegraphService;
		this.xmoEntityFactory = xmoEntityFactory;
		this.physics = physics;
	}

	@Override
	public Void onRunWith(Prop prop) {
		if (prop.getXmoEntity() == null) {
			buildXmoEntity(prop);
		}

		// TODO: make these methods part of another interface
		scenegraphService.addCollisionObjects(prop.getXmoEntity().getCollisionObjects());
		scenegraphService.addNotVisibleProp(prop);

		return null;
	}

	private void buildXmoEntity(Prop prop) {
		try {
			XmoEntity xmoEntity = createXmoEntity(prop);
			prop.setXmoEntity(xmoEntity);
			physics.buildPhysicsOn(prop);
			// create physics if available


		} catch (XmlIOException e) {
			log.error("Failed to load prop", e);
		}
	}

	public XmoEntity createXmoEntity(Prop prop) throws XmlIOException {
		XmoEntity xmoEntity = xmoEntityFactory.create(prop.getXmoFile(), null);
		prop.transformProp(xmoEntity);

		return xmoEntity;
	}


}
