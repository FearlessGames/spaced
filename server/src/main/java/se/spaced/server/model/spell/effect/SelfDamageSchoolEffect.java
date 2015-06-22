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
public class SelfDamageSchoolEffect extends RangeableEffect {
	private static final String NAME = "Self School Damage";
	@Transient
	private final SpellCombatService spellCombatService;

	@Inject
	public SelfDamageSchoolEffect(
			SpellCombatService spellCombatService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		super(smrtBroadcaster);
		this.spellCombatService = spellCombatService;
	}

	public SelfDamageSchoolEffect(
			SpellCombatService spellCombatService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			IntervalInt range,
			MagicSchool school) {
		super(NAME, school, range, smrtBroadcaster);
		this.spellCombatService = spellCombatService;
	}

	@Override
	public void apply(long now, ServerEntity performer, ServerEntity target, String causeName
	) {
		smrtBroadcaster.create().toCombat(performer, target).send().combat().effectApplied(performer, target, getResourceName());
		spellCombatService.doDamage(performer, performer, now, this, causeName);
	}

	@Override
	public void fail(long now, ServerEntity performer, ServerEntity target, String causeName) {
		spellCombatService.doMiss(performer, performer, now, this, causeName);
	}


}