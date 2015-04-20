package se.spaced.client.model.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.PlayerTargeting;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;

@Singleton
public class PlayerActions {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final UserCharacter userCharacter;
	private final ServerConnection serverConnection;
	private final ListenerDispatcher<UserCharacterListener> dispatcher;
	private final PlayerTargeting targeting;
	private final PlayerEquipment equipment;

	private final LocalRecorder recorder;

	@Inject
	public PlayerActions(
			UserCharacter userCharacter,
			ServerConnection serverConnection,
			ListenerDispatcher<UserCharacterListener> dispatcher,
			PlayerTargeting targeting, PlayerEquipment equipment, LocalRecorder recorder) {
		this.userCharacter = userCharacter;
		this.serverConnection = serverConnection;
		this.dispatcher = dispatcher;
		this.targeting = targeting;
		this.equipment = equipment;
		this.recorder = recorder;
	}

	public void startSpellCast(ClientSpell spell) {
		if (recorder.state().isMoving() && spell.isCancelOnMove()) {
			logger.info("Can only start casting if not moving, this logger should be replaced with a UI notification");
			return;
		}
		final ClientEntity target = targeting.getTarget();
		logger.info("Starting spellcast with target {}", target);
		serverConnection.getReceiver().combat().startSpellCast(target, spell);
	}

	public void stopSpellCast() {
		logger.info("Stopping spellcast");
		serverConnection.getReceiver().combat().stopSpellCast();
	}

	public void resurrect() {
		if (!userCharacter.isAlive()) {
			serverConnection.getReceiver().entity().requestResurrection();
			dispatcher.trigger().resurrectionRequested();
		}
	}

	public void dance() {
		RandomProvider randomDance = new RealRandomProvider();
		recorder.record(AnimationState.valueOf("DANCE" + randomDance.getInteger(1, 4)));
	}

	public void sit() {
		recorder.record(AnimationState.SIT);
	}

	public void sleep() {
		recorder.record(AnimationState.SLEEP);
	}

	public void equip(ClientItem item) {
		equip(item, equipment.findEquippableContainer(item));
	}

	public void equip(ClientItem item, ContainerType container) {
		dispatcher.trigger().equip(item, container);
	}

	public void unequip(ContainerType container) {
		dispatcher.trigger().unequip(container);
	}

	public void unstuck() {
		serverConnection.getReceiver().entity().unstuck();
}
}
