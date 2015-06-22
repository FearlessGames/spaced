package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.cfg.Configuration;
import se.spaced.server.account.Account;
import se.spaced.server.loot.EmptyLootTemplate;
import se.spaced.server.loot.KofNLootTemplate;
import se.spaced.server.loot.LootTemplateProbability;
import se.spaced.server.loot.MoneyLootTemplate;
import se.spaced.server.loot.MultiLootTemplate;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.loot.SingleItemLootTemplate;
import se.spaced.server.mob.brains.templates.AggroingBrainTemplate;
import se.spaced.server.mob.brains.templates.AttackingBrainTemplate;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.mob.brains.templates.CompositeBrainTemplate;
import se.spaced.server.mob.brains.templates.HealingBrainTemplate;
import se.spaced.server.mob.brains.templates.NullBrainTemplate;
import se.spaced.server.mob.brains.templates.PatrollingBrainTemplate;
import se.spaced.server.mob.brains.templates.ProximityWhisperBrainTemplate;
import se.spaced.server.mob.brains.templates.RoamingBrainTemplate;
import se.spaced.server.mob.brains.templates.ScriptedBrainTemplate;
import se.spaced.server.mob.brains.templates.VendorBrainTemplate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aura.KeyAura;
import se.spaced.server.model.aura.ModStatAura;
import se.spaced.server.model.aura.PeriodicEffectAura;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.cooldown.CooldownSetTemplate;
import se.spaced.server.model.cooldown.CooldownTemplate;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.currency.Wallet;
import se.spaced.server.model.currency.WalletCompartment;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.model.items.PersistedInventory;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemStack;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.PlayerTemplate;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.model.spawn.WhisperMessage;
import se.spaced.server.model.spawn.area.CompositeSpawnArea;
import se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea;
import se.spaced.server.model.spawn.area.RandomSpaceSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.schedule.SpawnScheduleTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.SpellBook;
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
import se.spaced.server.stats.KillEntry;
import se.spaced.server.stats.SpellActionEntry;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.stats.StatData;

public class StandardClassAnnotator {
	private StandardClassAnnotator() {
	}

	public static void annotateClasses(Configuration config) {
		config.addAnnotatedClass(ServerEntity.class);
		config.addAnnotatedClass(EntityTemplate.class);
		config.addAnnotatedClass(PlayerTemplate.class);
		config.addAnnotatedClass(MobTemplate.class);

		config.addAnnotatedClass(PersistedCreatureType.class);
		config.addAnnotatedClass(PersistedFaction.class);
		config.addAnnotatedClass(Account.class);
		config.addAnnotatedClass(Player.class);
		config.addAnnotatedClass(Mob.class);
		config.addAnnotatedClass(PersistedPositionalData.class);
		config.addAnnotatedClass(PersistedAppearanceData.class);
		config.addAnnotatedClass(StatData.class);
		config.addAnnotatedClass(ServerSpell.class);
		config.addAnnotatedClass(SpellBook.class);
		config.addAnnotatedClass(Effect.class);
		config.addAnnotatedClass(ProjectileEffect.class);
		config.addAnnotatedClass(DamageSchoolEffect.class);
		config.addAnnotatedClass(SelfDamageSchoolEffect.class);
		config.addAnnotatedClass(InterruptEffect.class);
		config.addAnnotatedClass(HealEffect.class);
		config.addAnnotatedClass(RecoverEffect.class);
		config.addAnnotatedClass(CoolEffect.class);
		config.addAnnotatedClass(ApplyAuraEffect.class);
		config.addAnnotatedClass(GrantSpellEffect.class);
		config.addAnnotatedClass(ServerAura.class);
		config.addAnnotatedClass(ModStatAura.class);
		config.addAnnotatedClass(KeyAura.class);
		config.addAnnotatedClass(PeriodicEffectAura.class);
		config.addAnnotatedClass(ModStat.class);
		config.addAnnotatedClass(ServerItem.class);
		config.addAnnotatedClass(ServerItemStack.class);
		config.addAnnotatedClass(ServerItemTemplate.class);

		config.addAnnotatedClass(Wallet.class);
		config.addAnnotatedClass(WalletCompartment.class);
		config.addAnnotatedClass(PersistedMoney.class);
		config.addAnnotatedClass(PersistedCurrency.class);

		config.addAnnotatedClass(CooldownTemplate.class);
		config.addAnnotatedClass(CooldownSetTemplate.class);
		config.addAnnotatedClass(PersistableLootTemplate.class);
		config.addAnnotatedClass(MultiLootTemplate.class);
		config.addAnnotatedClass(KofNLootTemplate.class);
		config.addAnnotatedClass(SingleItemLootTemplate.class);
		config.addAnnotatedClass(MoneyLootTemplate.class);
		config.addAnnotatedClass(LootTemplateProbability.class);
		config.addAnnotatedClass(EmptyLootTemplate.class);


		config.addAnnotatedClass(NullBrainTemplate.class);
		config.addAnnotatedClass(PatrollingBrainTemplate.class);
		config.addAnnotatedClass(ProximityWhisperBrainTemplate.class);
		config.addAnnotatedClass(RoamingBrainTemplate.class);
		config.addAnnotatedClass(RandomSpaceSpawnArea.class);
		config.addAnnotatedClass(AttackingBrainTemplate.class);
		config.addAnnotatedClass(HealingBrainTemplate.class);
		config.addAnnotatedClass(AggroingBrainTemplate.class);
		config.addAnnotatedClass(ScriptedBrainTemplate.class);
		config.addAnnotatedClass(VendorBrainTemplate.class);
		config.addAnnotatedClass(CompositeBrainTemplate.class);
		config.addAnnotatedClass(SpawnPatternTemplate.class);
		config.addAnnotatedClass(MobSpawnTemplate.class);
		config.addAnnotatedClass(SinglePointSpawnArea.class);
		config.addAnnotatedClass(CompositeSpawnArea.class);
		config.addAnnotatedClass(PolygonSpaceSpawnArea.class);
		config.addAnnotatedClass(SpawnScheduleTemplate.class);
		config.addAnnotatedClass(EquippedItems.class);
		config.addAnnotatedClass(WhisperMessage.class);


		config.addAnnotatedClass(Wallet.class);
		config.addAnnotatedClass(WalletCompartment.class);
		config.addAnnotatedClass(PersistedMoney.class);
		config.addAnnotatedClass(PersistedCurrency.class);

		config.addAnnotatedClass(BrainTemplate.class);
		config.addAnnotatedClass(GraveyardTemplate.class);

		config.addAnnotatedClass(PersistedInventory.class);


		config.addAnnotatedClass(KillEntry.class);
		config.addAnnotatedClass(SpellActionEntry.class);
	}
}

