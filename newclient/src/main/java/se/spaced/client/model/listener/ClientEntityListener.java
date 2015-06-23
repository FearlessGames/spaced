package se.spaced.client.model.listener;

import se.spaced.client.model.ClientEntity;
import se.spaced.shared.model.AnimationState;

public interface ClientEntityListener {
	void appearanceDataUpdated(ClientEntity clientEntity);

	void statsUpdated(ClientEntity clientEntity);

	void died(ClientEntity clientEntity);

	void respawned(ClientEntity clientEntity);

	void animationStateChanged(ClientEntity clientEntity, AnimationState animationState);

	void positionalDataChanged(ClientEntity clientEntity);
}
