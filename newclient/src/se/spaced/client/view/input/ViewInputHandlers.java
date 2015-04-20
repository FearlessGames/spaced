package se.spaced.client.view.input;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.game.logic.local.LocalChatLogic;
import se.spaced.client.view.SlashHandler;
import se.spaced.client.view.SlashHandler.Command;

import java.util.Map.Entry;

@Singleton
public class ViewInputHandlers implements InputHandlers {

	private final LocalChatLogic localChatLogic;
	private final SlashHandler textInputHandler;

	@Inject
	public ViewInputHandlers(final LocalChatLogic localChatLogic, final SlashHandler handler) {
		this.localChatLogic = localChatLogic;
		this.textInputHandler = handler;
	}

	@Override
	public void addInputHandlers() {
		textInputHandler.addCommand("/help", new Command() {
			@Override
			public void perform(String text) {
				localChatLogic.systemMessage("Available commands:");
				localChatLogic.systemMessage("--------");
				for (Entry<String, Command> entry : textInputHandler.getCommands()) {
					localChatLogic.systemMessage(entry.getKey() + "  " + entry.getValue().getDescription());
				}
			}

			@Override
			public String getDescription() {
				return "Show this help";
			}
		});
	}

}
