package se.spaced.server.mob;

import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;

public class MobWhisperer {
	private final MobOrderExecutor mobOrderExecutor;
	private final Mob mob;

	public MobWhisperer(MobOrderExecutor mobOrderExecutor, Mob mob) {
		this.mobOrderExecutor = mobOrderExecutor;
		this.mob = mob;
	}

	public void whisperEntity(ServerEntity entity, String message) {
		String actualMessage = message.replaceAll("\\$NAME\\$", entity.getName());
		mobOrderExecutor.lookAt(mob, entity);
		mobOrderExecutor.whisper(mob, entity, actualMessage);
	}
}
