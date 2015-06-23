package se.spaced.server.tools.spawnpattern.presenter;

import com.google.inject.Inject;
import org.hibernate.Hibernate;
import org.hibernate.Transaction;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUIDFactory;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.SpawnPatternTemplateDao;
import se.spaced.server.persistence.migrator.ExportServerContent;
import se.spaced.server.tools.spawnpattern.view.SpawnPatternToolView;
import se.spaced.shared.world.area.SinglePoint;

import java.util.*;

public class SpawnPatternToolPresenter implements SpawnPatternToolView.Presenter {
	private final SpawnPatternToolView view;

	private final SpawnPatternTemplatePresenter spawnPatternTemplatePresenter;
	private final SpawnPatternTemplateDao spawnPatternTemplateDao;
	private final TransactionManager transactionManager;
	private final UUIDFactory uuidFactory;
	private List<SpawnPatternTemplate> spawnPatternTemplateList;
	private final ExportServerContent exportServerContent;


	@Inject
	public SpawnPatternToolPresenter(
			SpawnPatternToolView view,
			SpawnPatternTemplatePresenter spawnPatternTemplatePresenter,
			SpawnPatternTemplateDao spawnPatternTemplateDao,
			TransactionManager transactionManager,
			UUIDFactory uuidFactory, ExportServerContent exportServerContent) {
		this.view = view;
		this.spawnPatternTemplatePresenter = spawnPatternTemplatePresenter;
		this.spawnPatternTemplateDao = spawnPatternTemplateDao;
		this.transactionManager = transactionManager;
		this.uuidFactory = uuidFactory;
		this.exportServerContent = exportServerContent;

		view.setPresenter(this);

	}


	@Override
	public void show() {
		view.setVisible(true);

	}

	public void loadSpawns() {
		Transaction transaction = transactionManager.beginTransaction();
		spawnPatternTemplateList = spawnPatternTemplateDao.findAll();
		for (SpawnPatternTemplate spawnPattern : spawnPatternTemplateList) {
			Hibernate.initialize(spawnPattern);
			Hibernate.initialize(spawnPattern.getArea());
			Hibernate.initialize(spawnPattern.getMobspawns());
			view.addSpawnPatternTemplate(spawnPattern);
		}
		transaction.commit();
		if (!spawnPatternTemplateList.isEmpty()) {
			view.selectSpawnPatternTemplate(spawnPatternTemplateList.get(0));
		}
	}

	@Override
	public void selectedSpawnPattern(SpawnPatternTemplate spawnPatternTemplate) {
		spawnPatternTemplatePresenter.showSpawnPattern(spawnPatternTemplate);
	}

	@Override
	public void saveTemplates() {
		Transaction transaction = transactionManager.beginTransaction();
		for (SpawnPatternTemplate spawnPatternTemplate : spawnPatternTemplateList) {
			spawnPatternTemplateDao.persist(spawnPatternTemplate);
		}

		transaction.commit();

	}

	@Override
	public void createNewTemplate(String name) {
		if (name == null || name.isEmpty()) {
			return;
		}

		SpawnPatternTemplate spawnPatternTemplate = new SpawnPatternTemplate(
				uuidFactory.combUUID(),
				new SinglePointSpawnArea(
						uuidFactory.combUUID(),
						new SinglePoint(
								new SpacedVector3(0, 0, 0),
								new SpacedRotation(0, 0, 0, 0))),
				new ArrayList<MobSpawnTemplate>(),
				name);
		view.addSpawnPatternTemplate(spawnPatternTemplate);
		view.selectSpawnPatternTemplate(spawnPatternTemplate);
		spawnPatternTemplateList.add(spawnPatternTemplate);
	}

	@Override
	public void toXml() {
		Transaction transaction = transactionManager.beginTransaction();
		List<SpawnPatternTemplate> all = spawnPatternTemplateDao.findAll();
		Collections.sort(all, new SpawnPatternTemplateComparator());
		String asXml = exportServerContent.genereateXmlFor(all);
		view.showExportedXml("SpawnPatterns exported xml " + new Date(), asXml);

		transaction.commit();
	}

	@Override
	public void removeSelectedSpawnPattern(SpawnPatternTemplate spawnPatternTemplate) {
		spawnPatternTemplateDao.delete(spawnPatternTemplate);
		spawnPatternTemplateList.remove(spawnPatternTemplate);
	}


	private static class SpawnPatternTemplateComparator implements Comparator<SpawnPatternTemplate> {
		@Override
		public int compare(SpawnPatternTemplate o1, SpawnPatternTemplate o2) {
			return o1.getPk().compareTo(o2.getPk());
		}
	}
}
