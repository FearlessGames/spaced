package se.spaced.server.stats;

import com.google.inject.Inject;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.statistics.EventLogger;

import java.util.HashMap;
import java.util.Map;

public class SpellStatisticsCollector extends S2CEmptyReceiver {
	private final EventLogger eventLogger;
	private final Map<Entity, SpellActionEntry> actionMap;

	@Inject
	public SpellStatisticsCollector(SmrtBroadcaster<S2CProtocol> broadcaster, EventLogger eventLogger) {
		this.eventLogger = eventLogger;
		actionMap = new HashMap<>();
		broadcaster.addSpy(this);
	}

	@Override
	public void entityStartedSpellCast(Entity entity, Entity targetEntity, Spell spell) {
		ServerEntity performer = (ServerEntity) entity;
		ServerEntity target = (ServerEntity) targetEntity;
		ServerSpell serverSpell = (ServerSpell) spell;
		SpellActionEntry entry = new SpellActionEntry(performer.getTemplate(), target.getTemplate(), serverSpell);
		if (actionMap.containsKey(entity)) {
			entityStoppedSpellCast(entity, spell);
		}
		actionMap.put(entity, entry);

	}

	@Override
	public void entityCompletedSpellCast(Entity entity, Entity target, Spell spell) {
		if (!actionMap.containsKey(entity)) {
			return;
		}

		SpellActionEntry spellActionEntry = actionMap.remove(entity);
		spellActionEntry.setCompleted(true);
		EntityTemplate performer = spellActionEntry.getPerformer();
		EntityTemplate target1 = spellActionEntry.getTarget();
		ServerSpell spell1 = spellActionEntry.getSpell();
		eventLogger.log("SPELLCAST_COMPLETED", performer.getName(), entity.getPk().toString(), performer.getPk().toString(), target1.getName(), target.getPk().toString(), target1.getPk().toString(), spell1.getName(), spell1.getPk().toString());
	}

	@Override
	public void entityStoppedSpellCast(Entity entity, Spell spell) {
		if (!actionMap.containsKey(entity)) {
			return;
		}

		SpellActionEntry spellActionEntry = actionMap.remove(entity);
		spellActionEntry.setCompleted(false);

		EntityTemplate performer = spellActionEntry.getPerformer();
		EntityTemplate target1 = spellActionEntry.getTarget();
		ServerSpell spell1 = spellActionEntry.getSpell();
		eventLogger.log("SPELLCAST_STOPPED", performer.getName(), performer.getPk().toString(), target1.getName(), target1.getPk().toString(), spell1.getName(), spell1.getPk().toString());
	}
}
