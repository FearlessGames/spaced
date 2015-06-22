package se.spaced.server.model.action;

import com.google.common.collect.Lists;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spell.effect.Effect;

import java.util.List;

public class RepeatingSpellAction extends AbstractRepeatedAction {
	private final Effect effect;
	private final ServerEntity performer;
	private final ServerEntity target;
	private final String causeName;
	private final long timeBetweenTicks;
	private final List<Listener> listeners = Lists.newArrayList();

	public RepeatingSpellAction(
			ActionScheduler actionScheduler,
			long executionTime,
			Effect effect,
			ServerEntity performer,
			ServerEntity target,
			String causeName,
			int numberOfTicks,
			long duration) {
		super(actionScheduler, executionTime);
		this.effect = effect;
		this.performer = performer;
		this.target = target;
		this.causeName = causeName;
		this.timeBetweenTicks = duration / numberOfTicks;
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	@Override
	protected long getTimeToNextUpdate() {
		return timeBetweenTicks;
	}

	@Override
	protected void performRepeat() {
		effect.apply(getExecutionTime(), performer, target, causeName);
		for (Listener listener : listeners) {
			listener.onPerform();
		}
	}

	public interface Listener {
		void onPerform();
	}
}
