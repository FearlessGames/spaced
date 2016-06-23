package se.spaced.messages.protocol;

import com.google.common.collect.ImmutableSet;
import se.fearless.common.stats.ModStat;
import se.fearless.common.uuid.UUID;

public interface AuraTemplate {
	UUID getPk();

	String getName();

	String getIconPath();

	long getDuration();

	boolean isVisible();

	ImmutableSet<ModStat> getMods();

	boolean isKey();
}
