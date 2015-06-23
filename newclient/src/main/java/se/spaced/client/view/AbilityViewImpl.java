package se.spaced.client.view;

import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.spaced.client.ardor.effect.AsynchEffect;
import se.spaced.client.ardor.effect.EffectContext;
import se.spaced.client.ardor.effect.EffectSystem;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.view.entity.EntityEffectDirectory;
import se.spaced.client.view.entity.EntityView;
import se.spaced.client.view.entity.VisualEntity;

@Singleton
public class AbilityViewImpl implements AbilityView {
	private final EffectSystem effectSystem;
	private final EntityView entityView;
	private final Node entityNode;
	private final EntityEffectDirectory effectDirectory;

	@Inject
	public AbilityViewImpl(
			EffectSystem effectSystem,
			@Named("entityNode") Node entityNode,
			EntityView entityView,
			EntityEffectDirectory effectDirectory) {
		this.effectSystem = effectSystem;
		this.entityNode = entityNode;
		this.entityView = entityView;
		this.effectDirectory = effectDirectory;
	}

	@Override
	public void startAbilityCharge(final ClientEntity source, final ClientEntity target, final ClientSpell spell) {
		final VisualEntity sourceEntity = entityView.getEntity(source.getPk());
		final VisualEntity targetEntity = entityView.getEntity(target.getPk());

		EffectContext effectContext = new EffectContext.Builder()
				.spatialParent(sourceEntity.getNode())
				.source(sourceEntity)
				.target(targetEntity)
				.build();

		String effectName = spell.getEffectResource() + ".charge";
		AsynchEffect asynchEffect = new AsynchEffect(effectContext, effectName, effectSystem);
		asynchEffect.start();
		effectDirectory.put(sourceEntity, effectName, asynchEffect);
	}

	@Override
	public void stopAbilityCharge(final ClientEntity source, final ClientSpell spell) {
		final VisualEntity entity = entityView.getEntity(source.getPk());
		final AsynchEffect effect = effectDirectory.remove(entity, spell.getEffectResource() + ".charge");
		if (effect != null) {
			effect.stop();
		}
	}

	@Override
	public void startEffectApplied(final ClientEntity source, ClientEntity target, String effectResource) {
		VisualEntity sourceVisualEntity = entityView.getEntity(source.getPk());
		VisualEntity targetVisualEntity = entityView.getEntity(target.getPk());

		EffectContext context = new EffectContext.Builder()
				.spatialParent(targetVisualEntity.getNode())
				.source(sourceVisualEntity)
				.target(targetVisualEntity)
				.build();

		effectSystem.startEffect(effectResource + ".apply", context);
	}

	@Override
	public void startAbilityProjectile(int projectileId, ClientEntity source, ClientEntity destination, String effectResource, double speed) {
		VisualEntity sourceVisualEntity = entityView.getEntity(source.getPk());
		VisualEntity targetVisualEntity = entityView.getEntity(destination.getPk());

		EffectContext effectContext = new EffectContext.Builder()
				.spatialParent(entityNode)
				.target(targetVisualEntity)
				.source(sourceVisualEntity)
				.setProjectileSpeed(speed)
				.build();

		effectSystem.startEffect(effectResource + ".travel", effectContext);
	}
}
