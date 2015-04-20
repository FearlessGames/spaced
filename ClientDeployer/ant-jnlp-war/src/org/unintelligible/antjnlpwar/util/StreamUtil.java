/*
 * Created on 18-Apr-2005

 */
package org.unintelligible.antjnlpwar.util;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author ngc
 */
public class StreamUtil {

	/**
	 * Copies a source file. Preserves its date stamp across the copy.
	 *
	 * @param srcFile
	 * @param destFolder
	 * @throws BuildException a wrapped IOException
	 */
	public static void copyFile(File srcFile, File destFolder) {
		try {
			File destFile = new File(destFolder, srcFile.getName());
			if (destFile.exists()) {
				throw new BuildException("Could not copy " + srcFile + " to "
						+ destFolder + " as " + destFile + " already exists");
			}
			// Create channel on the source
			FileChannel srcChannel = null;
			FileChannel destChannel = null;
			try {
				srcChannel = new FileInputStream(srcFile).getChannel();

				// Create channel on the destination
				destChannel = new FileOutputStream(destFile).getChannel();

				// Copy file contents from source to destination
				destChannel.transferFrom(srcChannel, 0, srcChannel.size());
			} finally {
				// Close the channels
				if (srcChannel != null) {
					srcChannel.close();
				}
				if (destChannel != null) {
					destChannel.close();
				}
			}
			//set the time on the dest file to be equal to that of the src file
			destFile.setLastModified((srcFile.lastModified()));
		} catch (IOException e) {
			throw new BuildException("Could not copy " + srcFile + " to "
					+ destFolder + ": " + e, e);
		}

	}

	/**
	 * @param filename
	 * @param iStream
	 * @param destFolder
	 * @throws BuildException a wrapped IOException
	 */
	public static void copyFile(
			String filename, InputStream iStream,
			File destFolder) {
		File destFile = new File(destFolder, filename);
		if (destFile.exists()) {
			throw new BuildException("Could not copy the stream for "
					+ filename + " to " + destFolder + " as " + destFile
					+ " already exists");
		}
		WritableByteChannel channel = null;
		try {
			//		 Obtain a channel
			channel = new FileOutputStream(destFile).getChannel();
			// Create a direct ByteBuffer;
			// see also e158 Creating a ByteBuffer
			ByteBuffer buf = ByteBuffer.allocateDirect(10);

			byte[] bytes = new byte[1024];
			int count = 0;
			int index = 0;
			// Continue writing bytes until there are no more
			while (count >= 0) {
				if (index == count) {
					count = iStream.read(bytes);
					index = 0;
				}
				// Fill ByteBuffer
				while (index < count && buf.hasRemaining()) {
					buf.put(bytes[index++]);
				}

				// Set the limit to the current position and the position to 0
				// making the new bytes visible for write()
				buf.flip();

				// Write the bytes to the channel
				int numWritten = channel.write(buf);

				// Check if all bytes were written
				if (buf.hasRemaining()) {
					// If not all bytes were written, move the unwritten bytes
					// to the beginning and set position just after the last
					// unwritten byte; also set limit to the capacity
					buf.compact();
				} else {
					// Set the position to 0 and the limit to capacity
					buf.clear();
				}
			}

			// Close the file
			channel.close();
		} catch (IOException ioe) {
			throw new BuildException("Could not copy " + filename + " to "
					+ destFolder + ": " + ioe, ioe);
		} finally {
			if (iStream != null) {
				try {
					iStream.close();
				} catch (IOException e) {
				}
			}
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
				}
			}
		}

	}
}