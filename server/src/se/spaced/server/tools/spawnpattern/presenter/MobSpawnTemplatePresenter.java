package se.spaced.server.tools.spawnpattern.presenter;

import com.google.inject.Inject;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.schedule.SpawnScheduleTemplate;
import se.spaced.server.tools.spawnpattern.view.BrainParameterView;
import se.spaced.server.tools.spawnpattern.view.ErrorView;
import se.spaced.server.tools.spawnpattern.view.MobSpawnTemplateView;
import se.spaced.shared.tools.ClipBoarder;
import se.spaced.shared.world.area.Geometry;

public class MobSpawnTemplatePresenter implements MobSpawnTemplateView.Presenter, BrainParameterView.Presenter {
	private final MobSpawnTemplateView view;
	private final BrainParameterView brainParameterView;
	private final ErrorView errorView;
	private final GeometryFactory geometryFactory;
	private final ClipBoarder clipBoarder;
	private MobSpawnTemplate currentMobSpawnTemplate;

	@Inject
	public MobSpawnTemplatePresenter(
			MobSpawnTemplateView view,
			BrainParameterView brainParameterView,
			ErrorView errorView,
			GeometryFactory geometryFactory,
			ClipBoarder clipBoarder) {
		this.view = view;
		this.brainParameterView = brainParameterView;
		this.errorView = errorView;
		this.geometryFactory = geometryFactory;
		this.clipBoarder = clipBoarder;
		view.setPresenter(this);
		brainParameterView.setPresenter(this);
	}

	public void showMobSpawnTemplate(MobSpawnTemplate mobSpawnTemplate) {
		currentMobSpawnTemplate = mobSpawnTemplate;
		if (mobSpawnTemplate != null) {
			SpawnScheduleTemplate spawnScheduleTemplate = mobSpawnTemplate.getSpawnScheduleTemplate();
			view.setSpawnScheduleTemplateData(spawnScheduleTemplate);
			view.setMobTemplateData(mobSpawnTemplate.getMobTemplate());
			brainParameterView.setBrainParameters(mobSpawnTemplate.getMobTemplate().getBrainTemplate().getRequiredParameters(), mobSpawnTemplate.getMobTemplate(),
					mobSpawnTemplate);
		} else {
			view.setSpawnScheduleTemplateData(null);
			view.setMobTemplateData(null);
		}

	}


	@Override
	public void changeMaxWaitTime(int maxWaitTime) {
		if (currentMobSpawnTemplate != null) {
			currentMobSpawnTemplate.getSpawnScheduleTemplate().setMaxWaitTime(maxWaitTime);
		}
	}

	@Override
	public void changeGeometryData() {
		String clipBoard = clipBoarder.getClipBoard();
		if (clipBoard == null || clipBoard.isEmpty()) {
			errorView.showErrorMessage("Failed to change geometry", "Clipboard is empty");
			return;
		}

		try {
			Geometry geometry = geometryFactory.getGeometryFromContent(clipBoard);
			currentMobSpawnTemplate.setGeometryData(geometry);

			// TODO: we want to update the geometry in some way
//			brainParameterView.setBrainParameters(mobSpawnTemplate.getMobTemplate().getBrainTemplate().getRequiredParameters(), mobSpawnTemplate.getMobTemplate(),
//					mobSpawnTemplate);
		} catch (GeometryException e) {
			errorView.showErrorMessage("Failed to change geometry", e.getMessage());
		}


	}

	@Override
	public void changeMinCount(int minCount) {
		if (currentMobSpawnTemplate != null) {
			currentMobSpawnTemplate.getSpawnScheduleTemplate().setMinCount(minCount);
		}
	}

	@Override
	public void changeMaxCount(int maxCount) {
		if (currentMobSpawnTemplate != null) {
			currentMobSpawnTemplate.getSpawnScheduleTemplate().setMaxCount(maxCount);
		}
	}
}
