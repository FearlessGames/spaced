package se.spaced.client.view.input;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.ardor.effect.EffectLoader;
import se.spaced.client.view.SlashHandler;

@Singleton
public class ParticleEffectsInputHandler implements InputHandlers {
	private final SlashHandler slashHandler;
	private final EffectLoader effectLoader;

	@Inject
	public ParticleEffectsInputHandler(SlashHandler slashHandler, EffectLoader effectLoader) {
		this.slashHandler = slashHandler;
		this.effectLoader = effectLoader;
	}

	@Override
	public void addInputHandlers() {
		slashHandler.addCommand("/rlfx", new SlashHandler.Command() {
			@Override
			public String getDescription() {
				return "Reloads all particle effects";
			}

			@Override
			public void perform(String text) {
				effectLoader.reset();
			}
		});
	}

}
