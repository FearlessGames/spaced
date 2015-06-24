package se.spaced.client.presenter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearless.common.uuid.UUID;
import se.spaced.client.ardor.ui.events.CombatGuiEvents;
import se.spaced.client.model.PlayerTargeting;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.player.PlayerTargetingListener;
import se.spaced.client.model.player.TargetInfo;
import se.spaced.client.view.cursor.CursorView;
import se.spaced.client.view.entity.EntityView;
import se.spaced.client.view.entity.EntityViewListener;
import se.spaced.shared.events.EventHandler;

@Singleton
public class TargetingPresenter implements EntityViewListener, PlayerTargetingListener {
	private final EntityView entityView;
	private final EventHandler luaEventHandler;
	private final CursorView cursorView;
	private final PlayerTargeting targeting;
	private final UserCharacter userCharacter;

	@Inject
	public TargetingPresenter(
			EntityView entityView,
			EventHandler luaEventHandler,
			CursorView cursorView,
			PlayerTargeting targeting, UserCharacter userCharacter) {
		this.entityView = entityView;
		this.luaEventHandler = luaEventHandler;
		this.cursorView = cursorView;
		this.targeting = targeting;
		this.userCharacter = userCharacter;
	}

	@Override
	public void entityLeftClicked(final UUID entityUuid) {
		targeting.setTarget(entityUuid);
	}

	@Override
	public void entityRightClicked(UUID entityUuid) {

	}

	@Override
	public void nothingLeftClicked() {
		targeting.clearTarget();
	}

	@Override
	public void nothingRightClicked() {

	}

	@Override
	public void entityHovered(final UUID entityUuid) {
		targeting.setHover(entityUuid);
	}

	@Override
	public void hoverReset() {
		targeting.clearHover();
	}

	// PlayerTargetingListener
	// TODO: Don't trigger player target changed when only target state updated
	@Override
	public void newTarget(final TargetInfo targetInfo) {
		entityView.setTargetedEntity(targetInfo);
		luaEventHandler.fireEvent(CombatGuiEvents.UNIT_CHANGED_TARGET,
				userCharacter.getUserControlledEntity(),
				targetInfo.getClientEntity());
	}

	@Override
	public void targetCleared() {
		entityView.clearTargetedEntity();
		luaEventHandler.fireEvent(CombatGuiEvents.UNIT_CHANGED_TARGET, userCharacter.getUserControlledEntity(), null);
	}

	@Override
	public void newHover(final TargetInfo targetInfo) {
		entityView.setHoveredEntity(targetInfo);
		// TODO: Use some sort of service that contains the logic of selecting the correct cursor for given target
		cursorView.newHover(targetInfo);
	}

	@Override
	public void hoverCleared() {
		entityView.clearHoveredEntity();
		cursorView.newHover(null);
	}
}
