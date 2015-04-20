package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.area.CompositeSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.tools.spawnpattern.presenter.SpawnAreaPresenter;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JPanel;
import java.util.List;
import java.util.Map;

public class CompositeSpawnAreaPanel extends JPanel {
	public CompositeSpawnAreaPanel(
			CompositeSpawnArea spawnArea,
			Map<Class<?>, SpawnAreaPresenter.SpawnAreaPanelFactory> panelFactoryMap) {
		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(this);
		List<? extends SpawnArea> areas = spawnArea.getAreas();
		for (SpawnArea area : areas) {
			twoColumnBuilder.addRow("*          ",
					panelFactoryMap.get(area.getClass()).createPanel(area));
		}
	}
}
