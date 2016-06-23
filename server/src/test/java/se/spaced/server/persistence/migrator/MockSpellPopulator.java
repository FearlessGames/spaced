package se.spaced.server.persistence.migrator;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.hibernate.Transaction;
import se.fearless.common.stats.ModStat;
import se.fearless.common.stats.Operator;
import se.fearless.common.uuid.UUIDFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.ModStatAura;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.*;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.TargetingType;
import se.spaced.shared.model.stats.SpacedStatType;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MockSpellPopulator implements Migrator {
	private final TransactionManager transactionManager;
	private final SpellDao spellDao;
	private final SpellCombatService spellCombatService;
	private final ActionScheduler actionScheduler;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final AtomicInteger projectileIdCounter;
	private final AuraService auraService;
	private final UUIDFactory uuidFactory;

	@Inject
	public MockSpellPopulator(
			TransactionManager transactionManager,
			SpellDao spellDao,
			SpellCombatService spellCombatService,
			ActionScheduler actionScheduler,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			@Named("projectileId") AtomicInteger projectileIdCounter,
			AuraService auraService, UUIDFactory uuidFactory) {
		this.transactionManager = transactionManager;
		this.spellDao = spellDao;
		this.spellCombatService = spellCombatService;
		this.actionScheduler = actionScheduler;
		this.smrtBroadcaster = smrtBroadcaster;
		this.projectileIdCounter = projectileIdCounter;
		this.auraService = auraService;
		this.uuidFactory = uuidFactory;
	}

	@Override
	public void execute() {
		Transaction transaction = transactionManager.beginTransaction();
		List<ServerSpell> all = spellDao.findAll();
		if (!all.isEmpty()) {
			return;
		}
		ProjectileEffect projectileEffect = new ProjectileEffect(actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);
		projectileEffect.setPk(uuidFactory.combUUID());
		DamageSchoolEffect lazorBlastEffect = new DamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(6, 12), MagicSchool.LIGHT);
		lazorBlastEffect.setPk(uuidFactory.combUUID());
		lazorBlastEffect.setResourceName("abilities/lazor_blast");
		projectileEffect.addImpactEffect(lazorBlastEffect);
		projectileEffect.setSpeed(70d);
		projectileEffect.setResourceName("projectiles/lazor_blast");
		spellDao.persist(new ServerSpell.Builder("Lazor blast").castTime(500).school(MagicSchool.LIGHT).
				addSpellEffect(projectileEffect).heatContribution(45).uuid(uuidFactory.combUUID()).
				cancelOnMove(true).requiresHostileTarget(true).ranges(0,
				40).effectResource("abilities/lazor_blast").build());


		projectileEffect = new ProjectileEffect(actionScheduler, smrtBroadcaster, projectileIdCounter);
		projectileEffect.setPk(uuidFactory.combUUID());
		DamageSchoolEffect turretBlast = new DamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(16, 35), MagicSchool.LIGHT);
		turretBlast.setPk(uuidFactory.combUUID());
		turretBlast.setResourceName("abilities/lazor_blast");
		projectileEffect.addImpactEffect(turretBlast);
		projectileEffect.setSpeed(300d);
		projectileEffect.setResourceName("projectiles/lazor_blast");
		spellDao.persist(new ServerSpell.Builder("Mech Turret Long Range Lazor blast").castTime(1000).school(MagicSchool.LIGHT).
				addSpellEffect(projectileEffect).heatContribution(15).uuid(uuidFactory.combUUID()).
				cancelOnMove(true).requiresHostileTarget(true).ranges(0,
				200).effectResource("abilities/lazor_blast").build());

		projectileEffect = new ProjectileEffect(actionScheduler, smrtBroadcaster, projectileIdCounter);
		projectileEffect.setPk(uuidFactory.combUUID());
		DamageSchoolEffect scottBlast = new DamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(11, 22), MagicSchool.LIGHT);
		scottBlast.setPk(uuidFactory.combUUID());
		scottBlast.setResourceName("abilities/lazor_blast");

		projectileEffect.addImpactEffect(scottBlast);
		projectileEffect.setSpeed(300d);
		projectileEffect.setResourceName("projectiles/lazor_blast");
		spellDao.persist(new ServerSpell.Builder("Pocket rocket").castTime(1500).school(MagicSchool.LIGHT).
				addSpellEffect(projectileEffect).heatContribution(5).uuid(uuidFactory.combUUID()).
				cancelOnMove(true).requiresHostileTarget(true).ranges(0,
				42).effectResource("abilities/lazor_blast").build());


		projectileEffect = new ProjectileEffect(actionScheduler, smrtBroadcaster, projectileIdCounter);
		projectileEffect.setPk(uuidFactory.combUUID());
		DamageSchoolEffect meleeEffect = new DamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(3, 10), MagicSchool.PHYSICAL);
		meleeEffect.setPk(uuidFactory.combUUID());
		meleeEffect.setResourceName("abilities/melee");
		projectileEffect.addImpactEffect(meleeEffect);
		projectileEffect.setSpeed(20d);
		projectileEffect.setResourceName("projectiles/melee");
		spellDao.persist(new ServerSpell.Builder("Melee").castTime(200).school(MagicSchool.PHYSICAL).
				addSpellEffect(projectileEffect).uuid(uuidFactory.combUUID()).
				requiresHostileTarget(true).ranges(0, 6).effectResource("abilities/melee").heatContribution(8).build());

		DamageSchoolEffect scottMeleeEffect = new DamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(70, 83), MagicSchool.PHYSICAL);
		scottMeleeEffect.setPk(uuidFactory.combUUID());
		scottMeleeEffect.setResourceName("abilities/melee");

		spellDao.persist(new ServerSpell.Builder("Hard blow").castTime(100).school(MagicSchool.PHYSICAL).
				addSpellEffect(scottMeleeEffect).uuid(uuidFactory.combUUID()).
				requiresHostileTarget(true).ranges(0, 6).effectResource("abilities/melee").heatContribution(1).build());


		projectileEffect = new ProjectileEffect(actionScheduler, smrtBroadcaster, projectileIdCounter);
		projectileEffect.setPk(uuidFactory.combUUID());
		DamageSchoolEffect overchargeEffect = new DamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(40, 55), MagicSchool.ELECTRICITY);
		overchargeEffect.setPk(uuidFactory.combUUID());
		overchargeEffect.setResourceName("abilities/overcharge");
		projectileEffect.addImpactEffect(overchargeEffect);
		projectileEffect.setSpeed(300d);
		projectileEffect.setResourceName("projectiles/overcharge");
		spellDao.persist(new ServerSpell.Builder("Overcharge").castTime(6000).school(MagicSchool.ELECTRICITY).
				addSpellEffect(projectileEffect).
				heatContribution(10).uuid(uuidFactory.combUUID()).
				cancelOnMove(true).requiresHostileTarget(true).ranges(0,
				50).effectResource("abilities/overcharge").build());

		projectileEffect = new ProjectileEffect(actionScheduler, smrtBroadcaster, projectileIdCounter);
		projectileEffect.setPk(uuidFactory.combUUID());
		DamageSchoolEffect plasmaBallEffect = new DamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(30, 45), MagicSchool.FIRE);
		plasmaBallEffect.setPk(uuidFactory.combUUID());
		plasmaBallEffect.setResourceName("abilities/plasmaball");
		projectileEffect.addImpactEffect(plasmaBallEffect);
		projectileEffect.setSpeed(15d);
		projectileEffect.setResourceName("projectiles/plasmaball");
		spellDao.persist(new ServerSpell.Builder("Plasma ball").castTime(3000).school(MagicSchool.FIRE).
				addSpellEffect(projectileEffect).uuid(uuidFactory.combUUID()).
				heatContribution(50).
				requiresHostileTarget(true).ranges(10, 100).effectResource("abilities/plasmaball").build());

		HealEffect rechargeEffect = new HealEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.ELECTRICITY,
				new IntervalInt(38, 50)
		);
		rechargeEffect.setPk(uuidFactory.combUUID());
		rechargeEffect.setResourceName("abilities/recharge");
		spellDao.persist(new ServerSpell.Builder("Recharge").castTime(1000).school(MagicSchool.ELECTRICITY).
				addSpellEffect(rechargeEffect).
				heatContribution(30).uuid(uuidFactory.combUUID()).
				cancelOnMove(true).ranges(0, 30).effectResource("abilities/recharge").build());

		CoolEffect coolEffect = new CoolEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FROST,
				new IntervalInt(38, 50)
		);
		coolEffect.setPk(uuidFactory.combUUID());
		coolEffect.setResourceName("abilities/coolcan");
		spellDao.persist(new ServerSpell.Builder("Cool").castTime(500).school(MagicSchool.FROST).
				addSpellEffect(coolEffect).uuid(uuidFactory.combUUID()).
				heatContribution(0).targetingType(TargetingType.SELF_ONLY).
				cancelOnMove(false).ranges(0, 30).effectResource("abilities/coolcan").build());

		HealEffect healingEatEffect = new HealEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.PHYSICAL,
				new IntervalInt(20, 40)
		);
		healingEatEffect.setPk(uuidFactory.combUUID());
		healingEatEffect.setResourceName("abilities/snack");
		spellDao.persist(new ServerSpell.Builder("Snack").castTime(500).school(MagicSchool.PHYSICAL).
				addSpellEffect(healingEatEffect).uuid(uuidFactory.combUUID()).
				heatContribution(0).targetingType(TargetingType.SELF_ONLY).
				cancelOnMove(false).ranges(0, 30).effectResource("abilities/snack").build());

		projectileEffect = new ProjectileEffect(actionScheduler, smrtBroadcaster, projectileIdCounter);
		projectileEffect.setPk(uuidFactory.combUUID());
		DamageSchoolEffect volatileTargetEffect = new DamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(35, 45), MagicSchool.FIRE);
		volatileTargetEffect.setPk(uuidFactory.combUUID());
		volatileTargetEffect.setResourceName("abilities/volatile_combustion");
		projectileEffect.addImpactEffect(volatileTargetEffect);
		SelfDamageSchoolEffect selfDamage = new SelfDamageSchoolEffect(spellCombatService, smrtBroadcaster,
				new IntervalInt(5, 10), MagicSchool.FIRE);
		selfDamage.setPk(uuidFactory.combUUID());
		projectileEffect.addImpactEffect(selfDamage);
		projectileEffect.setSpeed(15d);
		projectileEffect.setResourceName("projectiles/volatile_combustion");
		spellDao.persist(new ServerSpell.Builder("Volatile combustion").castTime(3000).school(MagicSchool.FIRE).
				addSpellEffect(projectileEffect).uuid(uuidFactory.combUUID()).
				heatContribution(30).
				cancelOnMove(true).ranges(0,
				100).requiresHostileTarget(true).effectResource("abilities/volatile_combustion").build());


		InterruptEffect disruptEffect = new InterruptEffect(spellCombatService, smrtBroadcaster, MagicSchool.ELECTRICITY);
		disruptEffect.setPk(uuidFactory.combUUID());
		spellDao.persist(new ServerSpell.Builder("Disrupt").castTime(1000).school(MagicSchool.ELECTRICITY).
				addSpellEffect(disruptEffect).
				requiresHostileTarget(true).ranges(0,
				40).uuid(uuidFactory.combUUID()).effectResource("abilities/disrupt").build());

		ServerAura aura = new ModStatAura("Fortitude",
				"textures/gui/abilityicons/fortitude", 60 * 1000L, false,
				0, true, new ModStat(5, SpacedStatType.STAMINA, Operator.ADD));
		aura.setPk(uuidFactory.combUUID());
		ApplyAuraEffect auraEffect = new ApplyAuraEffect(smrtBroadcaster, MagicSchool.FIRE, aura,
				auraService);
		auraEffect.setPk(uuidFactory.combUUID());
		spellDao.persist(
				new ServerSpell.Builder("Fortitude").castTime(0).spellEffects(auraEffect).requiresHostileTarget(false).
						heatContribution(80).ranges(0,
						30).uuid(uuidFactory.combUUID()).effectResource("abilities/fortitude").build());


		transaction.commit();
	}
}