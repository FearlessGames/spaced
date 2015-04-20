package se.spaced.client.net.messagelisteners;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.mockachino.*;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.ClientSpellProxy;
import se.spaced.client.model.SpellDirectory;
import se.spaced.client.model.cooldown.ClientCooldown;
import se.spaced.client.model.cooldown.ClientCooldownServiceImpl;
import se.spaced.client.model.spelleffects.ClientGrantSpellEffect;
import se.spaced.client.model.spelleffects.ClientSpellEffect;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.messages.protocol.CooldownProxy;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;
import se.spaced.shared.network.protocol.codec.datatype.SpellEffect;
import se.spaced.shared.util.math.LinearTimeValue;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class SpellListenerTest {

	private SpellListener spellListener;
	private SpellDirectory spellDirectory;
	private ActiveCache<Spell, ClientSpell> spellCache;
	private UUIDMockFactory uuidMockFactory;
	private ActiveCache<Cooldown, ClientCooldown> cooldownCache;
	private ServerConnection serverConnection;

	@Before
	public void setUp() throws Exception {
		uuidMockFactory = new UUIDMockFactory();
		spellDirectory = mock(SpellDirectory.class);
		serverConnection = MockUtil.deepMock(ServerConnection.class);
		spellCache = new SpellCacheImpl(serverConnection);
		cooldownCache = new ClientCooldownServiceImpl(serverConnection);
		spellListener = new SpellListener(spellDirectory, spellCache, cooldownCache, new MockTimeProvider());
	}

	@Test
	public void spellAdded() throws Exception {
		ClientSpellProxy spell = new ClientSpellProxy(uuidMockFactory.combUUID());
		spellListener.spellAdded(spell);

		verifyOnce().on(spellDirectory).addSpellToSpellbook(spell);
	}

	@Test
	public void spellDataWithCooldowns() throws Exception {
		Collection<SpellData> data = Lists.newArrayList();
		Collection<Cooldown> cooldowns = Lists.newArrayList();
		final UUID cooldownId = uuidMockFactory.combUUID();
		Cooldown cooldown = new CooldownProxy(cooldownId);
		cooldowns.add(cooldown);
		Set<AuraTemplate> requiredAuras = Collections.emptySet();
		List<? extends SpellEffect> effects = Collections.emptyList();
		data.add(new SpellData(uuidMockFactory.combUUID(), "Fireball", 1000, MagicSchool.FIRE, true, new IntervalInt(10, 20), "somepath", 5, cooldowns, true,
				requiredAuras, effects));
		data.add(new SpellData(uuidMockFactory.combUUID(),
				"Frost bolt",
				2000,
				MagicSchool.FROST,
				true,
				new IntervalInt(20, 30),
				"somepath2",
				4,
				cooldowns,
				false,
				requiredAuras, effects));
		spellListener.spellData(data);

		assertEquals(0, spellCache.getValues().size());

		cooldownCache.setValue(cooldown, new ClientCooldown(new CooldownData(cooldownId, new LinearTimeValue(20))));
		assertEquals(2, spellCache.getValues().size());
	}

	@Test
	public void spellDataWithoutCooldowns() throws Exception {
		Collection<SpellData> data = Lists.newArrayList();

		data.add(new SpellData(uuidMockFactory.combUUID(), "Fireball", 1000, MagicSchool.FIRE, true, new IntervalInt(10, 20), "somepath", 5, true));
		data.add(new SpellData(uuidMockFactory.combUUID(), "Frost bolt", 2000, MagicSchool.FROST, true, new IntervalInt(20, 30), "somepath2", 4, false));
		spellListener.spellData(data);

		assertEquals(2, spellCache.getValues().size());
	}

	@Test
	public void spellDataWithCooldownsAndEffects() throws Exception {
		final Collection<SpellData> data = Lists.newArrayList();
		Collection<Cooldown> cooldowns = Lists.newArrayList();
		final UUID cooldownId = uuidMockFactory.combUUID();
		Cooldown cooldown = new CooldownProxy(cooldownId);
		cooldowns.add(cooldown);
		Set<AuraTemplate> requiredAuras = Collections.emptySet();
		final UUID learnedSpellId = uuidMockFactory.combUUID();
		ClientSpellEffect grantEffect = new ClientGrantSpellEffect(new ClientSpellProxy(learnedSpellId), uuidMockFactory.combUUID());
		List<? extends SpellEffect> effects = Lists.newArrayList(grantEffect);
		final UUID firstSpellId = uuidMockFactory.combUUID();
		data.add(new SpellData(firstSpellId, "Fireball", 1000, MagicSchool.FIRE, true, new IntervalInt(10, 20), "somepath", 5, cooldowns, true,
				requiredAuras, effects));


		String learnedSpellName = "Frost bolt";
		final SpellData learnedSpellData = new SpellData(learnedSpellId,
				learnedSpellName,
				2000,
				MagicSchool.FROST,
				true,
				new IntervalInt(20, 30),
				"somepath2",
				4,
				Collections.<Cooldown>emptyList(),
				false,
				Collections.<AuraTemplate>emptySet(), Collections.<SpellEffect>emptyList());
		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				Object[] arguments = call.getArguments();
				List<? extends Spell> spellList = (List<? extends Spell>) arguments[0];
				if (spellList.get(0).getPk().equals(learnedSpellId)) {
					Collection<SpellData> learnedSpellDataList = Lists.newArrayList(learnedSpellData);
					spellListener.spellData(learnedSpellDataList);
				}
				return null;
			}
		}).on(serverConnection.getReceiver().spell()).requestSpellInfo(any(List.class));

		spellListener.spellData(data);

		assertEquals(0, spellCache.getValues().size());

		cooldownCache.setValue(cooldown, new ClientCooldown(new CooldownData(cooldownId, new LinearTimeValue(20))));
		assertEquals(2, spellCache.getValues().size());
		ClientSpell cacheSpell = Iterables.find(spellCache.getValues(), new Predicate<ClientSpell>() {
			@Override
			public boolean apply(ClientSpell clientSpell) {
				return clientSpell.getPk().equals(firstSpellId);
			}
		});


		assertEquals(firstSpellId, cacheSpell.getPk());

		Collection<? extends SpellEffect> spellEffects = cacheSpell.getSpellEffects();
		assertEquals(1, spellEffects.size());

		ClientGrantSpellEffect spellEffect = (ClientGrantSpellEffect) spellEffects.iterator().next();
		ClientSpell spell = (ClientSpell) spellEffect.getSpell();
		assertEquals(learnedSpellId, spell.getPk());
		assertEquals(learnedSpellName, spell.getName());
	}

}
