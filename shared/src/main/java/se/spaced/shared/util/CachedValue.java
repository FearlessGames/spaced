package se.spaced.shared.util;

import se.fearlessgames.common.util.TimeProvider;

/**
 * class to hold any generic cashed data. Will call the cache updater to get the value when it needs to refresh
 * Make sure the CacheUpdater is the same generic type as the cachedvalue
 *
 * @param <T>
 */
public class CachedValue<T> {
	private final TimeProvider timeProvider;
	private T cachedData;
	private long expireTime;
	private long cacheTimestamp;
	private final CacheUpdater<T> cacheUpdater;

	public CachedValue(TimeProvider timeProvider, long expireTime, CacheUpdater<T> cacheUpdater) {
		this.timeProvider = timeProvider;
		this.expireTime = expireTime;
		this.cacheUpdater = cacheUpdater;
		cacheTimestamp = timeProvider.now();
	}

	public void setCachedData(T cachedData) {
		this.cachedData = cachedData;
	}

	public T getCachedData() {
		if (cachedData == null || isExpired()) {
			refreshData();
		}
		return cachedData;
	}

	private void refreshData() {
		cachedData = cacheUpdater.refreshCashedData();
		cacheTimestamp = timeProvider.now();

	}

	private boolean isExpired() {
		return ((timeProvider.now() - cacheTimestamp) > expireTime);
	}


}
