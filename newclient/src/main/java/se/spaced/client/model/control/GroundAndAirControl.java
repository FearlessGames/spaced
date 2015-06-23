package se.spaced.client.model.control;

import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.input.ClientMouseButton;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.model.CharacterPhysics;
import se.spaced.client.model.ClientAuraService;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.messages.protocol.ClientAuraInstance;
import se.spaced.messages.protocol.ClientAuraTemplate;
import se.spaced.shared.model.aura.ModStat;

import java.util.Collections;

@Singleton
public class GroundAndAirControl extends CharacterControl {

	private final CharacterControl groundControl;
	private final CharacterControl airControl;

	private CharacterControl current;
	private boolean readyForTakeoff;
	private final ClientAuraService auraService;
	private static final ClientAuraInstance JETPACK_AURA = new ClientAuraInstance(
			new ClientAuraTemplate(UUID.fromString("509e2420-8447-45bf-ae51-9e8196df4575"),
					null,
					0,
					null,
					false, Collections.<ModStat>emptySet(), true), 0, new SystemTimeProvider());

	@Inject
	public GroundAndAirControl(
			UserCharacter userCharacter,
			CharacterControlLuaHandler luaHandler,
			PhysicsWorld physicsWorld,
			Camera camera,
			WalkingCharacterControl groundControl,
			KrkaHelicopterControl airControl, GroundImpactListener groundImpactListener,
			LocalRecorder recorder,
			ClientTeleporter teleporter, ClientAuraService auraService) {
		super(userCharacter, luaHandler, physicsWorld, camera, groundImpactListener, recorder, teleporter);
		this.groundControl = groundControl;
		this.airControl = airControl;

		current = groundControl;
		this.auraService = auraService;
	}

	@Override
	public void onMouseMove(int dx, int dy, boolean lmb, boolean rmb) {
		current.onMouseMove(dx, dy, lmb, rmb);
	}

	@Override
	public void onMouseButton(ClientMouseButton button, boolean pressed, int x, int y) {
		current.onMouseButton(button, pressed, x, y);
	}

	@Override
	public void onMouseWheel(int wheelDelta) {
		current.onMouseWheel(wheelDelta);
	}

	@Override
	public void updateCamera(double dt, PhysicsWorld physicsWorld) {
		current.updateCamera(dt, physicsWorld);
	}

	@Override
	public void animate() {
		current.animate();
	}

	@Override
	public void updatePhysics(long millisPerFrame, PhysicsWorld physicsWorld) {
		CharacterPhysics self = userCharacter.getPhysics();
		if (self.getGroundContact()) {
			setControl(groundControl);
		}
		current.updatePhysics(millisPerFrame, physicsWorld);
	}

	@Override
	public void updateSteering(double dt, PhysicsWorld physicsWorld) {
		if (jumping()) {
			if (!keys.isWantsToJump()) {
				readyForTakeoff = true;
			} else {
				if (readyForTakeoff) {
					setControl(airControl);
				}
				readyForTakeoff = false;
			}
		} else {
			readyForTakeoff = false;
		}
		current.updateSteering(dt, physicsWorld);
	}

	private void setControl(CharacterControl newControl) {
		if (newControl == airControl && !auraService.selfHasAura(JETPACK_AURA)) {
			return;
		}
		if (current != newControl) {
			current.onDeselected();
			current = newControl;
			current.onSelected();
		}
	}

	private boolean jumping() {
		return !userCharacter.getPhysics().canJump();
	}

	@Override
	public void onSelected() {
		current.onSelected();
	}

	@Override
	public void onDeselected() {
		current.onDeselected();
	}
}