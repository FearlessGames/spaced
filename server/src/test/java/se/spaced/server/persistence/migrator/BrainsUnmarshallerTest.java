package se.spaced.server.persistence.migrator;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.mob.brains.templates.CompositeBrainTemplate;
import se.spaced.server.mob.brains.templates.ProximityWhisperBrainTemplate;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryAuraDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryBrainTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryCooldownTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryCreatureTypeDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryCurrencyDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryFactionDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryGraveyardTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryItemTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryLootTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryMobTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemorySpawnPatternTemplateDao;
import se.spaced.server.persistence.dao.impl.inmemory.InMemorySpellDao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BrainsUnmarshallerTest {
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
	public void unmarshallBrain() throws Exception {
		ProximityWhisperBrainTemplate brain = (ProximityWhisperBrainTemplate) xStreamConverter.getXStream().fromXML("\t<proximityWhisperBrain>\n" +
				"\t\t<pk>573bb203-ddca-4b44-b1c2-9e8196b474f3</pk>\n" +
				"\t\t<name>whisper-brain</name>\n" +
				"\t</proximityWhisperBrain>");
		assertNotNull(brain);
		UUID pk = brain.getPk();
		assertEquals(UUID.fromString("573bb203-ddca-4b44-b1c2-9e8196b474f3"), pk);
	}

	@Test
	public void compositeBrain() throws Exception {
		CompositeBrainTemplate brain = (CompositeBrainTemplate) xStreamConverter.getXStream().fromXML("\t<compositeBrain>\n" +
				"\t\t<pk>603fefe3-5140-4ccd-a980-9e8196903272</pk>\n" +
				"\t\t<name>attacking-aggro-proximitywhisper-roaming-brain</name>\n" +
				"\t\t<brains>\n" +
				"\t\t\t<attackingBrain>\n" +
				"\t\t\t\t<pk>2a36fc44-1677-45d5-af88-9e8196906ce4</pk>\n" +
				"\t\t\t</attackingBrain>\n" +
				"\t\t\t<aggroBrain>\n" +
				"\t\t\t\t<pk>ce968ed7-7a64-4173-aaba-9e81969068ba</pk>\n" +
				"\t\t\t</aggroBrain>\n" +
				"\t\t\t<proximityWhisperBrain>\n" +
				"\t\t\t\t<pk>7662e1eb-ee99-451e-9f8d-9e819690e3a9</pk>\n" +
				"\t\t\t</proximityWhisperBrain>\n" +
				"\t\t\t<roamingBrain>\n" +
				"\t\t\t\t<pk>b731e78a-cc78-4077-8f19-9e81969072f5</pk>\n" +
				"\t\t\t</roamingBrain>\n" +
				"\t\t</brains>\n" +
				"\t</compositeBrain>");

		assertNotNull(brain);

		assertEquals("attacking-aggro-proximitywhisper-roaming-brain", brain.getName());
	}
}
