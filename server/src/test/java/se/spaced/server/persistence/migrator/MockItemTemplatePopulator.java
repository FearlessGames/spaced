package se.spaced.server.persistence.migrator;

import com.google.inject.Inject;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.ItemTemplateDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.shared.model.items.ItemType;

public class MockItemTemplatePopulator implements Migrator {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final TransactionManager transactionManager;
	private final ItemTemplateDao itemTemplateDao;
	private final SpellDao spellDao;

	@Inject
	public MockItemTemplatePopulator(TransactionManager transactionManager, ItemTemplateDao itemTemplateDao, SpellDao spellDao) {
		this.transactionManager = transactionManager;
		this.itemTemplateDao = itemTemplateDao;
		this.spellDao = spellDao;
	}

	@Override
	public void execute() {
		Transaction transaction = transactionManager.beginTransaction();
		if (!itemTemplateDao.findAll().isEmpty()) {
			return;
		}

		ServerSpell cool = spellDao.findByName("Cool");

		ServerItemTemplate slurm = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Can of Slurm", ItemType.CONSUMABLE).
				appearance(new PersistedAppearanceData("", "textures/items/icons/can")).spell(cool).build();

		itemTemplateDao.persist(slurm);

		ServerSpell snack = spellDao.findByName("Snack");
		ServerItemTemplate popplers = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Bucket of Popplers", ItemType.CONSUMABLE).
				appearance(new PersistedAppearanceData("", "textures/items/icons/bucket")).spell(snack).build();
		itemTemplateDao.persist(popplers);

		ServerItemTemplate rinds = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Bag of Gragnar's human rinds", ItemType.CONSUMABLE).
				appearance(new PersistedAppearanceData("", "textures/items/icons/snackbag")).build();
		itemTemplateDao.persist(rinds);

		ServerItemTemplate pigTail = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Pig tail", ItemType.TRASH).
				appearance(new PersistedAppearanceData("", "textures/items/icons/pigtail")).build();
		itemTemplateDao.persist(pigTail);

		ServerItemTemplate snout = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Snout", ItemType.TRASH).
				appearance(new PersistedAppearanceData("", "textures/items/icons/snout")).build();
		itemTemplateDao.persist(snout);

		ServerItemTemplate burnedBacon = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Burned bacon", ItemType.TRASH).
				appearance(new PersistedAppearanceData("", "textures/items/icons/bacon")).build();
		itemTemplateDao.persist(burnedBacon);


		ServerItemTemplate mk5helmet = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk5 Helmet", ItemType.HELMET).
				appearance(new PersistedAppearanceData("/props/gear/head/helmets/mk5/tn_red_mk5.xmo", "/items/icons/helmet_mk5_red")).
				build();
		itemTemplateDao.persist(mk5helmet);

		ServerItemTemplate mk4helmet = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk4 Helmet", ItemType.HELMET).
				appearance(new PersistedAppearanceData("/props/gear/head/helmets/tn_mk4/tn_red_mk4.xmo", "/items/icons/helmet_mk4_red")).
				build();
		itemTemplateDao.persist(mk4helmet);

		ServerItemTemplate mk3helmet = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk3 Helmet", ItemType.HELMET).
				appearance(new PersistedAppearanceData("/props/gear/head/helmets/tn_mk3/tn_red_mk3.xmo", "/items/icons/helmet_mk3_red")).
				build();
		itemTemplateDao.persist(mk3helmet);

		ServerItemTemplate mk2helmet = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk2 Helmet", ItemType.HELMET).
				appearance(new PersistedAppearanceData("/props/gear/head/helmets/mk2/mk2_red.xmo", "/items/icons/helmet_mk2_red")).
				build();
		itemTemplateDao.persist(mk2helmet);

		ServerItemTemplate caphat = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Cap", ItemType.HELMET).
				appearance(new PersistedAppearanceData("/props/gear/head/hats/cap/cap.xmo", "/items/icons/cap")).
				build();
		itemTemplateDao.persist(caphat);

		ServerItemTemplate leatherhat = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Leather Hat", ItemType.HELMET).
				appearance(new PersistedAppearanceData("/props/gear/head/hats/leatherbrim/leatherbrim.xmo", "/items/icons/leatherbrim")).
				build();
		itemTemplateDao.persist(leatherhat);

		ServerItemTemplate shades = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Shades", ItemType.HELMET).
				appearance(new PersistedAppearanceData("/props/gear/head/face/glasses/shades.xmo", "/items/icons/shades")).
				build();
		itemTemplateDao.persist(shades);

		ServerItemTemplate mk5chest = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk5 Chest", ItemType.SHIRT).
				appearance(new PersistedAppearanceData("/props/gear/chest/armor/mk5/tn_red_mk5.xmo", "/items/icons/chest_mk5_red")).
				build();
		itemTemplateDao.persist(mk5chest);

		ServerItemTemplate mk4chest = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk4 Chest", ItemType.SHIRT).
				appearance(new PersistedAppearanceData("/props/gear/chest/armor/mk4/tn_red_mk4.xmo", "/items/icons/chest_mk4_red")).
				build();
		itemTemplateDao.persist(mk4chest);

		ServerItemTemplate mk3chest = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk3 Chest", ItemType.SHIRT).
				appearance(new PersistedAppearanceData("/props/gear/chest/armor/mk3/mk3_red.xmo", "/items/icons/chest_mk3_red")).
				build();
		itemTemplateDao.persist(mk3chest);

		ServerItemTemplate mk2chest = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk2 Chest", ItemType.SHIRT).
				appearance(new PersistedAppearanceData("/props/gear/chest/armor/mk2/mk2_red.xmo", "/items/icons/chest_mk2_red")).
				build();
		itemTemplateDao.persist(mk2chest);

		ServerItemTemplate mk5legs = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk5 Legs", ItemType.TROUSERS).
				appearance(new PersistedAppearanceData("/props/gear/legs/armor/mk5/tn_red_mk5.xmo", "/items/icons/legs_mk5_red")).
				build();
		itemTemplateDao.persist(mk5legs);

		ServerItemTemplate mk4legs = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk4 Legs", ItemType.TROUSERS).
				appearance(new PersistedAppearanceData("/props/gear/legs/armor/mk4/tn_red_mk4.xmo", "/items/icons/legs_mk4_red")).
				build();
		itemTemplateDao.persist(mk4legs);

		ServerItemTemplate mk3legs = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk3 Legs", ItemType.TROUSERS).
				appearance(new PersistedAppearanceData("/props/gear/legs/armor/mk3/mk3_red.xmo", "/items/icons/legs_mk3_red")).
				build();
		itemTemplateDao.persist(mk3legs);

		ServerItemTemplate mk5feet = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk5 Boots", ItemType.SHOES).
				appearance(new PersistedAppearanceData("/props/gear/feet/boots/mk5/tn_red_mk5.xmo", "/items/icons/boots_mk5_red")).
				build();
		itemTemplateDao.persist(mk5feet);

		ServerItemTemplate mk2feet = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Mk2 Boots", ItemType.SHOES).
				appearance(new PersistedAppearanceData("/props/gear/feet/boots/mk2/tn_red_mk2.xmo", "/items/icons/boots_mk2_red")).
				build();
		itemTemplateDao.persist(mk2feet);

		ServerItemTemplate jetpack = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Beginners Jetpack", ItemType.JETPACK).
				appearance(new PersistedAppearanceData("/props/gear/back/propulsion/jetpack/jetpack_mk1.xmo", "/items/icons/jetpack")).
				build();
		itemTemplateDao.persist(jetpack);

		ServerItemTemplate s11 = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "s-11", ItemType.VEHICLE).
				appearance(new PersistedAppearanceData("/props/vehicles/flying/small/s-11/s-11.xmo", "/items/icons/s-11")).
				build();
		itemTemplateDao.persist(s11);

		ServerItemTemplate disc4 = new ServerItemTemplate.Builder(UUIDFactoryImpl.INSTANCE.combUUID(), "Disc-4", ItemType.VEHICLE).
				appearance(new PersistedAppearanceData("/props/vehicles/flying/small/disc-4/disc-4.xmo", "/items/icons/disc-4")).
				build();
		itemTemplateDao.persist(disc4);

		transaction.commit();
	}
}
