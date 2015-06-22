package se.spaced.server.model.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.CombatMechanics;
import se.spaced.server.model.combat.CurrentActionService;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

public class SpellAction extends TargetedAction {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final CombatMechanics mechanics;
	private final ServerSpell spell;

	private final CurrentActionService currentActionService;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final SpellListener listener;

	public SpellAction(
			CombatMechanics mechanics, long executionTime, ServerEntity performer, ServerEntity target,
			ServerSpell spell,
			CurrentActionService currentActionService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			SpellListener listener) {
		super(executionTime, performer, target);
		this.mechanics = mechanics;
		this.spell = spell;
		this.currentActionService = currentActionService;
		this.smrtBroadcaster = smrtBroadcaster;
		this.listener = listener;
	}

	@Override
	public void perform() {
		if (shouldPerform()) {
			long now = getExecutionTime();
			performer.warmUp(spell.getHeatContribution());
			applyAllEffects(now);

			smrtBroadcaster.create().toCombat(performer).send().combat().entityCompletedSpellCast(performer, target, spell);
			smrtBroadcaster.create().toCombat(performer).send().entity().updateStats(performer, performer.getBaseStats());
			if (listener != null) {
				listener.notifySpellCompleted();
			}
		} else {
			cancel();
		}
		currentActionService.clearCurrentAction(performer);
	}

	private void applyAllEffects(long now) {
		spell.perform(now, performer, target);
	}

	private boolean shouldPerform() {
		return mechanics.isAllowedToCompleteCast(getPerformer(), target, spell, currentActionService);
	}

	public ServerSpell getSpell() {
		return spell;
	}

	@Override
	public void onCancel() {
		smrtBroadcaster.create().to(performer, target).toArea(performer).send().combat().entityStoppedSpellCast(performer, spell);
	}

	@Override
	public void performerMoved() {
		if (spell.cancelOnMove()) {
			currentActionService.cancelCurrentAction(performer);
		}
	}
}
