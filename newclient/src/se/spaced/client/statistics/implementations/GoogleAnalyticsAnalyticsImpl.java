package se.spaced.client.statistics.implementations;

import com.boxysystems.jgoogleanalytics.FocusPoint;
import com.boxysystems.jgoogleanalytics.JGoogleAnalyticsTracker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.statistics.Analytics;
import se.spaced.client.statistics.Trackable;

@Singleton
public class GoogleAnalyticsAnalyticsImpl implements Analytics {
	private final JGoogleAnalyticsTracker tracker;

	@Inject
	public GoogleAnalyticsAnalyticsImpl(JGoogleAnalyticsTracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public void track(Trackable trackable) {
		tracker.trackAsynchronously(new FocusPoint(trackable.getEventCode()));
	}
}
