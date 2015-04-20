package se.spaced.client.tools.areacreator;

import com.ardor3d.scenegraph.Node;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.fearlessgames.common.lifetime.LifetimeManagerImpl;
import se.fearlessgames.common.mock.MockUtil;
import se.mockachino.*;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.tools.areacreator.impl.AreaCreatorPresenterImpl;
import se.spaced.client.tools.areacreator.impl.AreaCreatorViewImpl;
import se.spaced.client.tools.areacreator.impl.AreaDisplayHandlerArdorImpl;
import se.spaced.client.tools.areacreator.impl.XmlAreaFormatterImpl;
import se.spaced.client.view.ActivePropProvider;
import se.spaced.client.view.PropSelectionListener;
import se.spaced.shared.tools.ClipBoarder;
import se.spaced.shared.tools.ClipBoarderImpl;
import se.spaced.shared.util.ListenerDispatcher;

import static se.mockachino.Mockachino.*;

public class ScadStandaloneStarter {

	public static void main(String[] args) {
		AreaCreatorView view = new AreaCreatorViewImpl();
		UserCharacter userCharacter = MockUtil.deepMock(UserCharacter.class);
		setupUserCharacterMocks(userCharacter);

		ClipBoarder clipBoarder = new ClipBoarderImpl();
		AreaDisplayHandler areaDisplayHandler = new AreaDisplayHandlerArdorImpl(new Node());
		LifetimeManager lifetimeManager = new LifetimeManagerImpl();
		XmlAreaFormatter xmlAreaFormatter = new XmlAreaFormatterImpl();
		XStream xStream = new XStream(new DomDriver());
		ActivePropProvider activePropProvider = MockUtil.deepMock(ActivePropProvider.class);
		ListenerDispatcher<PropSelectionListener> propSelectionListener = MockUtil.deepMock(ListenerDispatcher.class);

		AreaCreatorPresenter areaCreatorPresenter = new AreaCreatorPresenterImpl(view,
				userCharacter,
				clipBoarder,
				areaDisplayHandler,
				lifetimeManager,
				xmlAreaFormatter,
				xStream,
				activePropProvider, propSelectionListener);

		areaCreatorPresenter.showGui();
	}

	private static void setupUserCharacterMocks(UserCharacter userCharacter) {
		when(userCharacter.getPosition()).thenAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				return new SpacedVector3(Math.random() * 100, Math.random() * 100, Math.random() * 100);
			}
		});


		when(userCharacter.getRotation()).thenAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				return new SpacedRotation(Math.random(),
						Math.random(),
						Math.random(),
						Math.random());
			}
		});
	}
}
