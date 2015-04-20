package se.spaced.server.tools.spawnpattern;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.server.GuiceFactory;
import se.spaced.server.tools.spawnpattern.presenter.GeometryFactory;
import se.spaced.server.tools.spawnpattern.presenter.MobSpawnTemplatePresenter;
import se.spaced.server.tools.spawnpattern.presenter.SpawnAreaFactory;
import se.spaced.server.tools.spawnpattern.presenter.SpawnAreaPresenter;
import se.spaced.server.tools.spawnpattern.presenter.SpawnPatternTemplatePresenter;
import se.spaced.server.tools.spawnpattern.presenter.SpawnPatternToolPresenter;
import se.spaced.server.tools.spawnpattern.view.AddMobTemplateDialogProvider;
import se.spaced.server.tools.spawnpattern.view.AddMobTemplateDialogProviderImpl;
import se.spaced.server.tools.spawnpattern.view.BrainParameterView;
import se.spaced.server.tools.spawnpattern.view.BrainParameterViewImpl;
import se.spaced.server.tools.spawnpattern.view.ErrorView;
import se.spaced.server.tools.spawnpattern.view.IsFrame;
import se.spaced.server.tools.spawnpattern.view.MobSpawnTemplateView;
import se.spaced.server.tools.spawnpattern.view.MobSpawnTemplateViewImpl;
import se.spaced.server.tools.spawnpattern.view.SpawnAreaView;
import se.spaced.server.tools.spawnpattern.view.SpawnAreaViewImpl;
import se.spaced.server.tools.spawnpattern.view.SpawnPatternTemplateView;
import se.spaced.server.tools.spawnpattern.view.SpawnPatternTemplateViewImpl;
import se.spaced.server.tools.spawnpattern.view.SpawnPatternToolView;
import se.spaced.server.tools.spawnpattern.view.SpawnPatternToolViewImpl;
import se.spaced.shared.tools.ClipBoarder;
import se.spaced.shared.tools.ClipBoarderImpl;
import se.spaced.shared.xml.XStreamIO;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import java.lang.reflect.InvocationTargetException;

public class SpawnPatternTool {
	private final SpawnPatternToolPresenter spawnPatternPresenter;

	@Inject
	public SpawnPatternTool(SpawnPatternToolPresenter spawnPatternPresenter) {
		this.spawnPatternPresenter = spawnPatternPresenter;
	}

	private void start() {
		spawnPatternPresenter.show();
		spawnPatternPresenter.loadSpawns();
	}

	public static void main(String[] args) throws InvocationTargetException, InterruptedException, ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
		GuiceFactory guiceFactory = new GuiceFactory();
		guiceFactory.addCustomModule(new AbstractModule() {
			@Override
			protected void configure() {
				bind(SpawnPatternToolPresenter.class).in(Scopes.SINGLETON);
				bind(SpawnPatternTemplatePresenter.class).in(Scopes.SINGLETON);
				bind(SpawnAreaPresenter.class).in(Scopes.SINGLETON);
				bind(MobSpawnTemplatePresenter.class).in(Scopes.SINGLETON);
				bind(BrainParameterView.class).to(BrainParameterViewImpl.class).in(Scopes.SINGLETON);

				bind(SpawnPatternToolView.class).to(SpawnPatternToolViewImpl.class).in(Scopes.SINGLETON);
				bind(SpawnPatternTemplateView.class).to(SpawnPatternTemplateViewImpl.class).in(Scopes.SINGLETON);
				bind(SpawnAreaView.class).to(SpawnAreaViewImpl.class).in(Scopes.SINGLETON);
				bind(MobSpawnTemplateView.class).to(MobSpawnTemplateViewImpl.class).in(Scopes.SINGLETON);

				bind(AddMobTemplateDialogProvider.class).to(AddMobTemplateDialogProviderImpl.class).in(Scopes.SINGLETON);

				bind(ClipBoarder.class).to(ClipBoarderImpl.class).in(Scopes.SINGLETON);
				bind(SpawnAreaFactory.class).in(Scopes.SINGLETON);
				bind(Border.class).toInstance(BorderFactory.createEtchedBorder());


			}

			@Provides
			@Singleton
			ErrorView errorViewProvider(SpawnPatternToolView spawnPatternToolView) {
				// guice fail, would be nice to use
				// bind(ErrorView.class).to(SpawnPatternToolViewImpl.class).in(Scopes.SINGLETON);
				// instead, but that causes two instances of SpawnPatternToolViewImpl
				return (ErrorView) spawnPatternToolView;
			}


			@Provides
			@Singleton
			XStreamIO xStreamProvider(XStream xStream, StreamLocator streamLocator) {
				return new XStreamIO(xStream, streamLocator);
			}

			@Provides
			@Singleton
			IsFrame frameProvider(SpawnPatternToolView spawnPatternToolView) {
				//same guice fail as ErrorView
				return (IsFrame) spawnPatternToolView;
			}

			@Provides
			@Singleton
			GeometryFactory geometryFactoryProvider(XStream xstream) {
				return new GeometryFactory(xstream);
			}


		});

		final Injector injector = guiceFactory.createInjector();

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				SpawnPatternTool instance = injector.getInstance(SpawnPatternTool.class);
				instance.start();
			}
		});


	}

}
