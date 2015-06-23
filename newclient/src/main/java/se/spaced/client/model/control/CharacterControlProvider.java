package se.spaced.client.model.control;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.input.ClientMouseButton;
import se.krka.kahlua.integration.annotations.LuaMethod;

@Singleton
public class CharacterControlProvider {
	protected CharacterControl currentControl;
	private final GroundAndAirControl groundAndAirControl;
	protected final WalkingCharacterControl walkingControl;
	protected final FlyingCharacterControl flyingControl;
	protected final HelicopterControl helicopterControl;
	protected final KrkaHelicopterControl krkaHelicopterControl;
	protected final DisableControl disableControl;
	protected final PlaneCharacterControl planeControl;

	@Inject
	CharacterControlProvider(
			HelicopterControl helicopterControl,
			WalkingCharacterControl walkingControl,
			FlyingCharacterControl flyingControl,
			DisableControl disableControl,
			KrkaHelicopterControl krkaHelicopterControl,
			GroundAndAirControl groundAndAirControl,
			PlaneCharacterControl planeCharacterControl) {
		this.walkingControl = walkingControl;
		this.flyingControl = flyingControl;
		this.helicopterControl = helicopterControl;
		this.disableControl = disableControl;
		this.krkaHelicopterControl = krkaHelicopterControl;

		currentControl = groundAndAirControl;
		this.groundAndAirControl = groundAndAirControl;
		this.planeControl = planeCharacterControl;
	}

	public CharacterControl getCurrentControl() {
		return currentControl;
	}

	private void setCurrentControl(CharacterControl newCurrentControl) {
		if (newCurrentControl != currentControl) {
			currentControl.onDeselected();
			newCurrentControl.onSelected();
			currentControl = newCurrentControl;
		}
	}

	public void setWalking() {
		//setCurrentControl(walkingControl);
		setCurrentControl(groundAndAirControl);
	}

	public void setFlying() {
		setCurrentControl(flyingControl);
	}

	private void setPlane() {
		setCurrentControl(planeControl);
	}

	@LuaMethod(global = true, name = "SetHelicopterMode")
	public void setHelicopter() {
		if (currentControl == krkaHelicopterControl) {
			setCurrentControl(helicopterControl);
		} else {
			setCurrentControl(krkaHelicopterControl);
		}
	}

	@LuaMethod(global = true, name = "SetGodMode")
	public void setGodMode(boolean enable) {
		if (enable) {
			setFlying();
		} else {
			setWalking();
		}
	}

	@LuaMethod(global = true, name = "SetPlaneMode")
	public void setPlaneMode(boolean enable) {
		if (enable) {
			setPlane();
		} else {
			setWalking();
		}
	}

	public void onMouseButton(ClientMouseButton button, boolean pressed, int x, int y) {
		getCurrentControl().onMouseButton(button, pressed, x, y);
	}
}
