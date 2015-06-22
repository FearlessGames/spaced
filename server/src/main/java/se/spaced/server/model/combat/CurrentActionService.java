package se.spaced.server.model.combat;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.OrderedAction;

import java.util.Map;

@Singleton
public class CurrentActionService {
	private final Map<ServerEntity, OrderedAction> currentActions;

	@Inject
	public CurrentActionService() {
		currentActions = Maps.newHashMap();
	}


	public void setCurrentAction(OrderedAction action) {
		clearCurrentAction(action.getPerformer());
		currentActions.put(action.getPerformer(), action);
	}

	public OrderedAction getCurrentAction(ServerEntity entity) {
		return currentActions.get(entity);
	}

	public void clearCurrentAction(ServerEntity performer) {
		currentActions.remove(performer);
	}

	public void cancelCurrentAction(ServerEntity performer) {
		OrderedAction previousAction = currentActions.get(performer);
		if (previousAction != null && !previousAction.isCancelled()) {
			previousAction.cancel();
		}
		clearCurrentAction(performer);
	}
}
