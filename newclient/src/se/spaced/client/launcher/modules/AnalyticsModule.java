package se.spaced.client.launcher.modules;

import com.boxysystems.jgoogleanalytics.JGoogleAnalyticsTracker;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import se.spaced.client.statistics.Analytics;
import se.spaced.client.statistics.implementations.GoogleAnalyticsAnalyticsImpl;

public class AnalyticsModule implements Module {


	private static final String APP_NAME = "spaced";
	public static final String ANALYTICS_CODE = "UA-12038900-5";

	@Override
	public void configure(Binder binder) {
	}

	@Provides
	public Analytics getAnalytics() {
		JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(APP_NAME, ANALYTICS_CODE);
//		tracker.setLoggingAdapter(new LoggingAdapter() {
//			@Override
//			public void logError(String s) {
//				System.out.println(s);
//			}
//
//			@Override
//			public void logMessage(String s) {
//				System.out.println(s);
//			}
//		});
		return new GoogleAnalyticsAnalyticsImpl(tracker);
	}
}
