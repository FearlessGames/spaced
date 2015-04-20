package se.spaced.client.bot;

import se.ardortech.Main;
import se.spaced.client.net.smrt.MessageQueue;

public abstract class AbstractBotGameLoop implements Main {
	private final MessageQueue queue;
	protected final BotConnection bot;

	public AbstractBotGameLoop(MessageQueue queue, BotConnection bot) {
		this.queue = queue;
		this.bot = bot;
	}

	@Override
	public void run() {
		bot.connectToServer();
		mainLoop();
	}

	protected void mainLoop() {
		while (bot.isConnected() || bot.isConnecting()) {
			queue.handleIncomingQueue();
			update();
			sleep();
		}
	}

	public abstract void update();

	private void sleep() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
