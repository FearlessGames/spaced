package se.spaced.server.model.spell.effect;

import com.google.inject.Inject;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.MagicSchool;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class InterruptEffect extends Effect {
	private static final String NAME = "Interrupt";
	@Transient
	private final SpellCombatService spellCombatService;

	@Inject
	public InterruptEffect(SpellCombatService spellCombatService, SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		super(smrtBroadcaster);
		this.spellCombatService = spellCombatService;
	}

	public InterruptEffect(SpellCombatService spellCombatService, SmrtBroadcaster<S2CProtocol> smrtBroadcaster, MagicSchool school) {
		super(school, smrtBroadcaster);
		this.spellCombatService = spellCombatService;
	}

	@Override
	public void apply(long now, ServerEntity performer, ServerEntity target, String causeName) {
		smrtBroadcaster.create().toCombat(performer).toCombat(target).send().
				combat().effectApplied(performer, target, getResourceName());
		spellCombatService.interruptSpellCast(target);
	}

	@Override
	public void fail(long now, ServerEntity performer, ServerEntity target, String causeName) {

	}
}
