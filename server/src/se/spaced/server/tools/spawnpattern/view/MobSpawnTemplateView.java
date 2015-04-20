package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.schedule.SpawnScheduleTemplate;

public interface MobSpawnTemplateView extends IsPanel {
	void setPresenter(Presenter presenter);

	void setSpawnScheduleTemplateData(SpawnScheduleTemplate spawnScheduleTemplate);

	void setMobTemplateData(MobTemplate mobTemplate);

	public interface Presenter {

		void changeMaxCount(int maxCount);

		void changeMinCount(int minCount);

		void changeMaxWaitTime(int maxWaitTime);
	}
}
