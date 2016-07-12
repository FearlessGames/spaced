package se.spaced.client.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;

public class FileUtil {
	public long copyAndCalcChecksum(File sourceFile, File targetFile) throws IOException {

		CheckedInputStream cis = new CheckedInputStream(new FileInputStream(sourceFile), new CRC32());
		FileOutputStream fos = new FileOutputStream(targetFile, false);

		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = cis.read(buffer)) >= 0) {
			fos.write(buffer, 0, len);
		}

		Checksum checksum = cis.getChecksum();
		cis.close();
		fos.close();
		return checksum.getValue();
	}

	public long calcChecksum(File file) throws IOException {
		CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new CRC32());
		byte[] buffer = new byte[4096];
		while (cis.read(buffer) >= 0) {
		}

		Checksum checksum = cis.getChecksum();
		cis.close();
		return checksum.getValue();
	}


	public String getFileExtension(File file) {
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex > 0 && dotIndex < fileName.length()) ? fileName.substring(dotIndex + 1) : null;
	}


	public long writeCalcChecksum(byte[] content, File targetFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(targetFile, false);
		CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
		cos.write(content);
		Checksum checksum = cos.getChecksum();
		cos.close();
		fos.close();
		return checksum.getValue();
	}
}