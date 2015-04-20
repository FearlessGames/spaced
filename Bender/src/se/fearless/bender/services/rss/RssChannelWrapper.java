package se.fearless.bender.services.rss;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;

import java.net.URL;
import java.util.Set;

public class RssChannelWrapper {
	private ChannelIF channel;
	private final StoryHandler storyHandler;

	public RssChannelWrapper(ChannelIF channel, StoryHandler storyHandler) {
		this.channel = channel;
		this.storyHandler = storyHandler;
	}

	public ChannelIF getChannel() {
		return channel;
	}

	public StoryHandler getStoryHandler() {
		return storyHandler;
	}

	public Set<ItemIF> getItems() {
		return channel.getItems();
	}

	public URL getLocation() {
		return channel.getLocation();
	}

	public void publish(ItemIF item) {
		storyHandler.publishStory(item);
	}

	public void updateChannel(ChannelIF newChannel) {
		this.channel = newChannel;
	}
}
