package se.spaced.client.view;

import com.ardor3d.image.Image;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.input.MouseCursor;
import com.ardor3d.input.MouseManager;
import com.google.common.collect.Maps;
import com.google.common.io.InputSupplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.client.view.cursor.BasicCursorView;
import se.spaced.client.view.cursor.Cursor;
import se.spaced.client.view.cursor.CursorResources;
import se.spaced.client.view.cursor.CursorView;
import se.spaced.client.view.entity.DefaultEntityInteractionView;
import se.spaced.client.view.entity.EntityInteractionView;
import se.spaced.client.view.entity.EntityView;
import se.spaced.client.view.entity.VisualEntityView;
import se.spaced.shared.util.ListenerDispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ViewModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(EntityView.class).to(VisualEntityView.class);
		bind(EntityInteractionView.class).to(DefaultEntityInteractionView.class);
		bind(PropView.class).to(PropViewImpl.class);
		bind(AbilityView.class).to(AbilityViewImpl.class);
		bind(AWTImageLoader.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public CursorView getCursorView(AWTImageLoader imageLoader, StreamLocator streamLocator, MouseManager mouseManager) {
		Map<Cursor, MouseCursor> cursors = Maps.newHashMap();

		for (Map.Entry<Cursor, String> entry : CursorResources.cursors()) {
			InputSupplier<? extends InputStream> is = streamLocator.getInputSupplier(entry.getValue());

			try (InputStream stream = is.getInput()) {
				Image image = imageLoader.load(stream, true);
				MouseCursor mouseCursor = new MouseCursor(entry.getKey().toString(), image, 0, image.getHeight() - 1);
				cursors.put(entry.getKey(), mouseCursor);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return new BasicCursorView(mouseManager, cursors);
	}

	@Provides
	@Singleton
	public ListenerDispatcher<PropSelectionListener> getPropViewLD() {
		ListenerDispatcher<PropSelectionListener> listenerDispatcher = ListenerDispatcher.create(
				PropSelectionListener.class);
		return listenerDispatcher;
	}

	@Provides
	@Singleton
	public ActivePropProvider getActivePropProvider(PropView propView) {
		return (ActivePropProvider) propView;
	}
}
