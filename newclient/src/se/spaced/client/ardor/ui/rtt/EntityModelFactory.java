package se.spaced.client.ardor.ui.rtt;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardorgui.components.rtt.Rtt;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.ardor.entity.AuraVisualiser;
import se.spaced.client.model.animation.AnimationClipCache;
import se.spaced.shared.model.xmo.XmoEntityFactory;

@Singleton
public class EntityModelFactory {
	private final XmoEntityFactory xmoEntityFactory;
	private final AnimationClipCache animationCache;
	private final AuraVisualiser auraVisualiser;

	@Inject
	public EntityModelFactory(
			final XmoEntityFactory xmoEntityFactory,
			final AnimationClipCache animationCache, AuraVisualiser auraVisualiser) {
		this.xmoEntityFactory = xmoEntityFactory;
		this.animationCache = animationCache;
		this.auraVisualiser = auraVisualiser;
	}

	@LuaMethod(name = "CreateEntityModelRtt", global = true)
	public EntityModel createEntityModelRtt(final String xmoFile, Rtt rtt) {
		EntityModel entityModel = new EntityModel(rtt, xmoEntityFactory, animationCache, auraVisualiser);
		entityModel.load(xmoFile);
		return entityModel;
	}
}
