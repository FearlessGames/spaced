package se.spaced.client.launcher.installer;

import org.junit.Before;
import org.junit.Test;
import se.mockachino.matchers.*;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class ResourceUpdaterTest {
	private LocalFileUtil fileUtil;
	private Getter getter;
	private ResourceUpdater resourceUpdater;

	@Before
	public void setup() {
		getter = mock(Getter.class);
		fileUtil = mock(LocalFileUtil.class);

		resourceUpdater = new ResourceUpdater(getter, fileUtil);
	}

	@Test
	public void testValidateCRC32() throws Exception {
		when(fileUtil.exists(Matchers.any(String.class))).thenReturn(false);
		assertEquals(ResourceUpdater.IndexCRC32.FILE_NOT_FOUND, resourceUpdater.validateCRC32());

		when(fileUtil.exists(Matchers.any(String.class))).thenReturn(true);
		when(getter.getContent(Matchers.any(File.class))).thenReturn("0x0000");
		when(getter.getContent(new URL("http://flexo.fearlessgames.se/client/resources/index.crc"))).thenReturn("0x0010");
		assertEquals(ResourceUpdater.IndexCRC32.INVALID, resourceUpdater.validateCRC32());


		when(fileUtil.exists(Matchers.any(String.class))).thenReturn(true);
		when(getter.getContent(Matchers.any(File.class))).thenReturn("0x0011");
		when(getter.getContent(new URL("http://flexo.fearlessgames.se/client/resources/index.crc"))).thenReturn("0x0011");
		assertEquals(ResourceUpdater.IndexCRC32.VALID, resourceUpdater.validateCRC32());

	}

	@Test
	public void testPrepareFullDownload() throws Exception {
		when(getter.getContent(new URL("http://flexo.fearlessgames.se/client/resources/index.txt"))).thenReturn(join(
				REMOTE_INDEX));
		resourceUpdater.prepareDownload();
		assertEquals(REMOTE_INDEX.length + 2, resourceUpdater.getActionList().size());
		for (ResourceUpdater.Action action : resourceUpdater.getActionList()) {
			assertTrue(action instanceof ResourceUpdater.DownloadAction);
		}

	}

	@Test
	public void testPrepareDiffDownload() throws Exception {
		when(getter.getContent(new URL("http://flexo.fearlessgames.se/client/resources/index.txt"))).thenReturn(join(
				REMOTE_INDEX));
		when(getter.getContent(Matchers.any(File.class))).thenReturn(join(LOCAL_INDEX));
		resourceUpdater.prepareDownload();
		assertEquals(6 + 2, resourceUpdater.getActionList().size());
		assertTrue(resourceUpdater.getActionList().get(0) instanceof ResourceUpdater.DownloadAction);
		assertTrue(resourceUpdater.getActionList().get(7) instanceof ResourceUpdater.DeleteAction);
	}

	@Test
	public void testStartAsyncDownload() throws Exception {
		/*
		ResourceUpdater resourceUpdater = new ResourceUpdater(new Getter(), new LocalFileUtil());

		int i = resourceUpdater.prepareFullDownload();
		final CountDownLatch countDownLatch = new CountDownLatch(i);
		System.out.println(i + " files to update");
		resourceUpdater.startAsyncDownload(new MultiThreadedQueueRunner.Callback<ResourceUpdater.Action, Void>() {
			@Override
			public void afterRunWith(ResourceUpdater.Action action, Void aVoid, int numberOfJobsRemaining) {
				System.out.println("Exceuted action for " + action.partialPath);
				countDownLatch.countDown();
			}
		}, new MultiThreadedQueueRunner.ExceptionCallback<ResourceUpdater.Action, Void>() {
			@Override
			public void onException(ResourceUpdater.Action action, Exception exception) {
				System.out.println("Failed to execute action for " + action.partialPath);
				countDownLatch.countDown();
			}
		});
		countDownLatch.await();
		System.out.println("all done!");

		*/
	}

	private String join(String[] strings) {
		StringBuilder sb = new StringBuilder();
		for (String string : strings) {
			sb.append(string).append("\r\n");
		}
		return sb.toString();
	}

	private static final String[] REMOTE_INDEX = {
			"textures/ball.png:d033ed9b",
			"textures/gui/progressbar.png:fa7f8c58",
			"textures/gui/button/GUIButtonEnabledState.png:bb9ba3e6",
			"textures/gui/button/over16.png:3b094199",
			"textures/gui/button/GUIButtonMouseOverState.png:420d99db",
			"textures/gui/button/GUIButtonDownState.png:9f48c0d4",
			"textures/gui/button/transit64.png:617303bb",
			"textures/gui/button/down16.png:849a27b0",
			"textures/gui/button/normal16.png:f406cb6c"
	};

	private static final String[] LOCAL_INDEX = {
			"textures/ball.png:d033ed9b",
			"textures/gui/progressbar.png:CHANGED",
			"textures/gui/button/GUIButtonEnabledState.png:bb9ba3e6",
			"textures/gui/button/over16.png:CHANGED",
			//removed
			//removed
			"textures/gui/button/transit64.png:617303bb",
			"textures/gui/button/down16.png:849a27b0",
			"textures/gui/button/ADDED_LOCALY.png:f406cb6c"
	};
}
