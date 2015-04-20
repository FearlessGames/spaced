package se.spaced.client.core.states;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.resources.zone.ScenegraphService;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.view.cursor.CursorView;

@Singleton
public class LoadingState implements GameState {
	private final SpacedGui spacedGui;
	private final CursorView cursorView;
	private final ZoneActivationService zoneActivationService;
	private final ScenegraphService scenegraphService;

	private SpacedVector3 position;

	@Inject
	public LoadingState(
			SpacedGui spacedGui,
			CursorView cursorView, ZoneActivationService zoneActivationService, ScenegraphService scenegraphService) {
		this.spacedGui = spacedGui;
		this.cursorView = cursorView;
		this.zoneActivationService = zoneActivationService;
		this.scenegraphService = scenegraphService;
	}

	@Override
	public void exit() {
		spacedGui.teardown();
	}

	@Override
	public void start() {
		spacedGui.start(new String[] {"ui/loading"});
		cursorView.newHover(null);
	}

	public void setPosition(SpacedVector3 position) {
		this.position = position;
	}

	@Override
	public void update(GameStateContext context, double timePerFrame) {
		SpacedVector3 localPos = position;
		if (localPos != null) {
			zoneActivationService.update(localPos, 2500.0);
			scenegraphService.update(localPos, 1000.0);
		}
		spacedGui.onUpdate(timePerFrame);
	}

	@Override
	public void updateFixed(GameStateContext context, long millisPerFrame) {
	}
}