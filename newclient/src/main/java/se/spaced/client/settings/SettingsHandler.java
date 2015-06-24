package se.spaced.client.settings;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.render.module.RendererSettings;
import se.fearless.common.lifetime.LifetimeListener;
import se.fearless.common.lifetime.LifetimeManager;
import se.spaced.shared.xml.XmlIO;

@Singleton
public class SettingsHandler implements LifetimeListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String RENDER_SETTINGS_FILE = "rendererSettings.xml";
	private static final String ACCOUNT_SETTINGS_FILE = "accountSettings.xml";
	private static final String GRAPHICS_SETTINGS_FILE = "graphicsSettings.xml";

	private final PersistedSettings<RendererSettings> rendererSettings;
	private final PersistedSettings<AccountSettings> accountSettings;
	private final PersistedSettings<GraphicsSettings> graphicsSettings;

	private boolean missingRenderSettings;

	@Inject
	public SettingsHandler(
			@Named("settings") final XmlIO xmlIO,
			final LifetimeManager lifetimeManager) {

		rendererSettings = new PersistedSettings<RendererSettings>(xmlIO, RENDER_SETTINGS_FILE, RendererSettings.class);
		accountSettings = new PersistedSettings<AccountSettings>(xmlIO, ACCOUNT_SETTINGS_FILE, AccountSettings.class);
		graphicsSettings = new PersistedSettings<GraphicsSettings>(xmlIO, GRAPHICS_SETTINGS_FILE, GraphicsSettings.class);

		rendererSettings.load();
		accountSettings.load();
		graphicsSettings.load();

		if (accountSettings.get() == null) {
			accountSettings.set(new AccountSettings());
		}

		if (graphicsSettings.get() == null) {
			graphicsSettings.set(new GraphicsSettings());
		}

		if (rendererSettings.get() == null) {
			rendererSettings.set(new RendererSettings());
			missingRenderSettings = true;
		}

		save();

		lifetimeManager.addListener(this);
	}

	public boolean isMissingRenderSettings() {
		return missingRenderSettings;
	}

	public GraphicsSettings getGraphicsSettings() {
		return graphicsSettings.get();
	}

	public AccountSettings getAccountSettings() {
		return accountSettings.get();
	}

	public RendererSettings getRendererSettings() {
		return rendererSettings.get();
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onShutdown() {
		save();
	}

	public void save() {
		accountSettings.save();
		graphicsSettings.save();
		rendererSettings.save();
	}
}
