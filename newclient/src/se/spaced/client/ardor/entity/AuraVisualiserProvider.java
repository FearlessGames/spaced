package se.spaced.client.ardor.entity;

import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.effect.AsynchEffect;
import se.spaced.client.ardor.effect.EffectContext;
import se.spaced.client.ardor.effect.EffectSystem;
import se.spaced.client.view.entity.EntityEffectDirectory;
import se.spaced.client.view.entity.VisualEntity;

public class AuraVisualiserProvider implements Provider<AuraVisualiser> {
	private final EffectSystem effectSystem;
	private final EntityEffectDirectory effectDirectory;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String JETPACK_CHARGE = "abilities/jetpack.charge";
	private static final String JETPACK_TRAVEL = "abilities/jetpack.travel";

	private static final String FORTITUDE_GAINED = "abilities/hpbuff.charge";

	@Inject
	public AuraVisualiserProvider(EffectSystem effectSystem, EntityEffectDirectory effectDirectory) {
		this.effectSystem = effectSystem;
		this.effectDirectory = effectDirectory;
	}

	@Override
	public AuraVisualiser get() {
		AuraVisualiser auraVisualiser = new AuraVisualiser();

		auraVisualiser.addMapping(AuraVisualiserEvent.JETPACK_STARTED, new AuraTrigger() {
			@Override
			public void trigger(VisualEntity visualEntity) {
				final AsynchEffect chargeEffect = effectDirectory.remove(visualEntity, JETPACK_CHARGE);
				if (chargeEffect != null) {
					chargeEffect.stop();
				}

				final EffectContext effectContext = buildEffectContext(visualEntity);
				AsynchEffect asynchEffect = new AsynchEffect(effectContext, JETPACK_TRAVEL, effectSystem);
				effectDirectory.put(visualEntity, JETPACK_TRAVEL, asynchEffect);
				asynchEffect.start();

				log.debug("Jetpack travel");
			}
		});
		auraVisualiser.addMapping(AuraVisualiserEvent.JETPACK_THRUST, new AuraTrigger() {
			@Override
			public void trigger(VisualEntity visualEntity) {
				final AsynchEffect travelEffect = effectDirectory.remove(visualEntity, JETPACK_TRAVEL);
				if (travelEffect != null) {
					travelEffect.stop();
				}

				final EffectContext effectContext = buildEffectContext(visualEntity);
				AsynchEffect asynchEffect = new AsynchEffect(effectContext, JETPACK_CHARGE, effectSystem);
				effectDirectory.put(visualEntity, JETPACK_CHARGE, asynchEffect);
				asynchEffect.start();

				log.debug("Jetpack charge");
			}
		});

		auraVisualiser.addMapping(AuraVisualiserEvent.JETPACK_STOPPED, new AuraTrigger() {
			@Override
			public void trigger(VisualEntity entity) {
				AsynchEffect chargeEffect = effectDirectory.findByEntity(entity, JETPACK_CHARGE);
				if (chargeEffect != null) {
					chargeEffect.stop();
				}

				AsynchEffect travelEffect = effectDirectory.findByEntity(entity, JETPACK_TRAVEL);
				if (travelEffect != null) {
					travelEffect.stop();
				}

				log.debug("Jetpack stop");
			}
		});

		auraVisualiser.addMapping(AuraVisualiserEvent.FORTITUDE_GAINED, new AuraTrigger() {
			@Override
			public void trigger(VisualEntity entity) {
				EffectContext effectContext = buildEffectContext(entity);
				AsynchEffect asynchEffect = new AsynchEffect(effectContext, FORTITUDE_GAINED, effectSystem);
				effectDirectory.put(entity, FORTITUDE_GAINED, asynchEffect);
				asynchEffect.start();
			}
		});

		auraVisualiser.addMapping(AuraVisualiserEvent.FORTITUDE_LOST, new AuraTrigger() {
			@Override
			public void trigger(VisualEntity entity) {
				AsynchEffect effect = effectDirectory.remove(entity, FORTITUDE_GAINED);
				if (effect != null) {
					effect.stop();
				}
			}
		});

		return auraVisualiser;
	}

	private EffectContext buildEffectContext(VisualEntity visualEntity) {
		Node node = visualEntity.getNode();
		return new EffectContext.Builder().spatialParent(node).source(visualEntity).target(visualEntity).build();
	}
}
