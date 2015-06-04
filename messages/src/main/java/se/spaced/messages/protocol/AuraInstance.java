package se.spaced.messages.protocol;

public interface AuraInstance extends AuraTemplate {
	long getTimeLeft(long now);

	AuraTemplate getTemplate();
}
