package se.spaced.client.settings;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.googlecode.gentyref.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.opengl.DisplayMode;
import se.spaced.client.settings.ui.DisplayModeSelector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class DisplayModeSelectorTest {

	private DisplayMode displayMode0;
	private DisplayMode displayMode1;
	private DisplayMode displayMode2;
	private DisplayMode displayMode3;
	public static final TypeToken<Supplier<DisplayMode>> MODE_SUPPLIER_TYPE_TOKEN = new TypeToken<Supplier<DisplayMode>>() {
	};

	@Before
	public void setUp() throws Exception {
		displayMode0 = createNewDisplayMode(800, 600, 16, 40);
		displayMode1 = createNewDisplayMode(1024, 800, 32, 40);
		displayMode2 = createNewDisplayMode(800, 600, 16, 40);
		displayMode3 = createNewDisplayMode(1024, 800, 16, 60);
	}

	@Test
	public void getDefaultSelectedDisplayMode() throws Exception {

		Supplier<List<DisplayMode>> supplier = new Supplier<List<DisplayMode>>() {
			@Override
			public List<DisplayMode> get() {
				return Lists.newArrayList(displayMode0, displayMode1, displayMode2, displayMode3);
			}
		};
		Supplier<DisplayMode> savedDisplayModeSupplier = mock(MODE_SUPPLIER_TYPE_TOKEN);
		DisplayModeSelector selector = new DisplayModeSelector(supplier, savedDisplayModeSupplier);
		DisplayMode mode = selector.getDefaultSelectedDisplayMode();
		assertEquals(displayMode1, mode);
	}

	@Test
	public void getDisplayModeWhenOneIsSaved() throws Exception {
		TypeToken<Supplier<List<DisplayMode>>> type = new TypeToken<Supplier<List<DisplayMode>>>() {
		};
		Supplier<List<DisplayMode>> availableSupplier = mock(type);
		Supplier<DisplayMode> savedModeSupplier = mock(MODE_SUPPLIER_TYPE_TOKEN);
		when(savedModeSupplier.get()).thenReturn(displayMode2);
		DisplayModeSelector selector = new DisplayModeSelector(availableSupplier, savedModeSupplier);
		DisplayMode mode = selector.getDisplayMode();
		assertEquals(displayMode2, mode);
	}

	private DisplayMode createNewDisplayMode(
			int width,
			int height,
			int bpp,
			int frequency) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<DisplayMode> displayModeClass = DisplayMode.class;
		Constructor<DisplayMode> constructor = displayModeClass.getDeclaredConstructor(int.class,
				int.class,
				int.class,
				int.class);
		constructor.setAccessible(true);
		return constructor.newInstance(width, height, bpp, frequency);
	}
}
