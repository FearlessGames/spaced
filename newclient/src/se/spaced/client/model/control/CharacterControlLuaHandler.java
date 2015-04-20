package se.spaced.client.model.control;

import com.google.inject.Singleton;
import se.ardortech.input.ClientMouseButton;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.shared.util.random.RealRandomProvider;

@Singleton
public class CharacterControlLuaHandler {
	private int moveLeft;
	private int moveRight;
	private int moveForward;
	private int mouseMoveForward;
	private int moveBackward;
	private boolean wantsToJump;
	private boolean sprint;
	private int stanceLow; // Combat Animation Prototype
	private int lungeRight;
	private int stanceRighthand;
	private int shootLeft;
	private int jumpKickSlash;

	private int aimRifle;
	private int fireRifle;
	private int swingRifle;
	private int strikeRifle;
	private int stanceRifle;

	// Mouse button states
	private boolean lmbPressed;
	private boolean rmbPressed;

	// bindings and things
	protected int getMoveFB() {
		if (mouseMoveForward > 0) {
			return mouseMoveForward;
		}

		return moveForward + moveBackward;
	}

	protected int getMoveLR() {
		return moveLeft + moveRight;
	}

	protected int getStanceLow() {
		return stanceLow;
	}

	protected int getLungeRight() {
		return lungeRight;
	}

	protected int getStanceRighthand() {
		return stanceRighthand;
	}

	protected int getShootLeft() {
		return shootLeft;
	}

	protected int getJumpKickSlash() {
		return jumpKickSlash;
	}

	protected int getAimRifle() {
		return aimRifle;
	}

	protected int getFireRifle() {
		return fireRifle;
	}

	protected int getSwingRifle() {
		return swingRifle;
	}

	protected int getStrikeRifle() {
		return strikeRifle;
	}

	protected int getStanceRifle() {
		return stanceRifle;
	}

	@LuaMethod(global = true, name = "MoveForward")
	public void moveForward() {
		moveForward = 1;
	}

	@LuaMethod(global = true, name = "MoveForwardStop")
	public void moveForwardStop() {
		moveForward = 0;
	}

	public void mouseMoveForward() {
		mouseMoveForward = 1;
	}

	public void mouseMoveForwardStop() {
		mouseMoveForward = 0;
	}

	@LuaMethod(global = true, name = "MoveBackwards")
	public void moveBackwards() {
		moveBackward = -1;
	}

	@LuaMethod(global = true, name = "MoveBackwardsStop")
	public void moveBackwardsStop() {
		moveBackward = 0;
	}

	@LuaMethod(global = true, name = "MoveRight")
	public void moveRight() {
		moveRight = -1;
	}

	@LuaMethod(global = true, name = "MoveRightStop")
	public void moveRightStop() {
		moveRight = 0;
	}

	@LuaMethod(global = true, name = "MoveLeft")
	public void moveLeft() {
		moveLeft = 1;
	}

	@LuaMethod(global = true, name = "MoveLeftStop")
	public void moveLeftStop() {
		moveLeft = 0;
	}


	@LuaMethod(global = true, name = "SprintStart")
	public void sprintStart() {
		sprint = true;
	}

	@LuaMethod(global = true, name = "SprintStop")
	public void sprintStop() {
		sprint = false;
	}

	@LuaMethod(global = true, name = "StartJump")
	public void startJump() {
		wantsToJump = true;
	}

	@LuaMethod(global = true, name = "StopJump")
	public void stopJump() {
		wantsToJump = false;
	}

	//Combat Animation Prototype
	@LuaMethod(global = true, name = "StanceLow")
	public void stanceLow() {
		stanceLow = 1;
	}

	@LuaMethod(global = true, name = "StanceLowStop")
	public void stanceLowStop() {
		stanceLow = 0;
	}

	@LuaMethod(global = true, name = "LungeRight")
	public void lungeRight() {
		lungeRight = new RealRandomProvider().getInteger(1, 2);
	}

	@LuaMethod(global = true, name = "LungeRightStop")
	public void lungeRightStop() {
		lungeRight = 0;
	}

	@LuaMethod(global = true, name = "StanceRighthand")
	public void stanceRighthand() {
		stanceRighthand = new RealRandomProvider().getInteger(1, 4);
	}

	@LuaMethod(global = true, name = "StanceRighthandStop")
	public void stanceHighStop() {
		stanceRighthand = 0;
	}

	@LuaMethod(global = true, name = "ShootLeft")
	public void shootLeft() {
		shootLeft = 1;
	}

	@LuaMethod(global = true, name = "ShootLeftStop")
	public void shootLeftStop() {
		shootLeft = 0;
	}

	@LuaMethod(global = true, name = "JumpKickSlash")
	public void jumpKickSlash() {
		jumpKickSlash = 1;
	}

	@LuaMethod(global = true, name = "JumpKickSlashStop")
	public void jumpKickSlashStop() {
		jumpKickSlash = 0;
	}

// Rifle Combat

	@LuaMethod(global = true, name = "AimRifleStop")
	public void aimRifleStop() {
		aimRifle = 0;
	}

	@LuaMethod(global = true, name = "AimRifle")
	public void aimRifle() {
		aimRifle = 1;
	}

	@LuaMethod(global = true, name = "FireRifleStop")
	public void fireRifleStop() {
		fireRifle = 0;
	}

	@LuaMethod(global = true, name = "FireRifle")
	public void fireRifle() {
		fireRifle = 1;
	}

	@LuaMethod(global = true, name = "SwingRifle")
	public void swingRifle() {
		swingRifle = 1;
	}

	@LuaMethod(global = true, name = "SwingRifleStop")
	public void swingRifleStop() {
		swingRifle = 0;
	}

	@LuaMethod(global = true, name = "StrikeRifle")
	public void strikeRifle() {
		strikeRifle = 1;
	}

	@LuaMethod(global = true, name = "StrikeRifleStop")
	public void strikeRifleStop() {
		strikeRifle = 0;
	}

	@LuaMethod(global = true, name = "StanceRifle")
	public void stanceRifle() {
		stanceRifle = 1;
	}

	@LuaMethod(global = true, name = "StanceRifleStop")
	public void stanceRifleStop() {
		stanceRifle = 0;
	}


	public boolean isWantsToJump() {
		return wantsToJump;
	}

	public boolean isSprint() {
		return sprint;
	}

	public void onMouseButton(ClientMouseButton button, boolean pressed, int x, int y) {
		if (button.equals(ClientMouseButton.LEFT)) {
			lmbPressed = pressed;
		}

		if (button.equals(ClientMouseButton.RIGHT)) {
			rmbPressed = pressed;
		}
	}

	public boolean rmbPressed() {
		return rmbPressed;
	}

	public boolean lmbPressed() {
		return lmbPressed;
	}
}
