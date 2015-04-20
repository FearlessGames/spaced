package se.spaced.server.persistence.util;

import com.thoughtworks.xstream.XStream;
import se.spaced.server.loot.KofNLootTemplate;
import se.spaced.server.loot.LootTemplateProbability;
import se.spaced.server.loot.MoneyLootTemplate;
import se.spaced.server.loot.MultiLootTemplate;
import se.spaced.server.loot.SingleItemLootTemplate;
import se.spaced.server.mob.brains.templates.AggroingBrainTemplate;
import se.spaced.server.mob.brains.templates.AttackingBrainTemplate;
import se.spaced.server.mob.brains.templates.CompositeBrainTemplate;
import se.spaced.server.mob.brains.templates.NullBrainTemplate;
import se.spaced.server.mob.brains.templates.PatrollingBrainTemplate;
import se.spaced.server.mob.brains.templates.ProximityWhisperBrainTemplate;
import se.spaced.server.mob.brains.templates.RoamingBrainTemplate;
import se.spaced.server.mob.brains.templates.ScriptedBrainTemplate;
import se.spaced.server.mob.brains.templates.VendorBrainTemplate;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.aura.KeyAura;
import se.spaced.server.model.aura.ModStatAura;
import se.spaced.server.model.aura.PeriodicEffectAura;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.cooldown.CooldownSetTemplate;
import se.spaced.server.model.cooldown.CooldownTemplate;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.movement.TransportationMode;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.model.spawn.area.CompositeSpawnArea;
import se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.ApplyAuraEffect;
import se.spaced.server.model.spell.effect.CoolEffect;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.server.model.spell.effect.GrantSpellEffect;
import se.spaced.server.model.spell.effect.HealEffect;
import se.spaced.server.model.spell.effect.InterruptEffect;
import se.spaced.server.model.spell.effect.ProjectileEffect;
import se.spaced.server.model.spell.effect.RecoverEffect;
import se.spaced.server.model.spell.effect.SelfDamageSchoolEffect;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplate;
import se.spaced.server.persistence.dao.interfaces.Persistable;
import se.spaced.server.persistence.migrator.converters.HibernateSetConverter;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.world.walkmesh.Walkmesh;
import se.spaced.shared.world.walkmesh.WalkmeshConnection;
import se.spaced.shared.xml.SharedXStreamRegistry;


public class ServerXStreamRegistry {
	SharedXStreamRegistry sharedXStreamRegistry = new SharedXStreamRegistry();

	public void registerDefaultsOn(XStream xStream) {
		sharedXStreamRegistry.registerDefaultsOn(xStream);

		xStream.registerConverter(new HibernateSetConverter());
		xStream.registerConverter(new EnumDoubleMapConverter(TransportationMode.class));

		xStream.omitField(Effect.class, "smrtBroadcaster");
		xStream.omitField(ProjectileEffect.class, "actionScheduler");
		xStream.omitField(ProjectileEffect.class, "visibilityService");
		xStream.omitField(ProjectileEffect.class, "projectileIdCounter");
		xStream.omitField(DamageSchoolEffect.class, "spellCombatService");
		xStream.omitField(DamageSchoolEffect.class, "visibilityService");
		xStream.omitField(CoolEffect.class, "spellCombatService");
		xStream.omitField(CoolEffect.class, "visibilityService");
		xStream.omitField(HealEffect.class, "spellCombatService");
		xStream.omitField(HealEffect.class, "visibilityService");
		xStream.omitField(InterruptEffect.class, "spellCombatService");
		xStream.omitField(InterruptEffect.class, "visibilityService");
		xStream.omitField(SelfDamageSchoolEffect.class, "spellCombatService");
		xStream.omitField(SelfDamageSchoolEffect.class, "visibilityService");
		xStream.omitField(ApplyAuraEffect.class, "spellCombatService");
		xStream.omitField(ApplyAuraEffect.class, "visibilityService");
		xStream.omitField(ApplyAuraEffect.class, "auraService");
		xStream.omitField(Persistable.class, "persisted");
		xStream.omitField(MobSpawnTemplate.class, "mobOrderExecutor");


		xStream.alias("mob", MobTemplate.class);
		xStream.alias("creatureType", PersistedCreatureType.class);
		xStream.alias("faction", PersistedFaction.class);
		xStream.alias("spawnpattern", SpawnPatternTemplate.class);
		xStream.alias("mobspawn", MobSpawnTemplate.class);

		xStream.alias("spell", ServerSpell.class);
		xStream.alias("projectile", ProjectileEffect.class);
		xStream.alias("damageSchoolEffect", DamageSchoolEffect.class);
		xStream.alias("healEffect", HealEffect.class);
		xStream.alias("recoverEffect", RecoverEffect.class);
		xStream.alias("grantSpellEffect", GrantSpellEffect.class);
		xStream.alias("selfDamageSchoolEffect", SelfDamageSchoolEffect.class);
		xStream.alias("interruptEffect", InterruptEffect.class);
		xStream.alias("applyAuraEffect", ApplyAuraEffect.class);
		xStream.alias("coolEffect", CoolEffect.class);

		xStream.alias("serverAura", ServerAura.class);
		xStream.alias("modStatAura", ModStatAura.class);
		xStream.alias("keyAura", KeyAura.class);
		xStream.alias("periodicEffectAura", PeriodicEffectAura.class);

		xStream.alias("item", ServerItemTemplate.class);

		xStream.alias("takeK", KofNLootTemplate.class);
		xStream.alias("takeAll", MultiLootTemplate.class);
		xStream.alias("single", SingleItemLootTemplate.class);
		xStream.alias("probability", LootTemplateProbability.class);
		xStream.alias("money", MoneyLootTemplate.class);
		xStream.alias("currencyamount", PersistedMoney.class);

		xStream.alias("nullBrain", NullBrainTemplate.class);
		xStream.alias("compositeBrain", CompositeBrainTemplate.class);
		xStream.alias("scriptedBrain", ScriptedBrainTemplate.class);
		xStream.alias("patrollingBrain", PatrollingBrainTemplate.class);
		xStream.alias("roamingBrain", RoamingBrainTemplate.class);
		xStream.alias("attackingBrain", AttackingBrainTemplate.class);
		xStream.alias("aggroBrain", AggroingBrainTemplate.class);
		xStream.alias("proximityWhisperBrain", ProximityWhisperBrainTemplate.class);
		xStream.alias("vendorBrain", VendorBrainTemplate.class);

		xStream.alias("cooldowns", CooldownSetTemplate.class);
		xStream.alias("cooldown", CooldownTemplate.class);
		xStream.alias("graveyard", GraveyardTemplate.class);
		xStream.alias("currency", PersistedCurrency.class);

		xStream.alias("pointArea", SinglePointSpawnArea.class);
		xStream.alias("polygonArea", PolygonSpaceSpawnArea.class);
		xStream.alias("compositeArea", CompositeSpawnArea.class);

		xStream.alias("walkmesh", Walkmesh.class);
		xStream.alias("connection", WalkmeshConnection.class);


		xStream.alias("equipmentSlot", ContainerType.class);

		xStream.processAnnotations(CooldownSetTemplate.class);
		xStream.processAnnotations(SpawnPatternTemplate.class);


	}
}
