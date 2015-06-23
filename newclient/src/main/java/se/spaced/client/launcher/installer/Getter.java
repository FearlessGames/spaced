package se.spaced.client.launcher.installer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Getter {
	public String getContent(URL url, CopyCallback... callbacks) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = url.openStream();
		copyContent(is, baos, callbacks);
		is.close();
		return new String(baos.toByteArray());
	}

	public String getContent(File file, CopyCallback... callbacks) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(file);
		copyContent(fis, baos, callbacks);
		fis.close();
		return new String(baos.toByteArray());
	}


	public void copyContent(URL url, File file, CopyCallback... callbacks) throws IOException {
		FileOutputStream fos = new FileOutputStream(file, false);
		InputStream is = url.openStream();
		copyContent(is, fos, callbacks);
		is.close();
		fos.close();
	}

	private void copyContent(InputStream is, OutputStream os, CopyCallback... callbacks) throws IOException {
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = is.read(buffer)) >= 0) {
			os.write(buffer, 0, len);
			if (callbacks != null) {
				for (CopyCallback callback : callbacks) {
					callback.afterPartialCopy(len);
				}
			}
		}
	}

	public interface CopyCallback {
		void afterPartialCopy(int len);
	}

}
