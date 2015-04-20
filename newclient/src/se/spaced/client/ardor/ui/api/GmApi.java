package se.spaced.client.ardor.ui.api;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.ardortech.render.DebugRender;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.KahluaTable;
import se.spaced.client.ardor.effect.EffectLoader;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.resources.zone.NavigationMeshDebugShape;
import se.spaced.client.resources.zone.RootZoneService;
import se.spaced.client.resources.zone.ZoneDebugShapeService;
import se.spaced.client.tools.Interpreter;
import se.spaced.client.tools.LuaTableView;
import se.spaced.client.tools.areacreator.AreaCreatorPresenter;
import se.spaced.client.tools.spd.SpdPresenter;
import se.spaced.client.tools.walkmesh.WalkmeshVisualiser;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.resources.XmoMaterialManager;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.PointSequence;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.terrain.DataPoint;
import se.spaced.shared.world.walkmesh.LocalSpaceConverter;
import se.spaced.shared.world.walkmesh.Walkmesh;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

@Singleton
public class GmApi {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final UserCharacter userCharacter;
	private final EffectLoader effectLoader;
	private final XmoMaterialManager materialManager;
	private final SpdPresenter spdPresenter;
	private final ZoneDebugShapeService zoneDebugShapeService;
	private final RootZoneService rootZoneService;
	private final ServerConnection serverConnection;
	private final Interpreter interpreter;
	private final DebugRender debugRender;
	private final AreaCreatorPresenter areaCreatorPresenter;
	private final NavigationMeshDebugShape navigationMeshDebugShape;
	//	private final HeightMap heightMap;
	private final WalkmeshVisualiser walkmeshVisualiser;
	private final XStreamIO xStreamIO;

	@Inject
	public GmApi(
			UserCharacter userCharacter, EffectLoader effectLoader, XmoMaterialManager materialManager,
			SpdPresenter spdPresenter,
			ZoneDebugShapeService zoneDebugShapeService,
			RootZoneService rootZoneService, ServerConnection serverConnection, Interpreter interpreter,
			DebugRender debugRender, AreaCreatorPresenter areaCreatorPresenter,
			NavigationMeshDebugShape navigationMeshDebugShape,
			WalkmeshVisualiser walkmeshVisualiser,
			XStreamIO xStreamIO) {
		this.userCharacter = userCharacter;
		this.effectLoader = effectLoader;
		this.materialManager = materialManager;
		this.spdPresenter = spdPresenter;
		this.zoneDebugShapeService = zoneDebugShapeService;
		this.rootZoneService = rootZoneService;
		this.serverConnection = serverConnection;
		this.interpreter = interpreter;
		this.debugRender = debugRender;
		this.areaCreatorPresenter = areaCreatorPresenter;
		this.navigationMeshDebugShape = navigationMeshDebugShape;
//		this.heightMap = null;
		this.walkmeshVisualiser = walkmeshVisualiser;
		this.xStreamIO = xStreamIO;
	}

	@LuaMethod(global = true, name = "GM_Teleport")
	public void teleport(double x, double y, double z) {
		if (isGm()) {
			userCharacter.getUserControlledEntity().setPositionalData(
					new PositionalData(new SpacedVector3(x, y, z), userCharacter.getUserControlledEntity().getRotation()));
		}
	}

	@LuaMethod(global = true, name = "GM_Visit")
	public void visit(String name) {
		serverConnection.getReceiver().gamemaster().visit(name);
	}

	@LuaMethod(name = "InvalidateMaterialCache", global = true)
	public void invalidateMaterialCache() {
		materialManager.invalidateEntireCache();
	}

	@LuaMethod(name = "ReloadZone", global = true)
	public void reloadZone() {
		rootZoneService.reload(userCharacter.getPosition());
	}

	@LuaMethod(global = true, name = "ReloadFx")
	public void reloadFx() {
		effectLoader.reset();
	}

	@LuaMethod(global = true, name = "ToggleBounds")
	public void toggleBounds() {
		debugRender.toggleBounds();
	}

	@LuaMethod(global = true, name = "ToggleNormals")
	public void toggleNormals() {
		debugRender.toggleNormals();
	}

	@LuaMethod(global = true, name = "ShowZoneIndexShapes")
	public void showZoneIndexShapes(boolean show) {
		zoneDebugShapeService.setState(show);
	}

	@LuaMethod(global = true, name = "ShowNavigationMesh")
	public void showNavigationMesh(boolean show) {
		navigationMeshDebugShape.setState(show);
	}

	@LuaMethod(global = true, name = "point")
	public Vector3 point(double x, double y, double z) {
		return new Vector3(x, y, z);
	}

	@LuaMethod(global = true, name = "quaternion")
	public Quaternion quaternion(double x, double y, double z, double w) {
		return new Quaternion(x, y, z, w);
	}

	@LuaMethod(global = true, name = "GM_ForceException")
	public void forceExceptionInServer(boolean inActionLoop) {
		serverConnection.getReceiver().gamemaster().forceExceptionServerSide(inActionLoop);
	}

	@LuaMethod(global = true, name = "GM_ShowAddPropDialog")
	public void showAddPropDialog() {
		spdPresenter.show();
	}

	@LuaMethod(global = true, name = "GM_ShowCreateAreaDialogue")
	public void showCreateAreaDialog() {
		areaCreatorPresenter.showGui();
	}

	@LuaMethod(global = true, name = "Interpreter")
	public void interactiveShell() {
		interpreter.showInterpreter();
	}

	@LuaMethod(global = true, name = "Browse")
	public void tableBrowser(KahluaTable table) {
		JFrame frame = new JFrame(table.toString());
		frame.getContentPane().add(new LuaTableView(table));
		frame.setSize(400, 1000);
		frame.setVisible(true);
	}

