package se.spaced.shared.scheduler;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JobManager {
	private final CopyOnWriteArrayList<Job> jobList;

	public JobManager() {
		jobList = new CopyOnWriteArrayList<Job>();
	}

	public void addJob(Job job) {
		jobList.add(job);
		job.start();
	}

	public void tick() {
		List<Job> finishedJobs = Lists.newArrayList();

		for (Job job : jobList) {
			if (job.hasElapsed()) {
				job.invoke();
				if (!job.isLoop()) {
					finishedJobs.add(job);
				}
			}
		}

		jobList.removeAll(finishedJobs);

	}

	public void removeJob(Job job) {
		jobList.remove(job);
	}
}
