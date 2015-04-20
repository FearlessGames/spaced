package se.spaced.client.launcher.installer;

import java.io.File;
import java.io.IOException;

public class LocalFileUtil {
	boolean exists(String file) {
		return new File(file).exists();
	}

	public void removeDir(String path) throws IOException {
		removeDir(new File(path));
	}

	public void removeDir(File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		String[] list = file.list();
		for (String s : list) {
			File f = new File(file, s);
			if (f.isDirectory()) {
				removeDir(f);
			}
		}
		file.delete();
	}

	public void mkdirs(String path) {
		new File(path).mkdirs();
	}

	public void delete(String file) {
		new File(file).delete();
	}

	public void makeParentPath(File file) {
		new File(file.getParent()).mkdirs();
	}
}
