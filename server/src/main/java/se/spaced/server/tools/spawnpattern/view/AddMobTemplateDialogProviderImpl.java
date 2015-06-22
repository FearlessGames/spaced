package se.spaced.server.tools.spawnpattern.view;

import com.google.inject.Inject;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.persistence.dao.interfaces.MobTemplateDao;

import javax.swing.BorderFactory;
import java.util.List;

public class AddMobTemplateDialogProviderImpl implements AddMobTemplateDialogProvider {
	private final IsFrame owner;
	private final List<MobTemplate> mobTemplates;


	@Inject
	public AddMobTemplateDialogProviderImpl(IsFrame owner, MobTemplateDao mobTemplateDao) {
		this.owner = owner;
		mobTemplates = mobTemplateDao.findAll();
	}

	@Override
	public void show(final AddMobTemplateDialogCallback addMobTemplateDialogCallback) {
		final AddMobTemplateDialog addMobTemplateDialog = new AddMobTemplateDialog(owner.asFrame(), mobTemplates,
				new BorderBuilder(BorderFactory.createEtchedBorder()));
		AddMobTemplateDialog.Presenter presenter = new AddMobTemplateDialog.Presenter() {
			@Override
			public void createNewSpawnTemplateForMob(MobTemplate mobTemplate) {
				addMobTemplateDialogCallback.createNewSpawnTemplateForMob(mobTemplate);
				addMobTemplateDialog.setVisible(false);
				addMobTemplateDialog.dispose();
			}
		};

		addMobTemplateDialog.setPresenter(presenter);

		addMobTemplateDialog.setVisible(true);
	}
}
