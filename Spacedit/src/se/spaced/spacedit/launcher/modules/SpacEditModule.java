package se.spaced.spacedit.launcher.modules;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.fearlessgames.common.io.FileStreamLocator;
import se.fearlessgames.common.io.MultiStreamLocator;
import se.fearlessgames.common.io.StreamLocator;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.fearlessgames.common.lifetime.LifetimeManagerImpl;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.shared.resources.XmoMaterialManager;
import se.spaced.shared.resources.XmoMaterialManagerImpl;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIO;
import se.spaced.spacedit.ardor.FixedFpsFrameUpdater;
import se.spaced.spacedit.ardor.FrameUpdater;
import se.spaced.spacedit.state.StateManager;
import se.spaced.spacedit.state.StateManagerImpl;
import se.spaced.spacedit.ui.presenter.display.ArdorPresenter;
import se.spaced.spacedit.ui.presenter.display.ArdorPresenterImpl;
import se.spaced.spacedit.ui.presenter.display.ArdorSceneHandler;
import se.spaced.spacedit.ui.presenter.display.ArdorSceneHandlerImpl;
import se.spaced.spacedit.ui.presenter.filechooser.ColladaFileChooserPresenter;
import se.spaced.spacedit.ui.presenter.filechooser.ColladaFileChooserPresenterImpl;
import se.spaced.spacedit.ui.presenter.filechooser.XMOFileManagerPresenter;
import se.spaced.spacedit.ui.presenter.filechooser.XMOFileManagerPresenterImpl;
import se.spaced.spacedit.ui.presenter.mainframe.MainFramePresenter;
import se.spaced.spacedit.ui.presenter.mainframe.MainFramePresenterImpl;
import se.spaced.spacedit.ui.presenter.propertyeditor.PropertyListPresenter;
import se.spaced.spacedit.ui.presenter.propertyeditor.PropertyListPresenterImpl;
import se.spaced.spacedit.ui.presenter.toolbar.ToolBarPresenter;
import se.spaced.spacedit.ui.presenter.toolbar.ToolBarPresenterImpl;
import se.spaced.spacedit.ui.presenter.xmotree.XmoTreePresenter;
import se.spaced.spacedit.ui.presenter.xmotree.XmoTreePresenterImpl;
import se.spaced.spacedit.ui.tdi.TdiChildToolbar;
import se.spaced.spacedit.ui.tdi.TdiChildWindow;
import se.spaced.spacedit.ui.view.display.ArdorView;
import se.spaced.spacedit.ui.view.display.ArdorViewSwingImpl;
import se.spaced.spacedit.ui.view.filechooser.FileChooserView;
import se.spaced.spacedit.ui.view.filechooser.FileChooserViewSwingImpl;
import se.spaced.spacedit.ui.view.frame.MainView;
import se.spaced.spacedit.ui.view.frame.MainViewSwingImpl;
import se.spaced.spacedit.ui.view.propertylist.PropertyListView;
import se.spaced.spacedit.ui.view.propertylist.PropertyListViewImpl;
import se.spaced.spacedit.ui.view.toolbar.ToolBarView;
import se.spaced.spacedit.ui.view.toolbar.ToolBarViewSwingImpl;
import se.spaced.spacedit.ui.view.utils.swing.SwingThread;
import se.spaced.spacedit.ui.view.utils.swing.SwingThreadInterceptor;
import se.spaced.spacedit.ui.view.xmotree.XmoTreeView;
import se.spaced.spacedit.ui.view.xmotree.XmoTreeViewImpl;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoContainerNodeMenuView;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoMetaNodeMenuView;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoRootMenuView;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoXmoMenuView;
import se.spaced.spacedit.ui.view.xmotree.menus.impl.XmoContainerNodeMenuImpl;
import se.spaced.spacedit.ui.view.xmotree.menus.impl.XmoMetaNodeMenuImpl;
import se.spaced.spacedit.ui.view.xmotree.menus.impl.XmoRootMenuImpl;
import se.spaced.spacedit.ui.view.xmotree.menus.impl.XmoXmoMenuImpl;
import se.spaced.spacedit.xmo.XmoCreator;
import se.spaced.spacedit.xmo.XmoCreatorImpl;
import se.spaced.spacedit.xmo.XmoManager;
import se.spaced.spacedit.xmo.XmoManagerImpl;
import se.spaced.spacedit.xmo.model.listeners.ExtendedMeshObjectPropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoContainerNodePropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoMetaNodePropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoRootPropertyListener;

import java.io.File;

public class SpacEditModule implements Module {
	@Override
	public void configure(Binder binder) {

		binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(SwingThread.class), new SwingThreadInterceptor());

