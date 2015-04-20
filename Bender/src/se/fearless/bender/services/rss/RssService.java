package se.fearless.bender.services.rss;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class RssService {
	private static final int SECONDS_BETWEEN_UPDATES = 20;
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Collection<RssChannelWrapper> channels;

	public RssService(Collection<RssChannelWrapper> channels) {
		this.channels = channels;
	}

	private Timer newsCollector;

	public void start() {

		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				harvestNews();
			}
		};
		newsCollector = new Timer();
		newsCollector.schedule(tt, 0, SECONDS_BETWEEN_UPDATES * 1000);
	}

	private void harvestNews() {
		for (RssChannelWrapper oldChannel : channels) {
			try {
				handleChannel(oldChannel);
			} catch (Exception e) {
				log.error("Failed to read rss for channel " + oldChannel.getLocation(), e);
			}
		}
	}

	private void handleChannel(RssChannelWrapper oldChannel) {
		Set<ItemIF> oldChannelItems = oldChannel.getItems();

		try {
			ChannelIF newChannel = FeedParser.parse(new ChannelBuilder(),
					oldChannel.getLocation());

			for (ItemIF item : newChannel.getItems()) {
				if (!oldChannelItems.contains(item)) {
					oldChannel.publish(item);
				}
			}

			oldChannel.updateChannel(newChannel);
		} catch (IOException e) {
			log.error("Failed to read the rss feed", e);
		} catch (ParseException e) {
			log.error("Failed to parse the rss feed", e);
		}
	}

	public void stop() {
		newsCollector.cancel();
	}

}
