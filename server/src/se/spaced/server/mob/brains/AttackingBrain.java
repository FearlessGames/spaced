package se.spaced.server.mob.brains;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.adapter.S2CAdapters;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobInfoProvider;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.SpellRange;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aggro.AggroDispatcher;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.spawn.AttackingParameters;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.shared.model.EntityInteractionCapability;

import java.util.EnumSet;
import java.util.List;

public class AttackingBrain extends AbstractMobBrain {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private State state;
	private final S2CProtocol receiver;

	private final List<ServerSpell> myAttacks;
	private final boolean moveToTarget;
	private final boolean lookAtTarget;
	private final MobInfoProvider mobInfoProvider;
	private final EntityTargetService entityTargetService;
	private long castStartTime;

	public AttackingBrain(
			MobOrderExecutor orderExecutor,
			MobInfoProvider mobInfoProvider, EntityTargetService entityTargetService, Mob mob,
			AttackingParameters attackingParameters) {
		super(mob, orderExecutor);
		this.entityTargetService = entityTargetService;
		myAttacks = Lists.newArrayList(mob.getSpellBook().getSpells());
		this.moveToTarget = attackingParameters.isMoveToTarget();
		this.lookAtTarget = attackingParameters.isLookAtTarget();
		this.mobInfoProvider = mobInfoProvider;
		state = State.IDLE;
		receiver = S2CAdapters.createServerCombatMessages(new AggroDispatcher(mob, mob.getAggroManager()));
	}

	private MobDecision attacking(long now) {
		ServerEntity crossWith = mob.getCurrentAggroTarget();
		if (crossWith == null) {
			entityTargetService.clearTarget(mob);
			return leaveCombat(now);
		}
		entityTargetService.setTarget(mob, crossWith);
		ServerSpell preferredAttack = selectAttackSpell();
		SpellRange rangeForSpell = mobInfoProvider.isInRangeForSpell(mob, crossWith, preferredAttack);
		switch (rangeForSpell) {
			case IN_RANGE:
				orderExecutor.stopWalking(mob);
				orderExecutor.castSpell(mob, crossWith, preferredAttack);
				castStartTime = now;
				state = State.CASTING;
				return casting(now);

			case TOO_FAR_AWAY:
				if (moveToTarget) {
					orderExecutor.runTo(mob, crossWith.getPosition());
				}
				break;
			case TOO_CLOSE:
				if (moveToTarget) {
					orderExecutor.backAwayFrom(mob, crossWith.getPosition(), preferredAttack.getRanges().getStart());
				}
				break;
		}
		if (lookAtTarget) {
			orderExecutor.lookAt(mob, crossWith);
		}

		return MobDecision.DECIDED;
	}

	private MobDecision casting(long now) {
		ServerEntity crossWith = mob.getCurrentAggroTarget();
		if (crossWith == null) {
			return leaveCombat(now);
		}
		orderExecutor.lookAt(mob, crossWith);
		ServerSpell preferredAttack = selectAttackSpell();
		SpellRange rangeForSpell = mobInfoProvider.isInRangeForSpell(mob, crossWith, preferredAttack);
		if (notInRange(rangeForSpell) || !isCasting(now, preferredAttack)) {
			state = State.ATTACKING;
			return attacking(now);
		}
		return MobDecision.DECIDED;
	}

	private boolean isCasting(long now, ServerSpell preferredAttack) {
		return (now - castStartTime < preferredAttack.getCastTime());
	}

	private boolean notInRange(SpellRange rangeForSpell) {
		return rangeForSpell != SpellRange.IN_RANGE;
	}

	private MobDecision leaveCombat(long now) {
		orderExecutor.stopWalking(mob);
		if (!mob.hasAggroTarget()) {
			state = State.IDLE;
		}
		return idle(now);
	}

	private ServerSpell selectAttackSpell() {
		return myAttacks.get(0);
	}


	private MobDecision idle(long now) {
		if (mob.hasAggroTarget()) {
			state = State.ATTACKING;
			return attacking(now);
		}
		return MobDecision.UNDECIDED;
	}

	@Override
	public MobDecision act(long now) {
		if (myAttacks.isEmpty()) {
			logger.error(mob.getName() + " (template " + mob.getTemplate().getName() + " : " + mob.getTemplate().getPk() + ") has no attacking spells!!!");
			return MobDecision.UNDECIDED;
		}
		return state.act(this, now);
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return receiver;
	}

	@Override
	public EnumSet<EntityInteractionCapability> getInteractionCapabilities() {
		return EnumSet.of(EntityInteractionCapability.ATTACK);
	}

	private enum State {
		IDLE {
			@Override
			MobDecision act(AttackingBrain brain, long now) {
				return brain.idle(now);
			}
		},
		ATTACKING {
			@Override
			MobDecision act(AttackingBrain brain, long now) {
				return brain.attacking(now);
			}
		},
		CASTING {
			@Override
			MobDecision act(AttackingBrain brain, long now) {
				return brain.casting(now);
			}
		};

		abstract MobDecision act(AttackingBrain brain, long now);
	}
}
