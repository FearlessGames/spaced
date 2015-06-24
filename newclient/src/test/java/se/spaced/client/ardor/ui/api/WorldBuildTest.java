package se.spaced.client.ardor.ui.api;

import org.junit.Test;
import se.fearless.common.lifetime.LifetimeManagerImpl;
import se.spaced.client.ardor.effect.EffectLoader;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.resources.zone.ScenegraphService;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.resources.zone.ZoneDebugShapeService;
import se.spaced.client.resources.zone.ZoneXmlWriter;
import se.spaced.client.tools.spd.SpdPresenter;
import se.spaced.client.tools.spd.SpdViewImpl;
import se.spaced.client.view.PropSelectionListener;
import se.spaced.client.view.PropViewImpl;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.resources.XmoMaterialManager;
import se.spaced.shared.util.ListenerDispatcher;

import javax.swing.*;

import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.stubReturn;


public class WorldBuildTest {

	@Test
	public void retardedJunit() {
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				UserCharacter userChar = mock(UserCharacter.class);
				stubReturn(new PositionalData()).on(userChar).getPositionalData();
				EffectLoader effectLoader = null;
				XmoMaterialManager materialManager = null;
				ZoneDebugShapeService zoneDebugShapeService = null;
				ZoneActivationService zoneActivationService = null;
				ScenegraphService scenegraphService = null;
				ZoneXmlWriter zoneXmlWriter = null;
				PropViewImpl propView = mock(PropViewImpl.class);
				SpdPresenter presenter = new SpdPresenter(new SpdViewImpl(propView),
						userChar,
						ListenerDispatcher.create(PropSelectionListener.class),
						new LifetimeManagerImpl(),
						zoneActivationService,
						scenegraphService,
						zoneXmlWriter,
						null, null, null, propView);
				presenter.show();
			}
		});
	}

}
