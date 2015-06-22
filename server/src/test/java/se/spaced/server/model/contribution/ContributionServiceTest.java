package se.spaced.server.model.contribution;

import com.google.common.collect.Multiset;
import org.junit.Before;
import org.junit.Test;
import se.spaced.server.model.ServerEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class ContributionServiceTest {

	private ContributionServiceImpl contributionService;

	@Before
	public void setUp() throws Exception {
		contributionService = new ContributionServiceImpl();
	}

	private ServerEntity createEntity() {
		ServerEntity entity = mock(ServerEntity.class);
		contributionService.entityAdded(entity);
		return entity;
	}

	@Test
	public void contributorsIsEmptySetBeforeInteractions() throws Exception {
		ServerEntity mob = createEntity();
		Multiset<ServerEntity> contributorsBefore = contributionService.getContributors(mob);
		assertNotNull(contributorsBefore);
		assertTrue(contributorsBefore.isEmpty());
	}


	@Test
	public void addContribution() throws Exception {
		ServerEntity mob = createEntity();
		ServerEntity player = createEntity();
		contributionService.addContribution(mob, player, 3);

		Multiset<ServerEntity> contributors = contributionService.getContributors(mob);
		assertEquals(1, contributors.elementSet().size());

		assertTrue(contributors.contains(player));
	}

	@Test
	public void addContributionForMultiplePlayersOnMultipleMobs() throws Exception {
		ServerEntity mobA = createEntity();
		ServerEntity mobB = createEntity();
		ServerEntity mobC = createEntity();
		ServerEntity playerA = createEntity();
		ServerEntity playerB = createEntity();

		contributionService.addContribution(mobA, playerA, 10);
		contributionService.addContribution(mobB, playerA, 3);

		contributionService.addContribution(mobB, playerB, 7);
		contributionService.addContribution(mobC, playerB, 3);

		Multiset<ServerEntity> contributorsA = contributionService.getContributors(mobA);
		assertEquals(1, contributorsA.elementSet().size());

		Multiset<ServerEntity> contributorsB = contributionService.getContributors(mobB);
		assertEquals(2, contributorsB.elementSet().size());

		Multiset<ServerEntity> contributorsC = contributionService.getContributors(mobC);
		assertEquals(1, contributorsC.elementSet().size());
	}
}
