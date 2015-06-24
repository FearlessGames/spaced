package se.spaced.client.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.util.ListenerDispatcher;

/**
 * Represents the entity played by the local user.
 */
@Singleton
public class UserCharacter {
	private final ListenerDispatcher<UserCharacterListener> dispatcher;
	private ClientEntity self;
	private boolean inCombat;
	private CharacterPhysics physics;
	private boolean gm;
	private boolean frozen;

	@Inject
	public UserCharacter(ListenerDispatcher<UserCharacterListener> dispatcher) {
		this.dispatcher = dispatcher;
	}

	public CharacterPhysics getPhysics() {
		return physics;
	}

	public boolean isInCombat() {
		return inCombat;
	}

	public void setInCombat(final boolean inCombat) {
		if (this.inCombat == inCombat) {
			return;
		}

		this.inCombat = inCombat;
		dispatcher.trigger().combatStateUpdated(inCombat);
	}

	public UUID getPk() {
		return self.getPk();
	}

	public SpacedVector3 getPosition() {
		return self.getPosition();
	}

	public String getName() {
		return self.getName();
	}

	public PositionalData getPositionalData() {
		return self.getPositionalData();
	}

	public AppearanceData getAppearanceData() {
		return self.getAppearanceData();
	}

	public SpacedRotation getRotation() {
		return self.getRotation();
	}

	public boolean isAlive() {
		return self.isAlive();
	}

	public void setPositionalData(final PositionalData positionalData) {
		if (self.isAlive()) {
			self.setPositionalData(positionalData);
			dispatcher.trigger().positionalDataUpdated(positionalData);
		}
	}

	public void playAnimation(AnimationState newState) {
		self.playAnimation(newState);
	}

	public void setAppearanceData(AppearanceData appearanceData) {
		self.setAppearanceData(appearanceData);
	}

	public EntityStats getBaseStats() {
		return self.getBaseStats();
	}

	public boolean isUserControlledEntity(final ClientEntity entity) {
		return self.equals(entity);
	}

	public ClientEntity getUserControlledEntity() {
		return self;
	}


	public void setControlledEntity(ClientEntity spacedEntity) {
		self = spacedEntity;
		physics = new CharacterPhysics(spacedEntity.getPositionalData());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UserCharacter that = (UserCharacter) o;

		if (self != null ? !self.equals(that.self) : that.self != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return self != null ? self.hashCode() : 0;
	}

	public void setIsGm(boolean gm) {
		this.gm = gm;
	}

	public boolean isGm() {
		return gm;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public boolean isFrozen() {
		return frozen;
	}
}
