package se.spaced.server.net.listeners.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.c2s.ClientCombatMessages;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.cooldown.CooldownService;
import se.spaced.server.model.cooldown.CooldownTemplate;
import se.spaced.server.model.cooldown.SimpleCooldown;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.spell.SpellService;

public class ClientCombatMessagesAuth implements ClientCombatMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ClientConnection clientConnection;
	private final SpellCombatService spellCombatService;
	private final SpellService spellService;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final TimeProvider timeProvider;
	private final CooldownService cooldownService;

	public ClientCombatMessagesAuth(
			ClientConnection clientConnection,
			SpellCombatService spellCombatService,
			SpellService spellService,
			TimeProvider timeProvider, CooldownService cooldownService) {
		this.clientConnection = clientConnection;
		this.spellCombatService = spellCombatService;
		this.spellService = spellService;
		this.timeProvider = timeProvider;
		this.cooldownService = cooldownService;
	}

	@Override
	public void startSpellCast(Entity target, Spell spell) {
		Player player = clientConnection.getPlayer();

		//  TODO: do this from main thread?

		if (spellService.entityHasSpell(player, (ServerSpell) spell)) {
			spellCombatService.startSpellCast(player, (ServerEntity) target, (ServerSpell) spell, timeProvider.now(), null);
		} else {
			log.warn("{} tried to cast {} which it doesn't have", player, spell);
		}
	}

	@Override
	public void stopSpellCast() {
		Player player = clientConnection.getPlayer();
		spellCombatService.stopSpellCast(player);
	}

	@Override
	public void requestCooldownData(Cooldown cooldown) {
		final Player player = clientConnection.getPlayer();
		if (player == null) {
			throw new IllegalStateException("Must be in game to request cooldown data");
		}

		CooldownTemplate cooldownTemplate = cooldownService.find(cooldown);
		final SimpleCooldown simpleCooldown = player.getSimpleCooldownInstance(cooldownTemplate, timeProvider.now());
		clientConnection.getReceiver().combat().cooldownData(new CooldownData(simpleCooldown.getCooldownTemplate().getPk(), simpleCooldown.getLinearTimeValue()));
	}
}
