package se.fearless.bender.services.rss;

import de.nava.informa.core.ItemIF;
import org.jibble.pircbot.Colors;
import se.fearless.bender.services.UrlService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubRssItemFormatter implements RssItemFormatter {
	private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	private final String title;
	private final UrlService tinyUrlService;

	public GitHubRssItemFormatter(String title, UrlService tinyUrlService) {
		this.title = title;
		this.tinyUrlService = tinyUrlService;
	}

	@Override
	public String format(ItemIF item) {
		Matcher matcher = REMOVE_TAGS.matcher(item.getTitle());
		String url = tinyUrlService.getShortUrl(item.getLink());
		return String.format("[%s] %s pushed \"%s\" ( %s )", title, item.getCreator(), matcher.replaceAll(""), url);
	}

	@Override
	public String getColor() {
		return Colors.GREEN;
	}
}
