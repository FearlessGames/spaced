package se.spaced.server.model.spell.effect;

import com.google.inject.Inject;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class CoolEffect extends RangeableEffect {
	@Transient
	private final SpellCombatService spellCombatService;

	@Inject
	public CoolEffect(
			SpellCombatService spellCombatService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		super(smrtBroadcaster);
		this.spellCombatService = spellCombatService;
	}

	public CoolEffect(
			SpellCombatService spellCombatService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			MagicSchool school,
			IntervalInt range) {
		super("Cool", school, range, smrtBroadcaster);
		this.spellCombatService = spellCombatService;
	}

	@Override
	public void apply(long now, ServerEntity performer, ServerEntity target, String causeName) {
		smrtBroadcaster.create().toCombat(performer, target).send().combat().effectApplied(performer,
				target,
				getResourceName());
		spellCombatService.doCool(performer, target, now, this, causeName);
	}

	@Override
	public void fail(long now, ServerEntity performer, ServerEntity target, String causeName) {
	}
}
