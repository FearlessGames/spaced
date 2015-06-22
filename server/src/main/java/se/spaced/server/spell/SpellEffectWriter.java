package se.spaced.server.spell;

import com.google.common.collect.ImmutableMap;
import se.spaced.server.model.spell.effect.CoolEffect;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.server.model.spell.effect.GrantSpellEffect;
import se.spaced.server.model.spell.effect.HealEffect;
import se.spaced.server.model.spell.effect.ProjectileEffect;
import se.spaced.server.model.spell.effect.RangeableEffect;
import se.spaced.server.model.spell.effect.RecoverEffect;
import se.spaced.server.model.spell.effect.SelfDamageSchoolEffect;
import se.spaced.server.net.mina.ServerToClientRequiredWriteCodec;
import se.spaced.shared.model.EffectType;
import se.spaced.shared.network.protocol.codec.datatype.SpellEffect;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;

public class SpellEffectWriter {

	private final ImmutableMap<Class<? extends Effect>, EffectType> effectTypeMap = ImmutableMap.<Class<? extends Effect>, EffectType>builder().
			put(DamageSchoolEffect.class, EffectType.DAMAGE).
			put(HealEffect.class, EffectType.HEAL).
			put(CoolEffect.class, EffectType.COOL).
			put(RecoverEffect.class, EffectType.RECOVER).
			put(SelfDamageSchoolEffect.class, EffectType.SELF_DAMAGE).
			put(ProjectileEffect.class, EffectType.PROJECTILE).
			put(GrantSpellEffect.class, EffectType.GRANT_SPELL).build();

	public void writeSpellEffectList(ServerToClientRequiredWriteCodec codec, OutputStream outputStream, Collection<? extends SpellEffect> effects) throws IOException {
		codec.writeByte(outputStream, (byte) effects.size());
		for (SpellEffect effect : effects) {
			writeSpellEffect(codec, outputStream, effect);
		}
	}

	private void writeSpellEffect(ServerToClientRequiredWriteCodec codec, OutputStream outputStream, SpellEffect effect) throws IOException {
		EffectType effectType = effectTypeMap.get(effect.getClass());
		if (effectType == null) {
			effectType = EffectType.UNKNOWN;
		}
		codec.writeByte(outputStream, (byte) effectType.ordinal());
		codec.writeUUID(outputStream, effect.getPk());

		switch (effectType) {
			case GRANT_SPELL:
				GrantSpellEffect grant = (GrantSpellEffect) effect;
				codec.writeSpell(outputStream, grant.getSpell());
				break;
			case DAMAGE:
			case HEAL:
			case COOL:
			case RECOVER:
			case SELF_DAMAGE:
				RangeableEffect rangeableEffect = (RangeableEffect) effect;
				writeRangeAndSchool(codec, outputStream, rangeableEffect);
				break;
			case PROJECTILE:
				ProjectileEffect projectileEffect = (ProjectileEffect) effect;
				Set<Effect> impactEffects = projectileEffect.getImpactEffects();
				codec.writeByte(outputStream, (byte) impactEffects.size());
				for (Effect impactEffect : impactEffects) {
					writeSpellEffect(codec, outputStream, impactEffect);
				}
				break;
			case UNKNOWN:
				break;
		}
	}

	private void writeRangeAndSchool(
			ServerToClientRequiredWriteCodec codec,
			OutputStream outputStream,
			RangeableEffect rangeableEffect) throws IOException {
		codec.writeInt(outputStream, rangeableEffect.getRange().getStart());
		codec.writeInt(outputStream, rangeableEffect.getRange().getEnd());
		codec.writeMagicSchool(outputStream, rangeableEffect.getSchool());
	}
}
