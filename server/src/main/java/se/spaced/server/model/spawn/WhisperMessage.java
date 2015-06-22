package se.spaced.server.model.spawn;

import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class WhisperMessage extends ExternalPersistableBase {
	@Column(length = 2048)
	private final String message;
	private final double whisperDistance;
	private final long whisperTimeout;

	WhisperMessage() {
		this("", 0.0, Long.MAX_VALUE);
	}

	public WhisperMessage(String message, double whisperDistance, long whisperTimeout) {
		this.whisperDistance = whisperDistance;
		this.message = message;
		this.whisperTimeout = whisperTimeout;
	}

	public double getDistance() {
		return whisperDistance;
	}

	public long getTimeout() {
		return whisperTimeout;
	}

	public String getMessage() {
		return message;
	}
}
