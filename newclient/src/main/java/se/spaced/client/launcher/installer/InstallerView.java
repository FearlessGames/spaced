package se.spaced.client.launcher.installer;

public interface InstallerView {
	void createAndShowUI();

	void close();

	void showError(String message, Exception e);

	void setNrOfActions(int i);

	void updateProgress(int i);

	void updateProgressText(String text);

	void updateBytesDownloaded(long total);

	void setTotalSize(long totalDownloadSize);

	void setNewsPage(String url);

	void setDone();

	interface Presenter {

		void onStartButton();

		void onExitButton();

		void onHttpLink(String uri);

		void onShowRenderProperties();
	}
}
