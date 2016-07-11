package se.spaced.shared.model.xmo;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.fearless.common.io.FileLocator;
import se.spaced.shared.xml.SharedXStreamRegistry;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIO;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class XmoFileValidator {
	private final String path;
	private final XmlIO xmlIO;
	private StringBuilder errorHolder = new StringBuilder();

	public XmoFileValidator(String path, XmlIO xmlIO) {
		this.path = path;
		this.xmlIO = xmlIO;
	}

	public static void main(String[] args) {
		XStream xStream = new XStream(new DomDriver());
		SharedXStreamRegistry sharedXStreamRegistry = new SharedXStreamRegistry();
		sharedXStreamRegistry.registerDefaultsOn(xStream);

		XmlIO io = new XStreamIO(xStream, new FileLocator(new File(args[0])));

		XmoFileValidator xmoFileValidator = new XmoFileValidator(args[0], io);
		String scanDir = args[1];
		File scanFile = new File(scanDir);
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File file, String s) {
				return s.toLowerCase().endsWith(".xmo");
			}
		};
		FileFilter dirFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		xmoFileValidator.validateFiles(scanFile, filter, dirFilter);

		if (xmoFileValidator.errorHolder.length() != 0) {
			notifyBender(xmoFileValidator.errorHolder.toString());
			throw new RuntimeException(xmoFileValidator.errorHolder.toString());
		}

	}

	private static void notifyBender(String message) {
		String[] lines = message.split("\r\n");
		message = "";
		for (String line : lines) {
			message += line + " ";
		}
		try {
			message = URLEncoder.encode(message, "UTF-8");
			URL benderAdress = new URL("http://localhost:8090/?type=failed&message=" + message);
			benderAdress.openConnection().getInputStream();
		} catch (IOException e) {
			// ignore
		}
	}

	private void validateFiles(File scanFile, FilenameFilter filter, FileFilter dirFilter) {
		for (File child : scanFile.listFiles(filter)) {
			validate(child);
		}
		for (File subDir : scanFile.listFiles(dirFilter)) {
			validateFiles(subDir, filter, dirFilter);
		}
	}

	public void validate(File file) {
		try {
			XmoRoot xmoRoot = xmlIO.load(XmoRoot.class, file.getPath());
			validate(xmoRoot, file.getAbsolutePath());
		} catch (Exception e) {
			errorHolder.append("Failed to unmarshar ").append(file.getAbsolutePath()).append("\r\n");
			errorHolder.append("Error: ").append(e.getMessage()).append("\r\n");
		}
	}

	public void validate(XmoRoot xmoRoot, String xmoFilename) {
		if (xmoRoot.getExtendedMeshObjects() != null) {
			for (ExtendedMeshObject child : xmoRoot.getExtendedMeshObjects()) {
				subValidate(child, xmoFilename);
			}
		}
		if (xmoRoot.getContainerNodes() != null) {
			for (XmoContainerNode xmoMetaNode : xmoRoot.getContainerNodes()) {
				if (xmoMetaNode.getExtendedMeshObjects() != null) {
					for (ExtendedMeshObject extendedMeshObject : xmoMetaNode.getExtendedMeshObjects()) {
						subValidate(extendedMeshObject, xmoFilename);
					}
				}
			}
		}

		if (xmoRoot.getXmoAttachmentPoints() != null) {
			for (AttachmentPointIdentifier api : xmoRoot.getXmoAttachmentPoints().keySet()) {
				XmoAttachmentPoint ap = xmoRoot.getXmoAttachmentPoints().get(api);
				if (ap.getJointName() == null) {
					errorHolder.append("jointPoint for attachmentPoint: ").append(api).append(" is null").append("\r\n");
				}
			}
		}
	}

	private void subValidate(ExtendedMeshObject xmo, String xmoFilename) {
		verifyFile(xmoFilename, xmo.getColladaFile());
		verifyFile(xmoFilename, xmo.getPhysicsFile());
		verifyFile(xmoFilename, xmo.getTextureFile());
		verifyFile(xmoFilename, xmo.getXmoMaterialFile());
	}

	private void verifyFile(String xmoFilename, String fileName) {
		if (fileName == null) {
			return;
		}
		InputStream stream = getClass().getResourceAsStream(path + fileName);
		verify(stream != null, "Could not find refered file: " + path + fileName + " when validating " + xmoFilename);
	}

	private void verify(boolean b, String s) {
		if (!b) {
			errorHolder.append(s).append("\r\n");
		}
	}
}
