package se.fearless.bender.services.rss;

import de.nava.informa.core.ItemIF;
import org.jibble.pircbot.Colors;

public class ForumsRssItemFormatter implements RssItemFormatter {
	private final String title;

	public ForumsRssItemFormatter(String title) {
		this.title = title;
	}

	@Override
	public String format(ItemIF item) {
		return String.format("[%s] %s %s", title, item.getTitle(), item.getLink());
	}

	@Override
	public String getColor() {
		return Colors.BLUE;
	}
}