		binder.bind(ArdorView.class).to(ArdorViewSwingImpl.class).in(Scopes.SINGLETON);
		binder.bind(MainFramePresenter.class).to(MainFramePresenterImpl.class).in(Scopes.SINGLETON);
		binder.bind(MainView.class).to(MainViewSwingImpl.class).in(Scopes.SINGLETON);
		binder.bind(ArdorPresenter.class).to(ArdorPresenterImpl.class).in(Scopes.SINGLETON);
		binder.bind(ToolBarPresenter.class).to(ToolBarPresenterImpl.class).in(Scopes.SINGLETON);
		binder.bind(ToolBarView.class).to(ToolBarViewSwingImpl.class).in(Scopes.SINGLETON);
		binder.bind(ColladaFileChooserPresenter.class).to(ColladaFileChooserPresenterImpl.class).in(Scopes.SINGLETON);
		binder.bind(FileChooserView.class).to(FileChooserViewSwingImpl.class); //not a singleton

		//testChangingXMOWillTriggerSave tdi
		binder.bind(XmoTreePresenter.class).to(XmoTreePresenterImpl.class).in(Scopes.SINGLETON);
		binder.bind(PropertyListPresenter.class).to(PropertyListPresenterImpl.class).in(Scopes.SINGLETON);
		binder.bind(TdiChildWindow.class).annotatedWith(Names.named("displayTdiChildWindow")).to(ArdorViewSwingImpl.class);
		binder.bind(TdiChildWindow.class).annotatedWith(Names.named("classTreeTdiChildWindow")).to(XmoTreeViewImpl.class);
		binder.bind(TdiChildWindow.class).annotatedWith(Names.named("propertyEditorTdiChildWindow")).to(PropertyListViewImpl.class);
		binder.bind(TdiChildToolbar.class).annotatedWith(Names.named("mainToolbar")).to(ToolBarViewSwingImpl.class);
		binder.bind(XmoTreeView.class).to(XmoTreeViewImpl.class).in(Scopes.SINGLETON);
		binder.bind(PropertyListView.class).to(PropertyListViewImpl.class).in(Scopes.SINGLETON);


		binder.bind(XmoRootMenuView.class).to(XmoRootMenuImpl.class).in(Scopes.SINGLETON);
		binder.bind(XmoXmoMenuView.class).to(XmoXmoMenuImpl.class).in(Scopes.SINGLETON);
		binder.bind(XmoContainerNodeMenuView.class).to(XmoContainerNodeMenuImpl.class).in(Scopes.SINGLETON);
		binder.bind(XmoMetaNodeMenuView.class).to(XmoMetaNodeMenuImpl.class).in(Scopes.SINGLETON);

		binder.bind(FrameUpdater.class).to(FixedFpsFrameUpdater.class).in(Scopes.SINGLETON);
		binder.bind(TimeProvider.class).to(SystemTimeProvider.class).in(Scopes.SINGLETON);


		binder.bind(String.class).annotatedWith(Names.named("colladaExtension")).toInstance("dae");
		binder.bind(String.class).annotatedWith(Names.named("colladaDir")).toInstance(System.getProperty("user.dir") + "/resources/");

		//xmo-creation
		binder.bind(String.class).annotatedWith(Names.named("xmoDir")).toInstance(System.getProperty("user.dir") + "/resources/");
		binder.bind(String.class).annotatedWith(Names.named("xmoExtension")).toInstance("xmo");
		binder.bind(XmoManager.class).to(XmoManagerImpl.class).in(Scopes.SINGLETON);
		binder.bind(XMOFileManagerPresenter.class).to(XMOFileManagerPresenterImpl.class);

		//colada
		binder.bind(ArdorSceneHandler.class).to(ArdorSceneHandlerImpl.class).in(Scopes.SINGLETON);

		//resources
		binder.bind(StreamLocator.class).to(MultiStreamLocator.class).in(Scopes.SINGLETON);

		//states
		binder.bind(StateManager.class).to(StateManagerImpl.class).in(Scopes.SINGLETON);

		//propertylistener
		binder.bind(ExtendedMeshObjectPropertyListener.class).to(ArdorSceneHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(XmoRootPropertyListener.class).to(ArdorSceneHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(XmoMetaNodePropertyListener.class).to(ArdorSceneHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(XmoContainerNodePropertyListener.class).to(ArdorSceneHandlerImpl.class).in(Scopes.SINGLETON);

		binder.bind(XmoCreator.class).to(XmoCreatorImpl.class).in(Scopes.SINGLETON);
		binder.bind(LifetimeManager.class).to(LifetimeManagerImpl.class);
		binder.bind(XmoMaterialManager.class).to(XmoMaterialManagerImpl.class).in(Scopes.SINGLETON);

	}

	@Provides
	@Singleton
	public XStream getXStream() {
		return new XStream(new DomDriver());
	}


	@Provides
	@Singleton
	public XmlIO getXmlIo(XStream xStream, StreamLocator streamLocator) {
		return new XStreamIO(xStream, streamLocator);
	}

	@Provides
	@Singleton
	public StreamLocator getStreamLocator() {
		return new FileStreamLocator(new File(System.getProperty("user.dir") + "/resources"));
	}

	public Injector getInjector() {
		return Guice.createInjector(this);
	}
}
