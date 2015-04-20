package se.spaced.client.environment;

import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.client.environment.components.Fog;
import se.spaced.client.environment.components.Sky;
import se.spaced.client.environment.components.Sun;
import se.spaced.client.environment.settings.EnvSettings;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.environment.time.GameTimeManager;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.sound.music.AmbientSystem;

@Singleton
public class EnvironmentSystem {
	private final GameTimeManager gameTimeManager;
	private final Sun sun;
	private final Fog fog;
	private final TimeProvider timeProvider;
	private final Sky sky;
	private final AmbientSystem ambientSystem;
	private final UserCharacter userCharacter;

	private final ZoneInterpolationStrategy zoneInterpolationStrategy;

	@Inject
	public EnvironmentSystem(
			GameTimeManager gameTimeManager,
			Sun sun,
			Fog fog,
			TimeProvider timeProvider,
			Sky sky,
			AmbientSystem ambientSystem,
			UserCharacter userCharacter,
			ZoneActivationService zoneActivationService, ZoneEnvironmentProvider zoneEnvironmentProvider) {
		this.gameTimeManager = gameTimeManager;
		this.sun = sun;
		this.fog = fog;
		this.timeProvider = timeProvider;
		this.sky = sky;
		this.ambientSystem = ambientSystem;
		this.userCharacter = userCharacter;
		zoneInterpolationStrategy = new ZoneInterpolationStrategy(zoneActivationService, zoneEnvironmentProvider);
	}

	public void update(Camera camera, double dt) {
		GameTime currentGameTime = gameTimeManager.fromSystemTime(timeProvider.now());
		if (userCharacter.getUserControlledEntity() != null) {

			SpacedVector3 position = userCharacter.getPosition();

			EnvSettings envSettings = zoneInterpolationStrategy.getSettings(currentGameTime, position);

			if (envSettings.getSunSetting() != null) {
				sun.setCurrentSettings(envSettings.getSunSetting());
			}

			if (envSettings.getFogSetting() != null) {
				fog.setCurrentSettings(envSettings.getFogSetting());
			}

			if (envSettings.getSoundSetting() != null) {
				ambientSystem.setCurrentSettings(envSettings.getSoundSetting());
			}
		}

		sun.update(camera, currentGameTime);
		sky.update(camera);
	}

	public void init(Node root) {
		sun.init(root);
		sky.init(root);
		fog.init(root);
	}

	public Sun getSun() {
		return sun;
	}
}
