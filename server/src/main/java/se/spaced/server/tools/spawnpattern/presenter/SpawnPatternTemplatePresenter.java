package se.spaced.server.tools.spawnpattern.presenter;

import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.model.spawn.schedule.SpawnScheduleTemplate;
import se.spaced.server.tools.spawnpattern.view.AddMobTemplateDialogProvider;
import se.spaced.server.tools.spawnpattern.view.SpawnPatternTemplateView;

import javax.inject.Inject;

public class SpawnPatternTemplatePresenter implements SpawnPatternTemplateView.Presenter {
	private final SpawnPatternTemplateView view;
	private final SpawnAreaPresenter spawnAreaPresenter;
	private final MobSpawnTemplatePresenter mobSpawnTemplatePresenter;
	private final UUIDFactory uuidFactory;
	private final AddMobTemplateDialogProvider addMobTemplateDialogProvider;
	private SpawnPatternTemplate currentSpawnPatternTemplate;

	@Inject
	public SpawnPatternTemplatePresenter(
			SpawnPatternTemplateView view,
			SpawnAreaPresenter spawnAreaPresenter,
			MobSpawnTemplatePresenter mobSpawnTemplatePresenter,
			UUIDFactory uuidFactory,
			AddMobTemplateDialogProvider addMobTemplateDialogProvider) {
		this.view = view;
		this.spawnAreaPresenter = spawnAreaPresenter;
		this.mobSpawnTemplatePresenter = mobSpawnTemplatePresenter;
		this.uuidFactory = uuidFactory;
		this.addMobTemplateDialogProvider = addMobTemplateDialogProvider;
		view.setPresenter(this);

	}

	public void showSpawnPattern(SpawnPatternTemplate spawnPatternTemplate) {
		currentSpawnPatternTemplate = spawnPatternTemplate;
		view.setUUID(spawnPatternTemplate.getPk().toString());
		view.setPatternName(spawnPatternTemplate.getName());
		spawnAreaPresenter.setCurrentTemplate(spawnPatternTemplate);
		view.selectMobSpawnTemplate(null);

		view.setMobSpawns(currentSpawnPatternTemplate.getMobspawns());
		if (!currentSpawnPatternTemplate.getMobspawns().isEmpty()) {
			view.selectMobSpawnTemplate(currentSpawnPatternTemplate.getMobspawns().iterator().next());
		}
	}

	@Override
	public void selectedMobSpawnTemplate(MobSpawnTemplate mobSpawnTemplate) {
		mobSpawnTemplatePresenter.showMobSpawnTemplate(mobSpawnTemplate);
	}


	@Override
	public void removeSpawnTemplate(MobSpawnTemplate mobSpawnTemplate) {
		currentSpawnPatternTemplate.getMobspawns().remove(mobSpawnTemplate);
		view.setMobSpawns(currentSpawnPatternTemplate.getMobspawns());
		selectedMobSpawnTemplate(null);
	}

	@Override
	public void changeNameOnCurrentPattern(String name) {
		currentSpawnPatternTemplate.setName(name);
	}

	@Override
	public void addMobSpawnTemplate() {
		addMobTemplateDialogProvider.show(new AddMobTemplateDialogProvider.AddMobTemplateDialogCallback() {
			@Override
			public void createNewSpawnTemplateForMob(MobTemplate mobTemplate) {
				MobSpawnTemplate mobSpawnTemplate = new MobSpawnTemplate(uuidFactory.combUUID(),
						mobTemplate,
						new SpawnScheduleTemplate(uuidFactory.combUUID(), 0, 0, 0),
						null);
				currentSpawnPatternTemplate.getMobspawns().add(mobSpawnTemplate);

				view.setMobSpawns(currentSpawnPatternTemplate.getMobspawns());
				view.selectMobSpawnTemplate(mobSpawnTemplate);
				//mobSpawnTemplatePresenter.showMobSpawnTemplate(mobSpawnTemplate);
			}
		});
	}
}
