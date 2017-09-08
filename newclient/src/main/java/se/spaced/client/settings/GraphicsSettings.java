package se.spaced.client.settings;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import se.ardortech.TextureManager;
import se.fearless.common.io.IOLocator;
import se.spaced.client.ardor.FlatWater;
import se.spaced.client.ardor.SimpleWater;
import se.spaced.client.ardor.Water;
import se.spaced.client.environment.components.Sky;

public class GraphicsSettings implements ValidatedSettings {
	@XStreamAlias("water")
	private WaterProvider waterProvider = WaterProvider.FLAT;

	public AbstractModule getWaterModule() {
		return waterProvider.getModule();
	}

	@Override
	public boolean valid() {
		return waterProvider != null;
	}

	private enum WaterProvider {
		SIMPLE {
			@Override
			public AbstractModule getModule() {
				return new AbstractModule() {
					@Override
					protected void configure() {
					}

					@Provides
					@Singleton
					public Water getWater() {
						return new SimpleWater();
					}

				};
			}
		},

		FLAT {
			@Override
			public AbstractModule getModule() {
				return new AbstractModule() {
					@Override
					protected void configure() {
					}

					@Provides
					@Singleton
					public Water getWater(TextureManager textureManager, Sky sky, IOLocator streamLocator) {
						return new FlatWater(textureManager, sky, streamLocator);
					}
				};
			}
		};

		public abstract AbstractModule getModule();
	}
}
