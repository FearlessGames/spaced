package se.spaced.shared.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("AnimationState")
public enum AnimationState {
	WALK(true),
	STRAFE_LEFT(true),
	STRAFE_RIGHT(true),
	WALK_BACK(true),
	RUN(true),
	IDLE(false),
	TURN_LEFT(false),
	TURN_RIGHT(false),
	JUMP(true),
	COMBAT_STANCE_1(false),
	COMBAT_STANCE_2(false),
	COMBAT_STANCE_3(false),
	COMBAT_SWING_RIFLE(false),
	COMBAT_STRIKE_RIFLE_BUTT(false),
	COMBAT_STRIKE_RIFLE_BARREL(false),
	COMBAT_AIM_RIFLE(false),
	COMBAT_FIRE_RIFLE(false),
	COMBAT_STANCE_RIFLE(false),
	ATTACK_AIM_LEFT(false),
	MELEE_KICK_SLASH_BACKHAND(false),
	ATTACK_SWING_RIGHT(false),
	ATTACK_SWING_RIGHT_HIGH(false),
	MELEE_STANCE_PUSHED(false),
	MELEE_STANCE_HIGH(false),
	MELEE_STANCE_BACKHAND(false),
	MELEE_STANCE_STAB(false),
	MELEE_STANCE_LOW(false),
	FLY(true),
	FLY_THRUST(true),
	SWIM(true),
	SIT(IDLE, false),
	SIT_PILOT(IDLE, true),
	SLEEP(IDLE, false),
	DANCE1(IDLE, false),
	DANCE2(IDLE, false),
	DANCE3(IDLE, false),
	DANCE4(IDLE, false),
	WALK_STRAFE_LEFT(true),
	WALK_STRAFE_RIGHT(true),
	WALK_BACK_STRAFE_LEFT(true),
	WALK_BACK_STRAFE_RIGHT(true),
	TELEPORT_OUT(true),
	TELEPORT_IN(true),
	DEAD(false),
	COMBAT_DEFAULT_ATTACK(false);

	private final AnimationState superType;
	private final boolean moving;

	private AnimationState() {
		this(false);
	}

	private AnimationState(boolean moving) {
		this.moving = moving;
		this.superType = this;
	}

	private AnimationState(AnimationState superType, boolean moving) {
		this.superType = superType;
		this.moving = moving;
	}

	public boolean is(AnimationState state) {
		return this == state || this.superType == state;
	}

	public boolean isMoving() {
		return moving;
	}
}