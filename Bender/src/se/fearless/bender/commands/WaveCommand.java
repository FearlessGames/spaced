package se.fearless.bender.commands;

import org.jibble.pircbot.Colors;
import org.simpleframework.http.Request;
import se.fearless.bender.IrcAnnouncer;

import java.io.IOException;

public class WaveCommand implements Command {
	private final IrcAnnouncer ircAnnouncer;
	

	public WaveCommand(IrcAnnouncer ircAnnouncer) {
		this.ircAnnouncer = ircAnnouncer;
	}

	@Override
	public void execute(final Request request) {
		try {
			String event = request.getParameter("event");
			String author = request.getParameter("author");
			String waveTitle = request.getParameter("title");
			String url = request.getParameter("url");
			
			if(event.equals("NEW_WAVE")) {
				announceNewWave(waveTitle, author, url);
			} else if(event.equals("UPDATED_BLIP")) {
				announceUpdatedWave(waveTitle, author, url);
			} else if(event.equals("NEW_BLIP")) {
				announceNewBlip(waveTitle, author, url);
			}
		} catch(IOException e) {
			 e.printStackTrace();
		}
	}

	private void announceNewWave(final String waveTitle, final String author, final String url) {
		announce("New wave: '" + waveTitle + "' by " + author + " - " + url);
	}

	private void announceUpdatedWave(final String waveTitle, final String author, final String url) {
		 announce("Updated wave: '" + waveTitle + "' by " + author + " - " + url);
	}

	private void announceNewBlip(final String waveTitle, final String author, final String url) {
		 announce("New comment to wave: '" + waveTitle + "' by " + author + " - " + url);
	}

	private void announce(final String message) {
		ircAnnouncer.announce(Colors.DARK_GREEN + "[Wave] " + Colors.NORMAL + message);
	}
}
