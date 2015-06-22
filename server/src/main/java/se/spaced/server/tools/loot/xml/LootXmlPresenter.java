package se.spaced.server.tools.loot.xml;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.hibernate.Transaction;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.migrator.ExportServerContent;
import se.spaced.server.tools.loot.MainView;
import se.spaced.server.tools.loot.PersistedLootService;
import se.spaced.server.tools.loot.Presenter;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class LootXmlPresenter implements LootXmlView.Presenter, Presenter {
	private final LootXmlView view;
	private final PersistedLootService lootService;
	private final ExportServerContent exportServerContent;
	private final TransactionManager transactionManager;

	@Inject
	public LootXmlPresenter(
			LootXmlView view,
			PersistedLootService lootService,
			TransactionManager transactionManager, ExportServerContent exportServerContent) {
		this.view = view;

		this.lootService = lootService;
		this.transactionManager = transactionManager;
		this.exportServerContent = exportServerContent;
		view.setPresenter(this);
	}

	@Override
	public void onCreateAndShowXml() {
		Transaction transaction = transactionManager.beginTransaction();
		List<PersistableLootTemplate> templates = new ArrayList<PersistableLootTemplate>(lootService.getSortedLootTemplates());

		String xml = exportServerContent.genereateXmlFor(templates);
		view.setXml(xml);
		transaction.commit();
	}

	@Override
	public void addTabOn(MainView mainView) {
		mainView.addTabPanel("Create XML", view.getPanel());
	}


}
