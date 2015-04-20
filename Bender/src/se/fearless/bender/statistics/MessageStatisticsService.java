package se.fearless.bender.statistics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageStatisticsService {
	private AtomicInteger totalNumberOfChannelMessages = new AtomicInteger(0);
	private ConcurrentHashMap<String, AtomicInteger> userMessageStatistics = new ConcurrentHashMap<String, AtomicInteger>();

	public void add(String senderNick, String message) { //TODO: fancy message parsing?
		totalNumberOfChannelMessages.incrementAndGet();
		AtomicInteger numberOfMessagesForUser = userMessageStatistics.get(senderNick);
		if (numberOfMessagesForUser == null) {
			userMessageStatistics.putIfAbsent(senderNick, new AtomicInteger(1));
		} else {
			numberOfMessagesForUser.incrementAndGet();
		}
	}

	public String getMessageStatisticsForUser(String user) {
		AtomicInteger atomicInteger = userMessageStatistics.get(user);
		if (atomicInteger != null) {
			return user + " has sent " + atomicInteger + " message(s)";
		} else {
			return "No messages has been recorded for user: " + user;
		}
	}

	public String getMessageStatisticOverview() {
		StringBuilder sb = new StringBuilder();
		int totalNumberOfMessages = totalNumberOfChannelMessages.get();
		for (String nick : userMessageStatistics.keySet()) {
			int messagesForUser = userMessageStatistics.get(nick).get();
			float percentOfAllMessages = ((float) messagesForUser / (float) totalNumberOfMessages) * 100;
			sb.append(nick).append(':').append(messagesForUser).append('(').append(percentOfAllMessages).append("%)").append(" | ");
		}
		return sb.toString();
	}
}
