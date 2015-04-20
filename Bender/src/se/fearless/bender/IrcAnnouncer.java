package se.fearless.bender;

import org.jibble.pircbot.PircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.bender.statistics.MessageStatisticsService;

import java.util.List;

public class IrcAnnouncer extends PircBot {

	private String channelName;
	private CommitLog commitLog;
	private final Logger log = LoggerFactory.getLogger(getClass());
	private MessageStatisticsService messageStatisticsService;
	private String lastChannel;


	public IrcAnnouncer(String userName, String channelName) {
		setName(userName);
		this.channelName = channelName;
	}

	public void announce(String message) {
		sendMessage(channelName, message);
	}


	@Override
	protected void onConnect() {
		super.onConnect();
		if (lastChannel != null) {
			joinChannel(lastChannel);
		}
	}

	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {
		super.onJoin(channel, sender, login, hostname);
		lastChannel = channel;
	}

	@Override
	protected void onDisconnect() {
		while (!isConnected()) {
			try {
				reconnect();
			} catch (Exception ignored) {
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException ie) {
				}
			}
		}

	}


	//TODO: some real command thingiemogizmo soon?
	@Override
	protected void onMessage(String channel, String senderNick, String senderLogin, String senderHostName, String message) {
		if (message.equals("!status")) {
			System.out.println("Got status message");
			return;
		}

		if (message.equals("!chanstats")) {
			sendMessage(channel, "stats for " + channelName);
			sendMessage(channel, messageStatisticsService.getMessageStatisticOverview());
			return;
		}

		if (message.startsWith("!commitlog")) {
			try {
				String[] messageParts = message.split(" ");
				int numberOfLoggedMessages = 5;
				if (messageParts.length > 1) {
					numberOfLoggedMessages = Integer.parseInt(messageParts[1]);
				}

				List<Commit> commits = commitLog.getLast(numberOfLoggedMessages);
				for (Commit c : commits) {
					String outgoing = new StringBuilder("Commit #").append(c.getRevision()).append(" by ").append(c.getAuthor()).append(", Message: ").append(c.getCommitMessage()).toString();
					sendMessage(senderNick, outgoing);
				}
			} catch (Exception e) {
				log.warn("got exception {}", e.getMessage());
				sendMessage(senderNick, "Error in !commitlog: " + e.getMessage());
			}
			return;
		}

		if (message.startsWith("!userstats")) {
			try {
				String user = message.split(" ")[1];
				sendMessage(channel, messageStatisticsService.getMessageStatisticsForUser(user));
			} catch (Exception e) {
				log.warn("got exception {}", e.getMessage());
				sendMessage(senderNick, "Error in !commitlog: " + e.getMessage());
			}
			return;
		}

		if (channel.equals(channelName)) {
			messageStatisticsService.add(senderNick, message);
		}

	}

	public void setCommitLog(CommitLog commitLog) {
		this.commitLog = commitLog;
	}

	public void setMessageStatisticsService(MessageStatisticsService messageStatisticsService) {
		this.messageStatisticsService = messageStatisticsService;
	}
}
