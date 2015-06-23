package se.spaced.client.launcher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import se.spaced.client.core.MainTickerService;
import se.spaced.client.core.MainTickerServiceImpl;
import se.spaced.client.game.logic.implementations.chat.ChatLogicImpl;
import se.spaced.client.game.logic.implementations.login.LocalLoginLogicImpl;
import se.spaced.client.game.logic.local.LocalChatLogic;
import se.spaced.client.game.logic.local.LocalLoginLogic;
import se.spaced.client.game.logic.remote.RemoteChatLogic;
import se.spaced.client.model.ClientAuraService;
import se.spaced.client.model.ClientAuraServiceImpl;
import se.spaced.client.view.entity.EntityEffectDirectory;

public final class GameLogicModule extends AbstractModule {

	@Override
	public void configure() {
		bind(LocalChatLogic.class).to(ChatLogicImpl.class).in(Scopes.SINGLETON);
		bind(LocalLoginLogic.class).to(LocalLoginLogicImpl.class).in(Scopes.SINGLETON);

		bind(RemoteChatLogic.class).to(ChatLogicImpl.class).in(Scopes.SINGLETON);

		bind(ChatLogicImpl.class).in(Singleton.class);
		bind(LocalLoginLogicImpl.class).in(Singleton.class);

		bind(MainTickerService.class).to(MainTickerServiceImpl.class).in(Scopes.SINGLETON);

		bind(EntityEffectDirectory.class).in(Scopes.SINGLETON);
		bind(ClientAuraService.class).to(ClientAuraServiceImpl.class);

	}
}
