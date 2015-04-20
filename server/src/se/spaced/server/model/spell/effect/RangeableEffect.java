package se.spaced.server.model.spell.effect;

import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
public abstract class RangeableEffect extends Effect {
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "start", column = @Column(name = "lowDamage")),
			@AttributeOverride(name = "end", column = @Column(name = "highDamage"))
	})
	protected IntervalInt range;

	protected RangeableEffect(SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		super(smrtBroadcaster);
	}

	protected RangeableEffect(String name, MagicSchool school, IntervalInt range, SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		super(school, smrtBroadcaster);
		this.range = range;
	}

	public IntervalInt getRange() {
		return range;
	}
}
