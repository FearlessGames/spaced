package se.fearless.effects;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.model.spelleffects.ClientGrantSpellEffect;
import se.spaced.client.model.spelleffects.ClientRangeSpellEffect;
import se.spaced.client.model.spelleffects.ClientSpellEffect;
import se.spaced.client.net.SpellEffectReader;
import se.spaced.client.net.smrt.ServerToClientReadCodec;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.server.model.spell.effect.GrantSpellEffect;
import se.spaced.server.model.spell.effect.ProjectileEffect;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.net.mina.ServerToClientRequiredWriteCodec;
import se.spaced.server.spell.SpellEffectWriter;
import se.spaced.shared.model.EffectType;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.network.protocol.codec.SharedCodec;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.fearless.CodecUtils.getInputStream;
import static se.mockachino.Mockachino.*;

public class EffectCodecTest {

	private SpellEffectWriter writer;
	private ServerToClientRequiredWriteCodec writeCodec;
	private ByteArrayOutputStream outputStream;

	private ServerToClientReadCodec readCodec;
	private SpellEffectReader reader;
	private UUIDFactory uuidFactory;

	@Before
	public void setUp() throws Exception {
		uuidFactory = new UUIDMockFactory();
		outputStream = new ByteArrayOutputStream(1024);

		writer = new SpellEffectWriter();
		MockTimeProvider timeProvider = new MockTimeProvider();
		SharedCodec sharedCodec = new SharedCodec(timeProvider);

		writeCodec = new ServerToClientRequiredWriteCodec(sharedCodec, timeProvider);

		reader = new SpellEffectReader();

		readCodec = new ServerToClientReadCodec(sharedCodec, mock(InventoryProvider.class), timeProvider);
	}

	@Test
	public void emptyList() throws Exception {
		writer.writeSpellEffectList(writeCodec, outputStream, Lists.<Effect>newArrayList());
		InputStream inputStream = getInputStream(outputStream);
		List<ClientSpellEffect> readEffects = reader.readSpellEffectList(readCodec, inputStream);
		assertNotNull(readEffects);
		assertEquals(0, readEffects.size());
	}

	@Test
	public void grantSpellEffect() throws Exception {
		UUID uuid = uuidFactory.randomUUID();
		Effect grantSpellEffect = new GrantSpellEffect(null, null, new ServerSpell.Builder("SpellName").uuid(uuid).build());
		UUID effectPk = uuidFactory.randomUUID();
		grantSpellEffect.setPk(effectPk);
		List<Effect> effects = Lists.newArrayList(grantSpellEffect);
		writer.writeSpellEffectList(writeCodec, outputStream, effects);

		InputStream inputStream = getInputStream(outputStream);
		List<ClientSpellEffect> readEffects = reader.readSpellEffectList(readCodec, inputStream);
		assertEquals(1, readEffects.size());

		ClientSpellEffect clientSpellEffect = readEffects.get(0);
		assertEquals(EffectType.GRANT_SPELL, clientSpellEffect.getType());
		ClientGrantSpellEffect grant = (ClientGrantSpellEffect) clientSpellEffect;
		assertEquals(effectPk, grant.getPk());
		assertEquals(uuid, grant.getSpell().getPk());
	}

	@Test
	public void damageSchoolEffect() throws Exception {
		IntervalInt damageRange = new IntervalInt(40, 50);
		MagicSchool school = MagicSchool.FROST;
		Effect damageSchoolEffect = new DamageSchoolEffect(mock(SpellCombatService.class), mock(SmrtBroadcasterImpl.class),
				damageRange, school);
		UUID effectPk = uuidFactory.randomUUID();
		damageSchoolEffect.setPk(effectPk);

		List<Effect> effects = Lists.newArrayList(damageSchoolEffect);
		writer.writeSpellEffectList(writeCodec, outputStream, effects);

		InputStream inputStream = getInputStream(outputStream);
		List<ClientSpellEffect> readEffects = reader.readSpellEffectList(readCodec, inputStream);
		assertEquals(1, readEffects.size());

		ClientSpellEffect clientSpellEffect = readEffects.get(0);
		assertEquals(EffectType.DAMAGE, clientSpellEffect.getType());
		ClientRangeSpellEffect clientEffect = (ClientRangeSpellEffect) clientSpellEffect;
		assertEquals(effectPk, clientEffect.getPk());
		assertEquals(damageRange, clientEffect.getRange());
		assertEquals(school, clientEffect.getSchool());
	}

