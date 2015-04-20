package se.spaced.server.model.spell.effect;

import com.google.inject.Inject;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.model.MagicSchool;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class GrantSpellEffect extends Effect {

	@ManyToOne
	private final ServerSpell spell;

	@Transient
	private final SpellService spellService;

	@Inject
	public GrantSpellEffect(SmrtBroadcaster<S2CProtocol> smrtBroadcaster, SpellService spellService) {
		this(smrtBroadcaster, spellService, null);
	}

	public GrantSpellEffect(SmrtBroadcaster<S2CProtocol> broadcaster, SpellService spellService, ServerSpell spell) {
		super(MagicSchool.LIGHT, broadcaster);
		this.spellService = spellService;
		this.spell = spell;
	}

	@Override
	public void apply(long now, ServerEntity performer, ServerEntity target, String causeName) {
		spellService.addSpellForEntity(target, spell);
		smrtBroadcaster.create().to(target).send().spell().spellAdded(spell);
	}

	@Override
	public void fail(long now, ServerEntity performer, ServerEntity target, String causeName) {
	}

	public ServerSpell getSpell() {
		return spell;
	}
}
