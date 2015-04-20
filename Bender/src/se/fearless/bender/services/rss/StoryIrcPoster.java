package se.fearless.bender.services.rss;

import de.nava.informa.core.ItemIF;
import se.fearless.bender.IrcAnnouncer;

public class StoryIrcPoster implements StoryHandler {

	private final IrcAnnouncer ircAnnouncer;
	private final RssItemFormatter formatter;

	public StoryIrcPoster(IrcAnnouncer ircAnnouncer, RssItemFormatter formatter) {
		this.ircAnnouncer = ircAnnouncer;
		this.formatter = formatter;
	}

	@Override
	public void publishStory(ItemIF item) {
		String message = formatter.format(item);
		ircAnnouncer.announce(formatter.getColor() +  message);
	}
}
