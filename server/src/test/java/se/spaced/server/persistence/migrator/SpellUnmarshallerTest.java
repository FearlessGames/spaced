package se.spaced.server.persistence.migrator;

import org.junit.Before;
import org.junit.Test;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.impl.inmemory.*;

import static org.junit.Assert.*;

public class SpellUnmarshallerTest {

	private ServerXStreamUnmarshaller xStreamConverter;

	@Before
	public void setUp() throws Exception {
		xStreamConverter = new ServerXStreamUnmarshaller(new InMemorySpellDao(),
				new InMemoryItemTemplateDao(), new InMemoryLootTemplateDao(), new InMemoryCreatureTypeDao(),
				new InMemoryMobTemplateDao(), new InMemoryFactionDao(), new InMemoryBrainTemplateDao(),
				new InMemorySpawnPatternTemplateDao(), new InMemoryCooldownTemplateDao(),
				new InMemoryGraveyardTemplateDao(), new InMemoryCurrencyDao(), new InMemoryAuraDao());

	}

	@Test
	public void unmarshallSimpleSpell() throws Exception {
		String spellXml = "<spell>\n" +
				"    <pk>d325be33-bcd8-4aa0-b2c8-020baacd07a8</pk>\n" +
				"    <name>Simple spell</name>\n" +
				"    <castTime>100</castTime>\n" +
				"    <effects>\n" +
				"        <damageSchoolEffect>\n" +
				"                    <pk>49d14ffb-36e8-4ca0-915e-49bf11c95a2e</pk>\n" +
				"                    <resourceName>abilities/simple_spell</resourceName>\n" +
				"                    <school>FIRE</school>\n" +
				"                    <range>\n" +
				"                        <start>2</start>\n" +
				"                        <end>100</end>\n" +
				"                    </range>\n" +
				"        </damageSchoolEffect>\n" +
				"    </effects>\n" +
				"    <school>FIRE</school>\n" +
				"    <targetingType>TARGET</targetingType>\n" +
				"    <requiresHostileTarget>true</requiresHostileTarget>\n" +
				"    <cancelOnMove>true</cancelOnMove>\n" +
				"    <ranges>\n" +
				"        <start>0</start>\n" +
				"        <end>100</end>\n" +
				"    </ranges>\n" +
				"    <effectResource>abilities/simple_spell</effectResource>\n" +
				"    <heatContribution>1</heatContribution>\n" +
				"</spell>";
		ServerSpell spell = (ServerSpell) xStreamConverter.getXStream().fromXML(spellXml);
		assertNotNull(spell);
		assertEquals("Simple spell", spell.getName());
		assertTrue(spell.getRequiredAuras().isEmpty());
	}

	@Test
	public void unmarshallBuff() throws Exception {
		String spellXml = "<spell>\n" +
				"\t\t<pk>52b91584-b605-4058-a2de-9d9f39873cd6</pk>\n" +
				"\t\t<name>Shieldfield</name>\n" +
				"\t\t<castTime>0</castTime>\n" +
				"\t\t<effects>\n" +
				"\t\t\t<applyAuraEffect>\n" +
				"\t\t\t\t<pk>594bde30-f2c8-4595-8d54-9d9f39873cd6</pk>\n" +
				"\t\t\t\t<school>ELECTRICITY</school>\n" +
				"\t\t\t\t<serverAura class=\"modStatAura\">\n" +
				"\t\t\t\t\t<pk>55fecd2a-ec26-40f7-9880-9d9f39873cd6</pk>\n" +
				"\t\t\t\t\t<name>Shieldfield</name>\n" +
				"\t\t\t\t\t<visible>true</visible>\n" +
				"\t\t\t\t\t<iconPath>textures/gui/abilityicons/shieldfield</iconPath>\n" +
				"\t\t\t\t\t<duration>900000</duration>\n" +
				"\t\t\t\t\t<mods>\n" +
				"\t\t\t\t\t\t<modStat>\n" +
				"\t\t\t\t\t\t\t<amount>100.0</amount>\n" +
				"\t\t\t\t\t\t\t<statType>SHIELD_CHARGE</statType>\n" +
				"\t\t\t\t\t\t\t<operator>ADD</operator>\n" +
				"\t\t\t\t\t\t</modStat>\n" +
				"\t\t\t\t\t</mods>\n" +
				"\t\t\t\t</serverAura>\n" +
				"\t\t\t</applyAuraEffect>\n" +
				"\t\t\t<recoverEffect>\n" +
				"\t\t\t\t<pk>6601a8ca-6532-45c1-8e54-9f2d9fff7204</pk>\n" +
				"\t\t\t\t<resourceName>abilities/recharge</resourceName>\n" +
				"\t\t\t\t<school>ELECTRICITY</school>\n" +
				"\t\t\t\t<range>\n" +
				"\t\t\t\t\t<start>100</start>\n" +
				"\t\t\t\t\t<end>100</end>\n" +
				"\t\t\t\t</range>\n" +
				"\t\t\t</recoverEffect>\n" +
				"\t\t</effects>\n" +
				"\t\t<school>PHYSICAL</school>\n" +
				"\t\t<targetingType>SELF_ONLY</targetingType>\n" +
				"\t\t<requiresHostileTarget>false</requiresHostileTarget>\n" +
				"\t\t<cancelOnMove>false</cancelOnMove>\n" +
				"\t\t<ranges>\n" +
				"\t\t\t<start>0</start>\n" +
				"\t\t\t<end>30</end>\n" +
				"\t\t</ranges>\n" +
				"\t\t<effectResource>abilities/shieldfield</effectResource>\n" +
				"\t\t<heatContribution>95</heatContribution>\n" +
				"\t</spell>";

		ServerSpell spell = (ServerSpell) xStreamConverter.getXStream().fromXML(spellXml);
		assertNotNull(spell);
		assertEquals("Shieldfield", spell.getName());


	}
}
