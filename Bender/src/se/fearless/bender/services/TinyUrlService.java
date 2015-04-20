package se.fearless.bender.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class TinyUrlService implements UrlService {
	private static final Logger log = LoggerFactory.getLogger(TinyUrlService.class);
	private final WGet wget;

	public TinyUrlService(WGet wget) {
		this.wget = wget;
	}

	@Override
	public String getShortUrl(String longUrl) {
		try {
			StringBuilder tinyUrlApiUrl = new StringBuilder();
			tinyUrlApiUrl.append("http://tinyurl.com/api-create.php?url=");
			tinyUrlApiUrl.append(longUrl);

			URL url = new URL(tinyUrlApiUrl.toString());

			String result = wget.getContent(url);
			log.debug("Created tinyurl (apiUrl: " + tinyUrlApiUrl.toString() + "), tinyUrl: " + result);

			if (result.isEmpty()) {
				return longUrl;
			}

			result = result.toLowerCase();

			if (!result.startsWith("http://")) {
				result = result.substring(result.indexOf("http://"));
			}

			return result;

		} catch (Exception e) {
			log.error("Failed to get tinyurl for commit: " + longUrl, e);
			return longUrl;
		}
	}

	@Override
	public String getShortUrl(URL longUrl) {
		try {
			return getShortUrl(longUrl.toString());
		} catch (Exception e) {
			log.error("Failed to get tinyurl for commit: " + longUrl, e);
			return longUrl + "";
		}
	}
}