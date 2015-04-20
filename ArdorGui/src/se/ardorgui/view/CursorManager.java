package se.ardorgui.view;

import com.ardor3d.image.Image;
import com.ardor3d.image.Texture;
import com.ardor3d.util.geom.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.view.util.TaskQueue;
import se.ardorgui.view.util.VisualiserCallback;

import java.awt.Insets;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class CursorManager {
	private static final Logger log = LoggerFactory.getLogger(CursorManager.class);
	private static final Map<String, Cursor> loadedCursors = new ConcurrentHashMap<String, Cursor>();

	//TODO change texureInsets to cursorOffsets
	public static void setCursor(Texture texture, Insets textureInsets) {
		Mouse.setGrabbed(false);
		Cursor cursor = null;

		// TODO: does this work?!
		String name = texture.getTextureKey().toString();
		//String name = texture.getTextureKey().getLocation().toString();
		if (loadedCursors.containsKey(name)) {
			cursor = loadedCursors.get(name);
		} else {
			Image image = texture.getImage();
			IntBuffer imageData = image.getData(0).asIntBuffer();
			IntBuffer imageDataCopy = BufferUtils.createIntBuffer(imageData.remaining());
			if(textureInsets == null) {
				textureInsets = new Insets(0, 0, 0, 0);
			}
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int pixel = imageData.get(y * image.getWidth() + x);
					int a = (pixel >> 24) & 0xff;
					if (a < 0x7f) {
						a = 0x00;
					} else {
						a = 0xff;
					}
					int blue = (pixel >> 16) & 0xff;
					int green = (pixel >> 8) & 0xff;
					int red = (pixel) & 0xff;

					// hack to avoid lwjgl/windows bug with a=0x00, all other = 0xff, which means that the alpha becomes XOR
					if (a == 0x00 && blue == 0xff && green == 0xff && red == 0xff) {
						blue = 0x00;
						green = 0x00;
						red = 0x00;
					}

					imageDataCopy.put(y * image.getWidth() + x, (a << 24) | (red << 16) | (green << 8) | blue);
				}
			}

			try {
				cursor = new Cursor(image.getWidth(), image.getHeight(), textureInsets.left, image.getHeight() - (textureInsets.top + 1) , 1, imageDataCopy, null);
			} catch (LWJGLException e) {
				log.warn("Failed creating cursor!", e);
			}

			loadedCursors.put(name, cursor);
		}
		try {
			Mouse.setNativeCursor(cursor);
		} catch (LWJGLException e) {
			log.warn("Failed setting native cursor!", e);
		}
	}

	public static void setCursorVisible(boolean visible) {
		Mouse.setGrabbed(!visible);
	}

	public static void setCursorPosition(final int x, final int y) {
		VisualiserCallback.executeTask(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				Mouse.setCursorPosition(x, y);
				return null;
			}
		}, false, "set cursor position to: " + x + ", " + y, TaskQueue.UPDATE);
	}
}
