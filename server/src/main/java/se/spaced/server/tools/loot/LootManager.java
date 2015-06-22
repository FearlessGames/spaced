package se.spaced.server.tools.loot;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import se.spaced.server.GuiceFactory;
import se.spaced.server.tools.loot.edit.EditPresenter;
import se.spaced.server.tools.loot.edit.EditView;
import se.spaced.server.tools.loot.edit.EditViewImpl;
import se.spaced.server.tools.loot.simulator.LootSimView;
import se.spaced.server.tools.loot.simulator.LootSimViewImpl;
import se.spaced.server.tools.loot.simulator.LootSimulatorPresenter;
import se.spaced.server.tools.loot.xml.LootXmlPresenter;
import se.spaced.server.tools.loot.xml.LootXmlView;
import se.spaced.server.tools.loot.xml.LootXmlViewImpl;

public class LootManager {
	private final MainView mainView;
	private final LootSimulatorPresenter lootSimulatorPresenter;
	private final LootXmlPresenter lootXmlPresenter;
	private final EditPresenter editPresenter;

	@Inject
	public LootManager(
			MainView mainView,
			LootSimulatorPresenter lootSimulatorPresenter,
			LootXmlPresenter lootXmlPresenter,
			EditPresenter editPresenter) {
		this.mainView = mainView;
		this.lootSimulatorPresenter = lootSimulatorPresenter;
		this.lootXmlPresenter = lootXmlPresenter;
		this.editPresenter = editPresenter;
	}

	public static void main(String[] args) {


		GuiceFactory guiceFactory = new GuiceFactory();

		guiceFactory.addCustomModule(new AbstractModule() {
			@Override
			protected void configure() {
				bind(MainView.class).to(MainViewImpl.class).in(Singleton.class);
				bind(LootSimView.class).to(LootSimViewImpl.class).in(Singleton.class);
				bind(LootXmlView.class).to(LootXmlViewImpl.class).in(Singleton.class);
				bind(EditView.class).to(EditViewImpl.class).in(Singleton.class);
			}
		});

		final Injector injector = guiceFactory.createInjector();

		LootManager lootManager = injector.getInstance(LootManager.class);
		lootManager.start();
	}

	private void start() {
		lootSimulatorPresenter.addTabOn(mainView);
		editPresenter.addTabOn(mainView);
		lootXmlPresenter.addTabOn(mainView);
		mainView.createAndShowUI();
	}
}
