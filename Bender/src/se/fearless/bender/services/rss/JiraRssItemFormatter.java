package se.fearless.bender.services.rss;

import de.nava.informa.core.ItemIF;
import org.jibble.pircbot.Colors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraRssItemFormatter implements RssItemFormatter {
	private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	private final String title;

	public JiraRssItemFormatter(String title) {
		this.title = title;
	}

	@Override
	public String format(ItemIF item) {
		Matcher matcher = REMOVE_TAGS.matcher(item.getTitle());

		return String.format("[%s] %s", title, matcher.replaceAll(""));
	}

	@Override
	public String getColor() {
		return Colors.PURPLE;
	}
}
