package se.spaced.server.model.spell.effect;

import com.google.inject.Inject;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.MagicSchool;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class ApplyAuraEffect extends Effect {

	@OneToOne(cascade = CascadeType.ALL)
	private ServerAura serverAura;
	
	@Transient
	private final AuraService auraService;

	@Inject
	public ApplyAuraEffect(
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			AuraService auraService) {
		super(smrtBroadcaster);
		this.auraService = auraService;
	}

	public ApplyAuraEffect(
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			MagicSchool school,
			ServerAura serverAura,
			AuraService auraService) {
		super(school, smrtBroadcaster);
		this.serverAura = serverAura;
		this.auraService = auraService;
	}

	@Override
	public void apply(long now, ServerEntity performer, final ServerEntity target, String causeName) {
		auraService.apply(performer, target, serverAura, now);
	}

	@Override
	public void fail(long now, ServerEntity performer, ServerEntity target, String causeName) {
	}
}