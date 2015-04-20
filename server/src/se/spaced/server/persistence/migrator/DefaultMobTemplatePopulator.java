package se.spaced.server.persistence.migrator;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.hibernate.Transaction;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;
import se.spaced.server.persistence.dao.interfaces.FactionDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DefaultMobTemplatePopulator implements Migrator {
	private final TransactionManager transactionManager;
	private final EntityTemplateDao entityTemplateDao;
	private final FactionDao factionDao;
	private final CreatureTypeDao creatureTypeDao;

	private final SpellDao spellDao;
	private ServerSpell plasmaBallSpell;
	private ServerSpell meleeSpell;
	private ServerSpell healSpell;


	private Collection<ServerSpell> mobSpells;
	private PersistedFaction borgPigsFaction;
	private PersistedFaction cratesFaction;
	private PersistedFaction dcsFaction;
	private ServerSpell turretLazorBlastSpell;

	@Inject
	public DefaultMobTemplatePopulator(
			TransactionManager transactionManager,
			SpellDao spellDao,
			EntityTemplateDao entityTemplateDao,
			FactionDao factionDao,
			CreatureTypeDao creatureTypeDao) {
		this.transactionManager = transactionManager;
		this.spellDao = spellDao;
		this.entityTemplateDao = entityTemplateDao;
		this.factionDao = factionDao;
		this.creatureTypeDao = creatureTypeDao;
	}

	@Override
	public void execute() {
		Transaction transaction = transactionManager.beginTransaction();
		List<EntityTemplate> all = entityTemplateDao.findAll();
		if (!all.isEmpty()) {
			return;
		}

		plasmaBallSpell = spellDao.findByName("Plasma ball");
		meleeSpell = spellDao.findByName("Melee");
		healSpell = spellDao.findByName("Recharge");
		turretLazorBlastSpell = spellDao.findByName("Mech Turret Long Range Lazor blast");

		mobSpells = Lists.asList(plasmaBallSpell, new ServerSpell[]{meleeSpell, plasmaBallSpell, healSpell, turretLazorBlastSpell});

		borgPigsFaction = factionDao.findByName("borgpigs");
		cratesFaction = factionDao.findByName("crates");
		dcsFaction = factionDao.findByName("dcs");


		addScott();

		addTargetDummy();

		addPigMaster();

		addCrateLord();

		addCommonCrate();

		addBorgPig();

		addMechTurret();

		addEzyPig();

		transaction.commit();
	}

	private void addScott() {
		ServerSpell hardBlow = spellDao.findByName("Hard blow");
		ServerSpell lazorBlast = spellDao.findByName("Pocket rocket");

		List<ServerSpell> spellList = Arrays.asList(hardBlow, lazorBlast);

		PersistedAppearanceData appearanceData = new PersistedAppearanceData("models/mobs/guard_mk3_red", "icon_manager");
		MobTemplate.Builder builder = new MobTemplate.Builder(UUIDFactoryImpl.INSTANCE.randomUUID(), "Scott Michaels");
		builder.creatureType(creatureTypeDao.findByName("humanoid")).stamina(70).
				appearance(appearanceData).spells(spellList).faction(cratesFaction);
		entityTemplateDao.persist(builder.build());

	}

	private void addTargetDummy() {


		PersistedAppearanceData appearanceData = new PersistedAppearanceData("models/mobs/guard_mk3_red", "icon_dummy");
		MobTemplate.Builder builder = new MobTemplate.Builder(UUIDFactoryImpl.INSTANCE.randomUUID(), "Target Dummy");
		builder.creatureType(creatureTypeDao.findByName("humanoid")).stamina(9001).
				appearance(appearanceData).faction(cratesFaction);
		entityTemplateDao.persist(builder.build());

	}


	private void addPigMaster() {
		PersistedAppearanceData appearanceData = new PersistedAppearanceData("models/mobs/pigmaster x-1620", "icon_bossrobot");

		MobTemplate.Builder builder = new MobTemplate.Builder(UUIDFactoryImpl.INSTANCE.randomUUID(), "Pigmaster X-1620");
		builder.creatureType(creatureTypeDao.findByName("mechanical")).appearance(appearanceData).stamina(200).
				spells(mobSpells).faction(borgPigsFaction);
		entityTemplateDao.persist(builder.build());
	}

	private void addCrateLord() {
		PersistedAppearanceData appearanceData = new PersistedAppearanceData("models/mobs/cratelord", "icon_cratelord");

		MobTemplate.Builder builder = new MobTemplate.Builder(UUIDFactoryImpl.INSTANCE.randomUUID(), "Eemba the Crate Lord");
		builder.creatureType(creatureTypeDao.findByName("mechanical")).appearance(appearanceData).stamina(1200).
				spells(mobSpells).faction(cratesFaction);
		entityTemplateDao.persist(builder.build());

	}

	private void addCommonCrate() {
		PersistedAppearanceData appearanceData = new PersistedAppearanceData("models/mobs/crate", "icon_mysteriouscrate");
		MobTemplate.Builder builder = new MobTemplate.Builder(UUIDFactoryImpl.INSTANCE.randomUUID(), "Common crate");

		builder.creatureType(creatureTypeDao.findByName("mechanical")).appearance(appearanceData).stamina(3).
				spells(mobSpells).faction(cratesFaction);
		entityTemplateDao.persist(builder.build());
	}

	private void addBorgPig() {
		PersistedAppearanceData appearanceData = new PersistedAppearanceData("models/mobs/borgpig", "icon_borgpig");
		MobTemplate.Builder builder = new MobTemplate.Builder(UUIDFactoryImpl.INSTANCE.randomUUID(), "Borg pig");
		builder.creatureType(creatureTypeDao.findByName("beast")).appearance(appearanceData).stamina(20).
				spells(mobSpells).faction(borgPigsFaction);
		entityTemplateDao.persist(builder.build());
	}

	private void addEzyPig() {
		PersistedAppearanceData appearanceData = new PersistedAppearanceData("models/mobs/ezypig", "icon_ezypig");
		MobTemplate.Builder builder = new MobTemplate.Builder(UUIDFactoryImpl.INSTANCE.randomUUID(), "Ezy Pig");
		builder.creatureType(creatureTypeDao.findByName("beast")).appearance(appearanceData).stamina(2).
				spells(mobSpells).faction(borgPigsFaction);
		entityTemplateDao.persist(builder.build());
	}


	private void addMechTurret() {

		PersistedAppearanceData appearanceData = new PersistedAppearanceData("models/mobs/mech-turret", "icon_dcs_turret");
		MobTemplate.Builder builder = new MobTemplate.Builder(UUIDFactoryImpl.INSTANCE.randomUUID(), "Mech Turret");
		builder.creatureType(creatureTypeDao.findByName("mechanical")).appearance(appearanceData).stamina(500).
				spells(mobSpells).faction(dcsFaction);
		entityTemplateDao.persist(builder.build());

	}
}