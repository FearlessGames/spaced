package se.spaced.client.model;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.messages.protocol.ClientAuraInstance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class ClientAuraServiceImplTest {

	private ClientAuraServiceImpl clientAuraService;
	private ClientEntity player;

	@Before
	public void setUp() throws Exception {
		PlayerEntityProvider provider = new PlayerEntityProvider();
		player = mock(ClientEntity.class);
		provider.setPlayerEntity(player);
		clientAuraService = new ClientAuraServiceImpl(provider);
	}

	@Test
	public void addAura() throws Exception {
		ClientEntity entity = mock(ClientEntity.class);
		ClientAuraInstance aura = mock(ClientAuraInstance.class);
		clientAuraService.applyAura(entity, aura);

		assertTrue(clientAuraService.getAuras(entity).contains(aura));

		assertFalse(clientAuraService.getAuras(mock(ClientEntity.class)).contains(aura));
		assertFalse(clientAuraService.getAuras(entity).contains(mock(ClientAuraInstance.class)));
	}

	@Test
	public void removeAura() throws Exception {
		ClientEntity entity1 = mock(ClientEntity.class);
		ClientAuraInstance aura1 = mock(ClientAuraInstance.class);

		ClientEntity entity2 = mock(ClientEntity.class);
		ClientAuraInstance aura2 = mock(ClientAuraInstance.class);

		clientAuraService.applyAura(entity1, aura1);
		clientAuraService.applyAura(entity2, aura1);

		clientAuraService.applyAura(entity1, aura2);

		clientAuraService.removeAura(entity1, aura1);

		assertFalse(clientAuraService.getAuras(entity1).contains(aura1));
		assertTrue(clientAuraService.getAuras(entity1).contains(aura2));
		assertTrue(clientAuraService.getAuras(entity2).contains(aura1));
		assertFalse(clientAuraService.getAuras(entity2).contains(aura2));
	}

	@Test
	public void selfHasAura() throws Exception {

		ClientAuraInstance aura1 = mock(ClientAuraInstance.class);
		ClientAuraInstance aura2 = mock(ClientAuraInstance.class);
		clientAuraService.applyAura(player, aura1);

		assertTrue(clientAuraService.selfHasAura(aura1));
		assertFalse(clientAuraService.selfHasAura(aura2));
	}

	@Test
	public void visibleAuras() throws Exception {
		ClientAuraInstance aura1 = mock(ClientAuraInstance.class);
		when(aura1.isVisible()).thenReturn(true);
		ClientAuraInstance aura2 = mock(ClientAuraInstance.class);
		when(aura2.isVisible()).thenReturn(false);
		ClientAuraInstance aura3 = mock(ClientAuraInstance.class);
		when(aura3.isVisible()).thenReturn(true);

		clientAuraService.applyAura(player, aura1);
		clientAuraService.applyAura(player, aura2);
		clientAuraService.applyAura(player, aura3);

		ImmutableSet<ClientAuraInstance> visibleAuras = clientAuraService.getVisibleAuras(player);
		assertEquals(2, visibleAuras.size());

		assertTrue(visibleAuras.contains(aura1));
		assertFalse(visibleAuras.contains(aura2));
		assertTrue(visibleAuras.contains(aura3));
	}
}
