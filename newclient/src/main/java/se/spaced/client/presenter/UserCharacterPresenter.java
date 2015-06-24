package se.spaced.client.presenter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.util.TimeProvider;
import se.spaced.client.ardor.ui.events.CombatGuiEvents;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.sound.SoundListener;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.items.ContainerType;

@Singleton
public class UserCharacterPresenter implements UserCharacterListener {
	private final EventHandler eventHandler;
	private final ServerConnection serverConnection;
	private final SoundListener soundListener;

	private long lastPositionUpdate;

	@Inject
	public UserCharacterPresenter(
			EventHandler eventHandler,
			TimeProvider timeProvider,
			ServerConnection serverConnection,
			SoundListener soundListener) {
		this.eventHandler = eventHandler;
		this.serverConnection = serverConnection;
		this.soundListener = soundListener;
	}

	// UserCharacter listener
	@Override
	public void combatStateUpdated(final boolean entered) {
		if (entered) {
			eventHandler.fireEvent(CombatGuiEvents.PLAYER_ENTERED_COMBAT);
		} else {
			eventHandler.fireEvent(CombatGuiEvents.PLAYER_LEFT_COMBAT);
		}
	}

	@Override
	public void resurrectionRequested() {
		// Ignored
	}

	@Override
	public void positionalDataUpdated(final PositionalData positionalData) {
		soundListener.setPosition(positionalData.getPosition());
		soundListener.setOrientation(positionalData.getRotation().applyTo(SpacedVector3.PLUS_K),
				positionalData.getRotation().applyTo(SpacedVector3.PLUS_J));
	}

	@Override
	public void equip(SpacedItem clientItem, ContainerType containerType) {
		serverConnection.getReceiver().equipment().equipItem(clientItem, containerType);
	}

	@Override
	public void unequip(ContainerType containerType) {
		serverConnection.getReceiver().equipment().unequipItem(containerType);
	}
}
