package se.spaced.client.view;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class SlashHandler {

	private final Map<String, Command> commands;

	public SlashHandler() {
		commands = Maps.newTreeMap();
	}

	public void addCommand(String name, Command command) {
		commands.put(name, command);
	}

	private static Pattern commandPattern = Pattern.compile("^(/[a-z]+)[ ]*(.*)$");

	/**
	 * @param text must start with a slash
	 * @return true if it succeeds in performing the slash command, false otherwise
	 */
	public boolean receiveInput(String text) {
		if (!text.startsWith("/")) {
			return true;
		}
		Matcher matcher = commandPattern.matcher(text);
		if (!matcher.matches()) {
			return false;
		}
		String commandName = matcher.group(1);
		String args = matcher.group(2);

		Command command = commands.get(commandName);
		if (command == null) {
			return false;
		}
		command.perform(args);
		return true;
	}

	public abstract static class Command {
		public abstract void perform(String text);

		public abstract String getDescription();
	}

	public Set<Entry<String, Command>> getCommands() {
		return commands.entrySet();
	}
}
