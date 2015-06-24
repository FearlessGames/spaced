package se.spaced.client.net;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import se.fearless.common.uuid.UUID;
import se.spaced.client.model.spelleffects.ClientGrantSpellEffect;
import se.spaced.client.model.spelleffects.ClientRangeSpellEffect;
import se.spaced.client.model.spelleffects.ClientSpellEffect;
import se.spaced.client.net.smrt.ServerToClientReadCodec;
import se.spaced.shared.model.EffectType;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpellEffectReader {

	private final ImmutableMap<Byte, EffectType> effectTypes = ImmutableMap.copyOf(Maps.uniqueIndex(Arrays.asList(EffectType.values()), new Function<EffectType, Byte>() {
		@Override
		public Byte apply(EffectType effectType) {
			return (byte) effectType.ordinal();
		}
	}));

	public List<ClientSpellEffect> readSpellEffectList(ServerToClientReadCodec readCodec, InputStream inputStream) throws IOException {
		byte size = readCodec.readByte(inputStream);
		if (size == 0) {
			return Collections.emptyList();
		}
		ArrayList<ClientSpellEffect> effects = Lists.newArrayListWithCapacity(size);
		for (int i = 0; i < size; i++) {
			readSpellEffect(readCodec, inputStream, effects);
		}
		return effects;
	}

	private void readSpellEffect(ServerToClientReadCodec readCodec, InputStream inputStream, ArrayList<ClientSpellEffect> effects) throws IOException {
		byte typeOrdinal = readCodec.readByte(inputStream);
		EffectType effectType = effectTypes.get(typeOrdinal);
		UUID pk = readCodec.readUUID(inputStream);
		switch (effectType) {
			case GRANT_SPELL:
				effects.add(new ClientGrantSpellEffect(readCodec.readSpell(inputStream), pk));
				return;
			case DAMAGE:
			case HEAL:
			case COOL:
			case RECOVER:
			case SELF_DAMAGE:
				ClientRangeSpellEffect recoverEffect = readRangeEffect(readCodec, inputStream, effectType, pk);
				effects.add(recoverEffect);
				return;
			case PROJECTILE:
				byte size = readCodec.readByte(inputStream);
				for (int i = 0; i < size; i++) {
					readSpellEffect(readCodec, inputStream, effects);
				}
				return;
			case UNKNOWN:
				effects.add(new ClientSpellEffect(EffectType.UNKNOWN, pk));
				return;
		}
		throw new IllegalStateException("Effect type didn't match any value. typeOrdinal=" + typeOrdinal);
	}

	private ClientRangeSpellEffect readRangeEffect(ServerToClientReadCodec readCodec, InputStream inputStream, EffectType effectType, UUID pk) throws IOException {
		IntervalInt range = readRange(readCodec, inputStream);
		MagicSchool school = readCodec.readMagicSchool(inputStream);
		return new ClientRangeSpellEffect(effectType, range, pk, school);
	}


	private IntervalInt readRange(ServerToClientReadCodec readCodec, InputStream inputStream) throws IOException {
		int start = readCodec.readInt(inputStream);
		int end = readCodec.readInt(inputStream);
		return new IntervalInt(start, end);
	}
}
