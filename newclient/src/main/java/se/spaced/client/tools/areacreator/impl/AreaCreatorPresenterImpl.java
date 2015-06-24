package se.spaced.client.tools.areacreator.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.lifetime.LifetimeListener;
import se.fearless.common.lifetime.LifetimeManager;
import se.spaced.client.model.Prop;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.tools.areacreator.*;
import se.spaced.client.view.ActivePropProvider;
import se.spaced.client.view.PropSelectionListener;
import se.spaced.shared.tools.ClipBoarder;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.*;
import se.spaced.shared.world.walkmesh.LocalSpaceConverter;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class AreaCreatorPresenterImpl implements AreaCreatorView.Presenter, LifetimeListener, AreaCreatorPresenter {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final AreaCreatorView view;
	private final UserCharacter userCharacter;
	private final ClipBoarder clipBoarder;
	private final AreaDisplayHandler areaDisplayHandler;
	private final XStream xStream;
	private final ActivePropProvider activePropProvider;
	private Area currentArea;
	private boolean displayInWorld = true;
	private final Map<Area, AreaCreatorView.AreaTypeSelector> areaTypeSelectorMap = new HashMap<Area, AreaCreatorView.AreaTypeSelector>();
	private final Map<Class<? extends Geometry>, Area> geometryAreaMap = new HashMap<Class<? extends Geometry>, Area>();

	@Inject
	public AreaCreatorPresenterImpl(
			final AreaCreatorView view,
			UserCharacter userCharacter,
			ClipBoarder clipBoarder,
			AreaDisplayHandler areaDisplayHandler,
			LifetimeManager lifetimeManager,
			final XmlAreaFormatter xmlAreaFormatter,
			XStream xStream,
			ActivePropProvider activePropProvider,
			ListenerDispatcher<PropSelectionListener> propSelectionListener) {
		this.view = view;
		this.userCharacter = userCharacter;
		this.clipBoarder = clipBoarder;
		this.areaDisplayHandler = areaDisplayHandler;
		this.xStream = xStream;
		this.activePropProvider = activePropProvider;
		view.setPresenter(this);
		lifetimeManager.addListener(this);

		registerSupportedArea("Single", SinglePoint.class, new SinglePointArea(xmlAreaFormatter));
		registerSupportedArea("Multi", MultiplePoints.class, new MultiPointArea(xmlAreaFormatter));
		registerSupportedArea("Path", Path.class, new PathArea(xmlAreaFormatter));
		registerSupportedArea("Polygon", Polygon.class, new PolygonArea(xmlAreaFormatter));

		propSelectionListener.addListener(new PropSelectionListener() {
			@Override
			public void activePropChanged(Prop prop, Prop oldProp) {
				view.setActiveXmo(prop.getXmoFile());
			}
		});
	}

	private void registerSupportedArea(String name, Class<? extends Geometry> geometryClass, final Area area) {
		AreaCreatorView.AreaTypeSelector selector = view.addAreaType(name,
				new AreaCreatorView.SelectionCallback() {
					@Override
					public void onSelection() {
						changeArea(area);
					}
				});

		areaTypeSelectorMap.put(area, selector);
		geometryAreaMap.put(geometryClass, area);

	}

	@Override
	public void addPointToCurrentGeometry(AreaPoint point) {
		currentArea.addAreaPoint(point);
		refreshView();
	}


	@Override
	public void addAreaPointHere() {
		AreaPoint areaPoint = createAreaPointAtUserCharacter();
		addPointToCurrentGeometry(areaPoint);
	}

	private AreaPoint createAreaPointAtUserCharacter() {

		SpacedVector3 position = userCharacter.getPosition();
		SpacedRotation rotation = userCharacter.getRotation();
		return new AreaPoint(position, rotation);
	}

	@Override
	public void insertAreaPointHere() {
		AreaPoint areaPoint = createAreaPointAtUserCharacter();
		insertAreaPoint(areaPoint);
	}

	private void refreshView() {
		view.setListItems(currentArea.getAreaPoints());
		if (displayInWorld) {
			areaDisplayHandler.areaModified(currentArea);
		}
	}

	@Override
	public void copyValuesToClipBoard() {
		LocalSpaceConverter localSpaceConverter = getLocalSpaceConverter();
		clipBoarder.putToClipBoard(currentArea.asListValueString(localSpaceConverter));
	}

	@Override
	public LocalSpaceConverter getLocalSpaceConverter() {
		LocalSpaceConverter localSpaceConverter = LocalSpaceConverter.NULL_CONVERTER;
		if (view.useLocalSpace() && activePropProvider.hasActiveProp()) {
			Prop activeProp = activePropProvider.getActiveProp();
			localSpaceConverter = new LocalSpaceConverter(activeProp.getLocation(),
					activeProp.getRotation());

		}
		return localSpaceConverter;
	}

	@Override
	public void copyGeometryToClipBoard() {
		LocalSpaceConverter localSpaceConverter = getLocalSpaceConverter();
		clipBoarder.putToClipBoard(currentArea.asGeometryString(localSpaceConverter));
	}

	@Override
	public void showGui() {
		view.display();
	}

	@Override
	public void toggleShowInWorld() {
		if (currentArea == null) {
			return;
		}

		displayInWorld = !displayInWorld;
		if (displayInWorld) {
			areaDisplayHandler.areaModified(currentArea);
		} else {
			areaDisplayHandler.hide();
		}

	}

	@Override
	public void deletePoint() {
		if (currentArea == null) {
			return;
		}

		currentArea.remove(view.getCurrentSelectedAreaPoint());
		refreshView();
	}

	private void changeArea(Area area) {
		areaDisplayHandler.hide();
		if (currentArea != null) {
			currentArea.clear();
		}
		view.clearListItems();
		currentArea = area;

	}

	@Override
	public void clearPoints() {
		if (currentArea == null) {
			return;
		}

		view.clearListItems();
		currentArea.clear();
		if (displayInWorld) {
			areaDisplayHandler.areaModified(currentArea);
		}
	}

	@Override
	public void selectedPoint() {
		AreaPoint currentPoint = view.getCurrentSelectedAreaPoint();
		areaDisplayHandler.removeIndicator();
		if (displayInWorld && currentPoint != null) {
			areaDisplayHandler.showIndicator(currentPoint);
		}
	}

	@Override
	public void pasteGeometry() {
		String clipBoardData = clipBoarder.getClipBoard();
		try {
			Geometry geometry = (Geometry) xStream.fromXML(clipBoardData);
			showGeometry(geometry);

		} catch (ClassCastException e) {
			log.error("Unable to cast data in clipboard to geometry: " + clipBoardData);
		}
	}


	@Override
	public void showGeometry(Geometry geometry) {
		Area area = geometryAreaMap.get(geometry.getClass());
		areaTypeSelectorMap.get(area).select();
		changeArea(area);
		LocalSpaceConverter localSpaceConverter = getLocalSpaceConverter();
		area.addGeometry(geometry, localSpaceConverter);
		refreshView();
		areaDisplayHandler.areaModified(currentArea);
	}

	@Override
	public void pasteAreaPointFromClipBoard() {
		String clipBoardData = clipBoarder.getClipBoard();
		try {
			AreaPoint areaPoint = (AreaPoint) xStream.fromXML(clipBoardData);
			insertAreaPoint(areaPoint);
		} catch (ClassCastException e) {
			log.error("Unable to cast data in clipboard to geometry: " + clipBoardData);
		}
	}

	@Override
	public void cutAreaPointToClipBoard() {
		copyAreaPointToClipBoard();
		deletePoint();
	}

	@Override
	public void copyAreaPointToClipBoard() {
		AreaPoint areaPoint = view.getCurrentSelectedAreaPoint();
		if (areaPoint != null) {
			clipBoarder.putToClipBoard(xStream.toXML(areaPoint));
		}
	}


	private void insertAreaPoint(AreaPoint areaPoint) {
		AreaPoint currentSelectedAreaPoint = view.getCurrentSelectedAreaPoint();
		if (currentSelectedAreaPoint == null) {
			addPointToCurrentGeometry(areaPoint);
		} else {
			currentArea.insertAreaPointAfter(currentSelectedAreaPoint, areaPoint);
			refreshView();
		}
	}


	@Override
	public void onStart() {

	}

	@Override
	public void onShutdown() {
		view.close();
	}
}
