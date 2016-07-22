package se.spaced.client.launcher.installer;

import se.spaced.shared.util.MultiThreadedQueueRunner;
import se.spaced.shared.util.QueueRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ResourceUpdater {
	private static final String BASE_PATH = System.getProperty("user.home") + File.separator + ".spaced" + File.separator;
	private static final String RESOURCE_PATH = BASE_PATH + "resources" + File.separator;

	private static final String CRC32_FILE = "index.crc";
	private static final String INDEX_FILE = "index.txt";

	private final Getter getter;
	private final LocalFileUtil fileUtil;
	private final MultiThreadedQueueRunner<Action, Void> queueRunner;
	private final List<Action> actionList;
	private long totalDownloadSize;
	private final String webBase;


	public ResourceUpdater(Getter getter, LocalFileUtil fileUtil, String contentServer) {
		this.getter = getter;
		this.fileUtil = fileUtil;
		totalDownloadSize = 0;
		queueRunner = new MultiThreadedQueueRunner<Action, Void>(4, new ResourceFetcher());
		actionList = new ArrayList<Action>();
		webBase = contentServer;
	}


	public IndexCRC32 validateCRC32() throws IOException {
		if (!fileUtil.exists(RESOURCE_PATH + CRC32_FILE)) {
			return IndexCRC32.FILE_NOT_FOUND;
		}
		String remoteCRC32 = getter.getContent(new URL(webBase + CRC32_FILE));
		String localCRC32 = getter.getContent(new File(RESOURCE_PATH + CRC32_FILE));

		return remoteCRC32.equals(localCRC32) ? IndexCRC32.VALID : IndexCRC32.INVALID;
	}


	private Iterable<Entry> parseIndex(String index) {
		Collection<Entry> entries = new ArrayList<Entry>();
		if (index == null) {
			return entries;
		}
		String[] lines = index.split("\r\n");
		for (String line : lines) {
			if (line.contains(":")) {
				String[] split = line.split(":");
				if (split.length == 2) {
					split = new String[]{split[0], "1024", split[1]};
				}
				entries.add(new Entry(split[0], Long.parseLong(split[1]), split[2]));
			}
		}
		return entries;
	}


	public void prepareDownload() throws IOException {
		Map<String, Entry> localEntries = indexLocalEntries();
		Map<String, Entry> remoteEntries = indexRemoteEntries();

		for (Entry remoteEntry : remoteEntries.values()) {
			if (localEntries.containsKey(remoteEntry.path)) {
				Entry localEntry = localEntries.remove(remoteEntry.path);
				if (!localEntry.crc32.equals(remoteEntry.crc32)) {
					totalDownloadSize += remoteEntry.size;
					actionList.add(new DownloadAction(remoteEntry.path));
				}
			} else {
				totalDownloadSize += remoteEntry.size;
				actionList.add(new DownloadAction(remoteEntry.path));
			}
		}

		actionList.add(new DownloadAction(CRC32_FILE));
		actionList.add(new DownloadAction(INDEX_FILE));

		for (String path : localEntries.keySet()) {
			actionList.add(new DeleteAction(path));
		}
	}

	private Map<String, Entry> indexRemoteEntries() throws IOException {
		Map<String, Entry> remoteEntries = new HashMap<String, Entry>();
		for (Entry entry : parseIndex(getter.getContent(new URL(webBase + INDEX_FILE)))) {
			remoteEntries.put(entry.path, entry);
		}
		return remoteEntries;
	}

	private Map<String, Entry> indexLocalEntries() {
		try {
			Map<String, Entry> localEntries = new HashMap<String, Entry>();
			for (Entry entry : parseIndex(getter.getContent(new File(RESOURCE_PATH + INDEX_FILE)))) {
				localEntries.put(entry.path, entry);
			}
			return localEntries;
		} catch (IOException ioe) {
			return new HashMap<String, Entry>();
		}
	}

	public List<Action> getActionList() {
		return actionList;
	}

	public void startAsyncDownload(
			QueueRunner.Callback<Action, Void> callback,
			QueueRunner.ExceptionCallback<Action, Void> exceptionCallback,
			Getter.CopyCallback copyCallback) {
		for (Action action : actionList) {
			action.setCopyCallback(copyCallback);
			queueRunner.runWith(action, callback, exceptionCallback);
		}

	}

	public long getTotalDownloadSize() {
		return totalDownloadSize;
	}

	private class Entry {
		private final String path;
		private final String crc32;
		private final long size;

		private Entry(String path, long size, String crc32) {
			this.path = path;
			this.crc32 = crc32;
			this.size = size;
		}
	}

	abstract static class Action {
		protected final String partialPath;
		protected Getter.CopyCallback copyCallback;

		protected Action(String partialPath) {
			this.partialPath = partialPath;
		}

		protected abstract void execute();

		public void setCopyCallback(Getter.CopyCallback copyCallback) {
			this.copyCallback = copyCallback;
		}
	}

	class DownloadAction extends Action {
		protected DownloadAction(String partialPath) {
			super(partialPath);
		}

		@Override
		protected void execute() {
			try {
				URL url = new URL(webBase + partialPath.replaceAll(" ", "%20"));
				File file = new File(RESOURCE_PATH + partialPath);
				fileUtil.makeParentPath(file);
				getter.copyContent(url, file, copyCallback);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String toString() {
			return "Downloded " + partialPath;
		}
	}

	class DeleteAction extends Action {
		protected DeleteAction(String partialPath) {
			super(partialPath);
		}

		@Override
		protected void execute() {
			fileUtil.delete(RESOURCE_PATH + partialPath);
		}

		@Override
		public String toString() {
			return "Deleted " + partialPath;
		}
	}

	private static class ResourceFetcher implements QueueRunner.Runner<Action, Void> {
		@Override
		public Void onRunWith(Action action) {
			action.execute();
			return null;
		}
	}

	public enum IndexCRC32 {
		VALID,
		INVALID,
		FILE_NOT_FOUND
	}
}
