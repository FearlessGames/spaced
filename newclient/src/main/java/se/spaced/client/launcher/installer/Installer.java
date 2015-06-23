package se.spaced.client.launcher.installer;

import com.google.common.base.Supplier;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.lwjgl.opengl.DisplayMode;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.client.launcher.ClientStarter;
import se.spaced.client.launcher.modules.StartupModule;
import se.spaced.client.launcher.modules.WebstartResourceModule;
import se.spaced.client.net.GameServer;
import se.spaced.client.settings.SettingsHandler;
import se.spaced.client.settings.ui.AvailableDisplayModesSupplier;
import se.spaced.client.settings.ui.RenderPropertiesPresenter;
import se.spaced.client.settings.ui.RenderPropertiesView;
import se.spaced.client.settings.ui.RenderPropertiesViewImpl;
import se.spaced.shared.util.QueueRunner;

import javax.swing.UIManager;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Installer implements InstallerView.Presenter {
	private InstallerView installerView;
	private final ResourceUpdater resourceUpdater;
	private int nrOfActions;
	private final Semaphore startSemaphore;
	private final List<GameServer> gameServers;
	private final StreamLocator streamLocator;
	private static final WebstartResourceModule RESOURCE_MODULE = new WebstartResourceModule(System.getProperty("user.home") + File.separator + ".spaced" + File.separator);

	public Installer(List<GameServer> gameServers, StreamLocator streamLocator) {
		this.gameServers = gameServers;
		this.streamLocator = streamLocator;
		resourceUpdater = new ResourceUpdater(new Getter(), new LocalFileUtil());
		startSemaphore = new Semaphore(0);
	}

	private void start() throws InterruptedException, InvocationTargetException {

		EventQueue.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				installerView = new InstallerViewImpl(Installer.this,
						streamLocator.getInputSupplier("installer/noNews.html"));
				installerView.setNewsPage("http://flexo.fearlessgames.se/client/resources/news/news.html");
				installerView.createAndShowUI();
			}
		});


		try {
			ResourceUpdater.IndexCRC32 indexCRC32 = resourceUpdater.validateCRC32();
			switch (indexCRC32) {
				case VALID:
					updateDone();
					break;
				case INVALID:
				case FILE_NOT_FOUND:
					prepareDownload();
					startDownload();
					break;
			}
		} catch (IOException e) {
			installerView.showError("Failed to get crc32", e);
			installerView.close();
		}


		startSemaphore.acquire();

		startSpaced();
	}

	private void startSpaced() {

		ClientStarter clientStarter = new ClientStarter(gameServers, RESOURCE_MODULE);

		clientStarter.start(
				new ClientStarter.StartCallback() {
					@Override
					public void done() {
						installerView.close();
					}
				}
		);
	}


	private void prepareDownload() throws IOException {
		resourceUpdater.prepareDownload();
		nrOfActions = resourceUpdater.getActionList().size();
		installerView.setNrOfActions(nrOfActions);
		installerView.setTotalSize(resourceUpdater.getTotalDownloadSize());
	}


	private void startDownload() {
		final CountDownLatch countDownLatch = new CountDownLatch(nrOfActions);
		resourceUpdater.startAsyncDownload(new CompletedDownloaded(countDownLatch),
				new FailedDownload(),
				new CopyCallback());

		try {
			countDownLatch.await();
			updateDone();
		} catch (InterruptedException e) {
			System.exit(0);
		}
	}

	private void updateDone() {
		installerView.setDone();
	}

	@Override
	public void onStartButton() {
		startSemaphore.release();
	}

	@Override
	public void onExitButton() {
		System.exit(0);
	}

	@Override
	public void onHttpLink(String uri) {
		DesktopBrowser desktopBrowser = new DesktopBrowser();
		desktopBrowser.browseTo(uri);
	}

	@Override
	public void onShowRenderProperties() {
		RenderPropertiesView renderPropertiesView = new RenderPropertiesViewImpl();
		Supplier<List<DisplayMode>> supplier = new AvailableDisplayModesSupplier();
		RenderPropertiesPresenter renderPropertiesPresenter = new RenderPropertiesPresenter(renderPropertiesView, supplier);

		Injector startupInjector = Guice.createInjector(RESOURCE_MODULE, new StartupModule());

		final SettingsHandler settingsHandler = startupInjector.getInstance(SettingsHandler.class);
		renderPropertiesPresenter.setCurrentSettings(settingsHandler.getRendererSettings());
		renderPropertiesPresenter.showDialog();
		settingsHandler.save();
	}


	private class CompletedDownloaded implements QueueRunner.Callback<ResourceUpdater.Action, Void> {
		private final AtomicInteger atomicInteger = new AtomicInteger(0);
		private final CountDownLatch countDownLatch;

		private CompletedDownloaded(CountDownLatch countDownLatch) {
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void afterRunWith(final ResourceUpdater.Action action, Void aVoid, int numberOfJobsRemaining) {
			final int i = atomicInteger.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					installerView.updateProgress(i);
					installerView.updateProgressText(action.toString());
				}
			});
			countDownLatch.countDown();
		}
	}

	private class FailedDownload implements QueueRunner.ExceptionCallback<ResourceUpdater.Action, Void> {
		@Override
		public void onException(final ResourceUpdater.Action action, final Exception exception) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					installerView.showError("Failed to process " + action.partialPath, exception);
					System.exit(1);
				}
			});

		}
	}

	private class CopyCallback implements Getter.CopyCallback {
		final AtomicLong downloadedBytes = new AtomicLong();

		@Override
		public void afterPartialCopy(int len) {
			final long total = downloadedBytes.addAndGet(len);
			if ((total / 4096) % 192 == 0) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						installerView.updateBytesDownloaded(total);
					}
				});
			}
		}
	}


	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		List<GameServer> gameServers = parseGameServers(args);
		StreamLocator streamLocator = RESOURCE_MODULE.getStreamLocator(RESOURCE_MODULE.getResourceRootDir(), RESOURCE_MODULE.getLuaVarsDir());
		Installer installer = new Installer(gameServers, streamLocator);
		installer.start();
	}


	private static List<GameServer> parseGameServers(String[] args) {
		List<GameServer> gameServers = new ArrayList<GameServer>();

		if (args == null) {
			return gameServers;
		}

		for (String arg : args) {
			if (arg.startsWith("--server=")) {
				int beginIndex = arg.indexOf("=");
				int splitIndex = arg.indexOf(":");
				String name = arg.substring(beginIndex + 1, splitIndex);
				String host = arg.substring(splitIndex + 1);
				gameServers.add(new GameServer(name, host));
			}
		}

		return gameServers;

	}


}
