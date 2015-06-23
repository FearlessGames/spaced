package se.spaced.server.mob.brains.templates;

import com.google.inject.Inject;
import se.fearless.common.time.TimeProvider;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.VendorBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.model.vendor.VendorService;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class VendorBrainTemplate extends ProximityWhisperBrainTemplate {

	@Transient
	private final MobOrderExecutor mobOrderExecutor;
	@Transient
	private final TimeProvider timeProvider;
	@Transient
	private final ItemService itemService;
	@Transient
	private final VendorService vendorService;

	@Inject
	public VendorBrainTemplate(
			MobOrderExecutor mobOrderExecutor,
			TimeProvider timeProvider,
			VendorService vendorService, ItemService itemService) {
		super(null, null);
		this.vendorService = vendorService;
		this.mobOrderExecutor = mobOrderExecutor;
		this.timeProvider = timeProvider;
		this.itemService = itemService;
	}


	@Override
	public MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		VendorBrain vendorBrain = new VendorBrain(mob,
				brainParameterProvider.getItemTypesForSale(),
				mobOrderExecutor,
				brainParameterProvider.getWhisperMessage().getTimeout(),
				brainParameterProvider.getWhisperMessage().getDistance(),
				timeProvider,
				itemService);
		vendorService.registerVendor(vendorBrain);
		return vendorBrain;
	}
}
