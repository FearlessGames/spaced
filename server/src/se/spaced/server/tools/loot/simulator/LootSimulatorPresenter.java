package se.spaced.server.tools.loot.simulator;

import com.google.inject.Inject;
import org.apache.commons.math.stat.StatUtils;
import org.hibernate.Transaction;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.LootTemplateDao;
import se.spaced.server.tools.loot.MainView;
import se.spaced.server.tools.loot.PersistedLootService;
import se.spaced.server.tools.loot.Presenter;

import javax.inject.Singleton;
import javax.swing.JTextArea;
import java.util.List;
import java.util.SortedSet;

@Singleton
public class LootSimulatorPresenter implements LootSimView.Presenter, Presenter {
	private final LootSimView view;
	private final TransactionManager transactionManager;
	private final LootTemplateDao lootTemplateDao;
	private final PersistedLootService persistedLootService;
	private static final int TIMES = 100000;

	@Inject
	public LootSimulatorPresenter(
			LootSimView view,
			TransactionManager transactionManager,
			LootTemplateDao lootTemplateDao,
			PersistedLootService persistedLootService) {
		this.view = view;
		this.transactionManager = transactionManager;
		this.lootTemplateDao = lootTemplateDao;
		this.persistedLootService = persistedLootService;
		view.setPresenter(this);
	}


	@Override
	public void addTabOn(MainView mainView) {
		populateTemplates();
		mainView.addTabPanel("Loot sim", view.getPanel());
	}

	private void populateTemplates() {
		Transaction tx = transactionManager.beginTransaction();
		SortedSet<PersistableLootTemplate> lootTemplates = persistedLootService.getSortedLootTemplates();
		view.setTemplates(lootTemplates.toArray(new PersistableLootTemplate[lootTemplates.size()]));
		tx.commit();
	}

	@Override
	public void onSearch(final PersistableLootTemplate selectedTemplate) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Transaction transaction = transactionManager.beginTransaction();
				PersistableLootTemplate selectedLootTemplate = lootTemplateDao.findByPk(selectedTemplate.getPk());
				LootSimulator sim = new LootSimulator(selectedLootTemplate);
				sim.simulate(TIMES);
				transaction.commit();
				printReport(selectedLootTemplate, sim);
			}
		}).start();

	}

	@Override
	public void onReload() {
		populateTemplates();
	}

	private synchronized void printReport(PersistableLootTemplate selectedLootTemplate, LootSimulator sim) {
		JTextArea textArea = view.getTextArea();
		List<LootResult> lootResult = sim.getItems();
		textArea.setText(String.format("Running %d times with loot template %s\n\n",
				TIMES,
				selectedLootTemplate.toString()));

		for (LootResult result : lootResult) {
			double percentage = (double) (100 * result.getCount()) / TIMES;
			textArea.append(String.format("%.2f%%\t%s\t%s\n",
					percentage,
					result.getItem().getName(),
					result.getItem().getPk()));
		}
		textArea.append("\n");
		textArea.append("# looted items:\n");
		textArea.append("Min: " + sim.getMinItems() + "\n");
		textArea.append("Max: " + sim.getMaxItems() + "\n");
		double mean = StatUtils.mean(sim.getLootAmounts());
		double stdDev = Math.sqrt(StatUtils.variance(sim.getLootAmounts(), mean));
		textArea.append("μ: " + mean + "\n" + "σ: " + stdDev + "\n");

		double q1 = StatUtils.percentile(sim.getLootAmounts(), 25);
		double q2 = StatUtils.percentile(sim.getLootAmounts(), 50);
		double q3 = StatUtils.percentile(sim.getLootAmounts(), 75);
		textArea.append("\nQ1: " + q1);
		textArea.append("\nQ2: " + q2);
		textArea.append("\nQ3: " + q3);
	}
}
