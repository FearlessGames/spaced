package se.spaced.server.tools.spawnpattern.presenter;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.model.spawn.area.CompositeSpawnArea;
import se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea;
import se.spaced.server.model.spawn.area.RandomSpaceSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.tools.spawnpattern.view.BorderBuilder;
import se.spaced.server.tools.spawnpattern.view.CompositeSpawnAreaPanel;
import se.spaced.server.tools.spawnpattern.view.ErrorView;
import se.spaced.server.tools.spawnpattern.view.PolygonSpaceSpawnAreaPanel;
import se.spaced.server.tools.spawnpattern.view.RandomSpaceSpawnAreaPanel;
import se.spaced.server.tools.spawnpattern.view.SinglePointSpawnAreaPanel;
import se.spaced.server.tools.spawnpattern.view.SpawnAreaView;
import se.spaced.shared.tools.ClipBoarder;

import javax.swing.JPanel;
import java.util.Map;

public class SpawnAreaPresenter implements SpawnAreaView.Presenter {

	private final ClipBoarder clipBoarder;
	private final SpawnAreaView view;
	private final ErrorView errorView;
	private final SpawnAreaFactory spawnAreaFactory;
	private SpawnArea currentSpawnArea;
	private SpawnPatternTemplate currentSpawnPatternTemplate;
	private final Map<Class<?>, SpawnAreaPanelFactory> panelFactoryMap;

	@Inject
	public SpawnAreaPresenter(
			final SpawnAreaView view,
			final ErrorView errorView,
			SpawnAreaFactory spawnAreaFactory,
			ClipBoarder clipBoarder, BorderBuilder borderBuilder) {

		this.view = view;
		this.errorView = errorView;
		this.spawnAreaFactory = spawnAreaFactory;
		this.clipBoarder = clipBoarder;

		view.setPresenter(this);

		panelFactoryMap = Maps.newHashMap();
		panelFactoryMap.put(RandomSpaceSpawnArea.class, new RandomSpaceSpawnAreaPanelFactory(borderBuilder));
		panelFactoryMap.put(SinglePointSpawnArea.class, new SinglePointSpawnAreaPanelFactory(borderBuilder));
		panelFactoryMap.put(PolygonSpaceSpawnArea.class, new PolygonSpaceSpawnAreaPanelFactory(borderBuilder));
		panelFactoryMap.put(CompositeSpawnArea.class, new CompositeSpawnAreaPanelFactory(panelFactoryMap, borderBuilder));

	}

	public void setCurrentTemplate(SpawnPatternTemplate spawnPatternTemplate) {
		currentSpawnPatternTemplate = spawnPatternTemplate;
		currentSpawnArea = ProxyTool.getRealObject(currentSpawnPatternTemplate.getArea());
		view.setUUID(currentSpawnArea.getPk());
		updatePanelData();
	}

	private void updatePanelData() {
		JPanel panel = panelFactoryMap.get(currentSpawnArea.getClass()).createPanel(currentSpawnArea);
		view.setSpawnAreaPanel(panel);
	}


	@Override
	public void changeAreaType() {
		Class<?>[] possibilities = {PolygonSpaceSpawnArea.class, RandomSpaceSpawnArea.class, SinglePointSpawnArea.class};
		Class<?> selectedType = view.askForAreaType(possibilities, currentSpawnArea.getClass());
		if (selectedType == null) {
			return;
		}

		String areaContent = clipBoarder.getClipBoard();
		if (areaContent == null) {
			return;
		}

		try {
			currentSpawnArea = spawnAreaFactory.createArea(selectedType, areaContent);
			currentSpawnPatternTemplate.setArea(currentSpawnArea);
			updatePanelData();
		} catch (SpawnAreaFactoryException e) {
			errorView.showErrorMessage("Failed to create area", e.getMessage());
		}


	}

	public interface SpawnAreaPanelFactory {
		JPanel createPanel(SpawnArea area);
	}

	private static class SinglePointSpawnAreaPanelFactory implements SpawnAreaPanelFactory {

		private final BorderBuilder borderBuilder;

		SinglePointSpawnAreaPanelFactory(BorderBuilder borderBuilder) {
			this.borderBuilder = borderBuilder;
		}

		@Override
		public JPanel createPanel(SpawnArea area) {
			SinglePointSpawnAreaPanel singlePointSpawnAreaPanel = new SinglePointSpawnAreaPanel((SinglePointSpawnArea) area);
			singlePointSpawnAreaPanel.setBorder(borderBuilder.getTitleBorder(area.getClass().getSimpleName()));
			return singlePointSpawnAreaPanel;
		}
	}

	private static class CompositeSpawnAreaPanelFactory implements SpawnAreaPanelFactory {
		private final Map<Class<?>, SpawnAreaPanelFactory> panelFactoryMap;
		private final BorderBuilder borderBuilder;

		CompositeSpawnAreaPanelFactory(Map<Class<?>, SpawnAreaPanelFactory> panelFactoryMap, BorderBuilder borderBuilder) {
			this.panelFactoryMap = panelFactoryMap;
			this.borderBuilder = borderBuilder;
		}

		@Override
		public JPanel createPanel(SpawnArea area) {
			CompositeSpawnAreaPanel spawnAreaPanel = new CompositeSpawnAreaPanel((CompositeSpawnArea) area,
					panelFactoryMap);
			spawnAreaPanel.setBorder(borderBuilder.getTitleBorder(area.getClass().getSimpleName()));
			return spawnAreaPanel;
		}
	}

	private static class RandomSpaceSpawnAreaPanelFactory implements SpawnAreaPanelFactory {

		private final BorderBuilder borderBuilder;

		RandomSpaceSpawnAreaPanelFactory(BorderBuilder borderBuilder) {
			this.borderBuilder = borderBuilder;
		}

		@Override
		public JPanel createPanel(SpawnArea area) {
			RandomSpaceSpawnAreaPanel spawnAreaPanel = new RandomSpaceSpawnAreaPanel((RandomSpaceSpawnArea) area);
			spawnAreaPanel.setBorder(borderBuilder.getTitleBorder(area.getClass().getSimpleName()));
			return spawnAreaPanel;
		}
	}

	private static class PolygonSpaceSpawnAreaPanelFactory implements SpawnAreaPanelFactory {
		private final BorderBuilder borderBuilder;

		PolygonSpaceSpawnAreaPanelFactory(BorderBuilder borderBuilder) {
			this.borderBuilder = borderBuilder;
		}

		@Override
		public JPanel createPanel(SpawnArea area) {
			PolygonSpaceSpawnArea polygonSpaceSpawnArea = (PolygonSpaceSpawnArea) area;
			PolygonSpaceSpawnAreaPanel spawnAreaPanel = new PolygonSpaceSpawnAreaPanel(polygonSpaceSpawnArea);
			spawnAreaPanel.setBorder(borderBuilder.getTitleBorder(area.getClass().getSimpleName()));
			return spawnAreaPanel;
		}
	}
}
