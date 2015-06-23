package se.spaced.client.tools.spd;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.io.FileStreamLocator;
import se.fearlessgames.common.lifetime.LifetimeListener;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.spaced.client.ardor.entity.InteractionColors;
import se.spaced.client.model.Prop;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.resources.zone.ScenegraphService;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.resources.zone.ZoneXmlWriter;
import se.spaced.client.view.ActivePropProvider;
import se.spaced.client.view.PropSelectionListener;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.scheduler.JobManager;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.xml.XmlIOException;

import javax.swing.JComponent;
import java.io.File;
import java.util.Iterator;

@Singleton
public class SpdPresenter implements PropSelectionListener, SpdView.Presenter, LifetimeListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final SpdView view;
	private final InteractionColors interactionColors;
	private final ActivePropProvider activePropProvider;
	private final UserCharacter userCharacter;
	private final ZoneActivationService zoneActivationService;
	private final ScenegraphService scenegraphService;
	private final ZoneXmlWriter zoneXmlWriter;
	private final XmoEntityFactory xmoEntityFactory;
	private final PropPreviwer propPreviwer;
	private boolean active;


	@Inject
	public SpdPresenter(
			final SpdView view, final UserCharacter userCharacter,
			ListenerDispatcher<PropSelectionListener> dispatcher,
			LifetimeManager lifetimeManager,
			final ZoneActivationService zoneActivationService,
			final ScenegraphService scenegraphService,
			final ZoneXmlWriter zoneXmlWriter,
			final XmoEntityFactory xmoEntityFactory,
			final InteractionColors interactionColors, JobManager jobManager, ActivePropProvider activePropProvider) {
		this.userCharacter = userCharacter;
		this.zoneActivationService = zoneActivationService;
		this.scenegraphService = scenegraphService;
		this.zoneXmlWriter = zoneXmlWriter;
		this.xmoEntityFactory = xmoEntityFactory;

		this.view = view;
		this.interactionColors = interactionColors;
		this.activePropProvider = activePropProvider;

		lifetimeManager.addListener(this);
		view.setPresenter(this);
		dispatcher.addListener(this);

		String pathname = System.getProperty("user.dir") + "/../shared/resources";
		FileStreamLocator locator = new FileStreamLocator(new File(pathname));
		Iterator<String> iterator = locator.listKeys();
		while (iterator.hasNext()) {
			String s = iterator.next();
			if (s.toLowerCase().endsWith(".xmo")) {
				view.addListItem(s);
			}
		}

		//propPreviwer = new LwjglHeadlessPropPreviewer(jobManager);
		propPreviwer = new DummyPropPreviewer();
		JComponent previewComponent = propPreviwer.getPreviewComponent();
		view.setPreviewComponent(previewComponent);

	}


	@Override
	public void placePropHere() {
		String xmoFileName = view.getSelectedXmoFileName();
		SpacedVector3 point = userCharacter.getPositionalData().getPosition();
		SpacedRotation rotation = userCharacter.getRotation();
		double d = view.getScaleValue();
		final Prop prop = new Prop(xmoFileName, point, new SpacedVector3(d, d, d), rotation);
		prop.setCreatedCallback(new Prop.CreatedCallback() {
			@Override
			public void onCreated() {
				log.info("Prop created: {}", prop);
				activePropProvider.setActiveProp(prop);
			}
		});

		Zone zone = zoneActivationService.getMostActiveZone(point);
		zone.addProp(prop);
		scenegraphService.addProp(prop, zone);

	}

	@Override
	public void removeProp() {

		Prop prop = activePropProvider.getActiveProp();
		log.debug("removing prop");
		if (prop.getZone() != null) {
			prop.getZone().getProps().remove(prop);
			scenegraphService.removeProps(Lists.newArrayList(prop));
			log.debug("Removing prop: {}", prop);
		}

	}

	@Override
	public void saveAllZones() {
		Zone rootZone = zoneActivationService.getRootZone();
		zoneXmlWriter.saveZone(rootZone.getFilename(), rootZone);
	}

	@Override
	public void changedXmoFile() {
		String fileName = view.getSelectedXmoFileName();
		try {
			XmoEntity xmoEntity = xmoEntityFactory.create(fileName, "PreviewEntity");
			propPreviwer.preview(xmoEntity);

		} catch (XmlIOException e) {
			log.error("Failed to load xmo " + fileName, e);
		}
	}

	@Override
	public void gainedFocus() {
		propPreviwer.gainedFocus();

	}

	@Override
	public void lostFocus() {
		propPreviwer.lostFocus();
	}

	public void show() {
		view.display();
	}

	@Override
	public void viewHiding() {
		active = false;
		deselectProp(activePropProvider.getActiveProp());
	}

	@Override
	public void viewShowing() {
		active = true;
	}

	@Override
	public void activePropChanged(Prop newProp, Prop oldProp) {
		if (active) {
			selectProp(newProp, oldProp);
		}
	}

	private void selectProp(Prop prop, Prop oldProp) {
		deselectProp(oldProp);
		ColorRGBA color = new ColorRGBA(MaterialState.DEFAULT_EMISSIVE);
		color.addLocal(interactionColors.getTargetedPropColor());
		prop.getXmoEntity().getModel().acceptVisitor(new ColorChangeVisitor(color), false);
		view.updateActiveProp();
	}

	private void deselectProp(Prop oldProp) {
		if (oldProp != null && oldProp.getXmoEntity() != null) {
			ColorRGBA color = new ColorRGBA(MaterialState.DEFAULT_EMISSIVE);
			oldProp.getXmoEntity().getModel().acceptVisitor(new ColorChangeVisitor(color), false);
		}
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onShutdown() {
		propPreviwer.shutdown();
		view.close();
	}
}
