package se.spaced.client.core.states;

import com.google.inject.Inject;
import se.spaced.client.ardor.GameInputListener;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.effect.EffectSystem;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.model.PlaybackService;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.CharacterControlProvider;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.client.resources.zone.ScenegraphService;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.view.entity.EntityInteractionView;
import se.spaced.client.view.entity.EntityView;

public class WorldGameState implements WorldGameStateMarkup {
	private final CharacterControlProvider controlProvider;
	private final EntityView entityView;
	private final EntityInteractionView interactionView;
	private final ZoneActivationService zoneActivationService;
	private final EffectSystem effectSystem;
	private final SpacedGui spacedGui;
	private final UserCharacter userCharacter;
	private final PhysicsWorld physicsWorld;
	private final ScenegraphService scenegraphService;
	private final GameInputListener gameInputListener;
	private final InputManager inputManager;
	private final PlaybackService playbackService;

	@Inject
	public WorldGameState(
			CharacterControlProvider controlProvider,
			EffectSystem effectSystem,
			SpacedGui spacedGui,
			PhysicsWorld physicsWorld,
			UserCharacter userCharacter,
			EntityView entityView,
			ZoneActivationService zoneActivationService,
			ScenegraphService scenegraphService,
			EntityInteractionView interactionView,
			GameInputListener gameInputListener,
			InputManager inputManager, PlaybackService playbackService) {
		this.controlProvider = controlProvider;
		this.effectSystem = effectSystem;
		this.spacedGui = spacedGui;
		this.physicsWorld = physicsWorld;
		this.userCharacter = userCharacter;
		this.entityView = entityView;
		this.zoneActivationService = zoneActivationService;
		this.scenegraphService = scenegraphService;
		this.interactionView = interactionView;
		this.gameInputListener = gameInputListener;
		this.inputManager = inputManager;
		this.playbackService = playbackService;
	}

	@Override
	public void exit() {
		entityView.reset();
		inputManager.removeKeyListener(gameInputListener);
		inputManager.removeMouseListener(gameInputListener);
	}

	@Override
	public void start() {
		spacedGui.start(new String[]{"ui/setup"});
		inputManager.addKeyListener(gameInputListener);
		inputManager.addMouseListener(gameInputListener);
	}


	@Override
	public void update(final GameStateContext context, double timePerFrame) {
		scenegraphService.update(userCharacter.getPosition(), 1000.0);

		// TODO: make visual range tweakable
		zoneActivationService.update(userCharacter.getPosition(), 2500.0);

		controlProvider.getCurrentControl().update(timePerFrame, physicsWorld);

		entityView.update(timePerFrame);
		interactionView.update(timePerFrame);
		effectSystem.update(timePerFrame);
		spacedGui.onUpdate(timePerFrame);
	}

	@Override
	public void updateFixed(GameStateContext context, long millisPerFrame) {
		controlProvider.getCurrentControl().update(millisPerFrame/1000.0, physicsWorld);

		playbackService.update();
		if (!scenegraphService.waitingForPhysics() && !userCharacter.isFrozen()) {
			controlProvider.getCurrentControl().updateFixed(millisPerFrame, physicsWorld);
		}
	}
}
