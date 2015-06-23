package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.game.logic.local.LocalChatLogic;

@Singleton
public class ChatApi {
	private final LocalChatLogic chatLogic;

	@Inject
	public ChatApi(LocalChatLogic chatLogic) {
		this.chatLogic = chatLogic;
	}

	@LuaMethod(name = "Say", global = true)
	public void say(String message) {
		chatLogic.say(message);
	}

	@LuaMethod(name = "Whisper", global = true)
	public void whisper(String name, String message) {
		chatLogic.whisper(name, message);
	}
}