	@LuaMethod(global = true, name = "GM_GiveItem")
	public void giveItem(String playerName, String itemTemplateName, int quantity) {
		serverConnection.getReceiver().gamemaster().giveItem(playerName, itemTemplateName, quantity);
	}


	@LuaMethod(global = true, name = "GM_GrantSpell")
	public void grantSpell(String playerName, String spellName) {
		serverConnection.getReceiver().gamemaster().grantSpell(playerName, spellName);
	}

	@LuaMethod(global = true, name = "GM_GiveMoney")
	public void giveMoney(String playerName, String currency, long amount) {
		serverConnection.getReceiver().gamemaster().giveMoney(playerName, currency, amount);
	}

	@LuaMethod(global = true, name = "GM_ReloadMob")
	public void reloadMob(ClientEntity entity) {
		if (entity == null) {
			return;
		}
		serverConnection.getReceiver().gamemaster().reloadMob(entity);
	}

	@LuaMethod(global = true, name = "GM_SpawnMob")
	public void spawnMob(String mobTemplate, String brainTemplate) {
		serverConnection.getReceiver().gamemaster().spawnMob(mobTemplate, brainTemplate);
	}

	@LuaMethod(global = true, name = "GM_ReloadServerContent")
	public void reloadServerContent() {
		serverConnection.getReceiver().gamemaster().reloadServerContent();
	}

	@LuaMethod(global = true, name = "GM_AiInfo")
	public void requestAiInfo(ClientEntity mob) {
		if (mob == null) {
			return;
		}
		serverConnection.getReceiver().gamemaster().requestAiInfo(mob);
	}

	@LuaMethod(global = true, name = "GM_Summon")
	public void summonEntity(String entityName) {
		serverConnection.getReceiver().gamemaster().summonEntity(entityName);

	}

	@LuaMethod(global = true, name = "IsGm")
	public boolean isGm() {
		return userCharacter.isGm();
	}

	@LuaMethod(global = true, name = "GM_AdjustTerrain")
	public void adjustTerrain(double amount, double x, double y, double z) {
		log.warn("The heightmap system is currently not in use");
//		heightMap.update(x, z, amount);
	}

	@LuaMethod(global = true, name = "GM_GetNearbyHeightmapPoints")
	public Iterable<DataPoint> getNearbyPoints(double radius, double x, double y, double z) {
		log.warn("The heightmap system is currently not in use");
//		return heightMap.getDataPointsNearby(x, z, radius);
		return Collections.emptyList();
	}

	@LuaMethod(global = true, name = "GM_AdjustHeightmapDataPoint")
	public void adjustDataPoint(double amount, int xIndex, int zIndex) {
		log.warn("The heightmap system is currently not in use");
		//heightMap.updateSinglePoint(xIndex, zIndex, amount);
	}

	@LuaMethod(global = true, name = "GM_WriteHeightToImage")
	public void writeHeightData(String fileName) throws IOException {
		log.warn("The heightmap system is currently not in use");
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(fileName);
//			HeightMapExporter exporter = new PngExporter();
//			exporter.export(heightMap, fos);
//		} finally {
//			fos.close();
//		}
	}

	@LuaMethod(global = true, name = "GM_WriteHeightToRaw")
	public void writeHeightToRaw(String fileName) throws IOException {
		log.warn("The heightmap system is currently not in use");
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(fileName);
//			HeightMapExporter exporter = new RawDataHeightMapExporter();
//			exporter.export(heightMap, fos);
//		} finally {
//			if (fos != null) {
//				fos.close();
//			}
//		}
//
	}

	@LuaMethod(global = true, name = "GM_Walkmesh")
	public void visualizeWalkmesh(String filename) {
		try {
			Walkmesh walkmesh = xStreamIO.load(Walkmesh.class, filename);
			LocalSpaceConverter localSpaceConverter = areaCreatorPresenter.getLocalSpaceConverter();
			walkmeshVisualiser.visualizeWalkmesh(walkmesh, localSpaceConverter.getTranslation(), localSpaceConverter.getRotation());
		} catch (XmlIOException e) {
			e.printStackTrace();
		}
	}

	@LuaMethod(global = true, name = "GM_BrowseForResourcePath")
	public String getFileName() {
		File currentDirectory = new File(".");
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		int choice = fileChooser.showOpenDialog(null);
		if (choice == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			String path = selectedFile.getAbsolutePath();
			Splitter splitter = Splitter.on("resources").trimResults();
			Iterable<String> split = splitter.split(path);
			if (Iterables.size(split) != 2) {
				log.error("{} is not a valid path", path);
			}
			return Iterables.get(split, 1);
		}
		return "";
	}

	@LuaMethod(global = true, name = "GM_GetPolygon")
	public Polygon getPolygon(double x, double y, double z) {
		return walkmeshVisualiser.getPolygon(new SpacedVector3(x, y, z));
	}

	@LuaMethod(global = true, name = "GM_LoadPolygon")
	public void loadPolygonInScad(PointSequence pointSequence) {
		areaCreatorPresenter.clearPoints();
		areaCreatorPresenter.showGui();

		Polygon polygon = new Polygon();
		ImmutableList<AreaPoint> areaPoints = pointSequence.getAreaPoints();
		for (int i = 0, lastIndex = areaPoints.size() - 1; i < lastIndex; i++) {
			AreaPoint areaPoint = areaPoints.get(i);
			polygon.add(areaPoint.getPoint());

		}

		areaCreatorPresenter.showGeometry(polygon);
	}
}