	@Test
	public void damageSchoolEffectWrappedInProjectile() throws Exception {
		IntervalInt damageRange = new IntervalInt(40, 50);
		MagicSchool school = MagicSchool.FROST;
		Effect damageSchoolEffect = new DamageSchoolEffect(mock(SpellCombatService.class), mock(SmrtBroadcasterImpl.class),
				damageRange, school);
		UUID effectPk = uuidFactory.randomUUID();
		damageSchoolEffect.setPk(effectPk);
		ProjectileEffect projectile = new ProjectileEffect(mock(ActionScheduler.class),
				mock(SmrtBroadcasterImpl.class),
				mock(AtomicInteger.class)
		);
		projectile.setPk(uuidFactory.randomUUID());
		projectile.addImpactEffect(damageSchoolEffect);

		List<Effect> effects = Lists.newArrayList((Effect) projectile);
		writer.writeSpellEffectList(writeCodec, outputStream, effects);

		InputStream inputStream = getInputStream(outputStream);
		List<ClientSpellEffect> readEffects = reader.readSpellEffectList(readCodec, inputStream);
		assertEquals(1, readEffects.size());

		ClientSpellEffect clientSpellEffect = readEffects.get(0);
		assertEquals(EffectType.DAMAGE, clientSpellEffect.getType());
		ClientRangeSpellEffect clientEffect = (ClientRangeSpellEffect) clientSpellEffect;
		assertEquals(effectPk, clientEffect.getPk());
		assertEquals(damageRange, clientEffect.getRange());
		assertEquals(school, clientEffect.getSchool());
	}

	@Test
	public void multipleWrappedInProjectile() throws Exception {
		IntervalInt damageRange = new IntervalInt(40, 50);
		MagicSchool school = MagicSchool.FROST;
		Effect damageSchoolEffect = new DamageSchoolEffect(mock(SpellCombatService.class), mock(SmrtBroadcasterImpl.class),
				damageRange, school);
		final UUID effectPk = uuidFactory.randomUUID();
		damageSchoolEffect.setPk(effectPk);

		IntervalInt damageRange2 = new IntervalInt(4, 5);
		Effect damageSchoolEffect2 = new DamageSchoolEffect(mock(SpellCombatService.class), mock(SmrtBroadcasterImpl.class),
				damageRange2, school);
		final UUID effectPk2 = uuidFactory.randomUUID();
		damageSchoolEffect2.setPk(effectPk2);

		ProjectileEffect projectile = new ProjectileEffect(mock(ActionScheduler.class),
				mock(SmrtBroadcasterImpl.class),
				mock(AtomicInteger.class)
		);
		projectile.setPk(uuidFactory.randomUUID());
		projectile.addImpactEffect(damageSchoolEffect);
		projectile.addImpactEffect(damageSchoolEffect2);

		List<Effect> effects = Lists.newArrayList((Effect) projectile);
		writer.writeSpellEffectList(writeCodec, outputStream, effects);

		InputStream inputStream = getInputStream(outputStream);
		List<ClientSpellEffect> readEffects = reader.readSpellEffectList(readCodec, inputStream);
		assertEquals(2, readEffects.size());

		ClientSpellEffect clientSpellEffect = Iterables.find(readEffects, new Predicate<ClientSpellEffect>() {
			@Override
			public boolean apply(ClientSpellEffect clientSpellEffect) {
				return clientSpellEffect.getPk().equals(effectPk);
			}
		});
		assertEquals(EffectType.DAMAGE, clientSpellEffect.getType());
		ClientRangeSpellEffect clientEffect = (ClientRangeSpellEffect) clientSpellEffect;
		assertEquals(effectPk, clientEffect.getPk());
		assertEquals(damageRange, clientEffect.getRange());
		assertEquals(school, clientEffect.getSchool());

		ClientSpellEffect clientSpellEffect2 = Iterables.find(readEffects, new Predicate<ClientSpellEffect>() {
			@Override
			public boolean apply(ClientSpellEffect clientSpellEffect) {
				return clientSpellEffect.getPk().equals(effectPk2);
			}
		});
		assertEquals(EffectType.DAMAGE, clientSpellEffect2.getType());
		ClientRangeSpellEffect clientEffect2 = (ClientRangeSpellEffect) clientSpellEffect2;
		assertEquals(effectPk2, clientEffect2.getPk());
		assertEquals(damageRange2, clientEffect2.getRange());
		assertEquals(school, clientEffect2.getSchool());

	}

	@Test
	public void unknownEffect() throws Exception {
		Effect effect = new Effect(mock(SmrtBroadcasterImpl.class)) {
			@Override
			public void apply(long now, ServerEntity performer, ServerEntity target, String causeName) {
			}

			@Override
			public void fail(long now, ServerEntity performer, ServerEntity target, String causeName) {
			}
		};
		effect.setPk(uuidFactory.randomUUID());
		List<Effect> effects = Lists.newArrayList(effect);
		writer.writeSpellEffectList(writeCodec, outputStream, effects);

		InputStream inputStream = getInputStream(outputStream);
		List<ClientSpellEffect> readEffects = reader.readSpellEffectList(readCodec, inputStream);

		assertEquals(1, readEffects.size());
		assertEquals(EffectType.UNKNOWN, readEffects.get(0).getType());
	}
}
