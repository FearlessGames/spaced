package se.fearless.bender.services.rss;

import de.nava.informa.core.ItemIF;

public interface RssItemFormatter {
	String format(ItemIF item);

	String getColor();
}
