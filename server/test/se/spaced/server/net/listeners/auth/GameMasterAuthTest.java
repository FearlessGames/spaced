package se.spaced.server.net.listeners.auth;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.Player;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.services.auth.PlayerAuthenticationProxyWrapper;
import se.spaced.server.services.auth.PlayerAuthenticator;

import static org.junit.Assert.fail;
import static se.mockachino.Mockachino.*;

public class GameMasterAuthTest extends ScenarioTestBase {

	private Player gm;
	private GameMasterApi gameMasterApi;
	private GameMasterApi innerApi;
	private Player krka;

	@Before
	public void setUp() throws Exception {
		SpacedRotation gmRotation = new SpacedRotation(2, 4, 3, 1, true);

		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();

		gm = factory.createPlayer("Gm");
		gm.setPositionalData(new PersistedPositionalData(new SpacedVector3(10, 20, 30), gmRotation));

		SpacedRotation krkaRotation = new SpacedRotation(2, 4, 3, 4, true);

		krka = factory.createPlayer("krka");
		krka.setPositionalData(new PersistedPositionalData(new SpacedVector3(1000, 2000, 3000), krkaRotation));


		innerApi = mock(GameMasterApi.class);

		gameMasterApi = PlayerAuthenticationProxyWrapper.wrap(innerApi, new PlayerAuthenticator() {
			@Override
			public boolean authenticate(Player player) {
				return gm.equals(player);
			}
		});

	}

	@Test
	public void gmGiveMoney() throws Exception {
		String playerName = "foo";
		String currencyName = "bar";
		long amount = 100L;
		gameMasterApi.giveMoney(gm, playerName, currencyName, amount);
		verifyOnce().on(innerApi).giveMoney(gm, playerName, currencyName, amount);
	}

	@Test
	public void krkaGiveMoney() throws Throwable {
		String playerName = "foo";
		String currencyName = "bar";
		long amount = 100L;
		try {
			gameMasterApi.giveMoney(krka, playerName, currencyName, amount);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).giveMoney(krka, playerName, currencyName, amount);
		}
	}

	@Test
	public void gmGrantSpell() throws Exception {
		String playerName = "foo";
		String spellName = "bar";
		gameMasterApi.grantSpell(gm, playerName, spellName);
		verifyOnce().on(innerApi).grantSpell(gm, playerName, spellName);
	}

	@Test
	public void krkaGrantSpell() throws Exception {
		String playerName = "foo";
		String spellName = "bar";
		try {
			gameMasterApi.grantSpell(krka, playerName, spellName);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).grantSpell(krka, playerName, spellName);
		}
	}

	@Test
	public void gmGiveItem() throws Exception {
		String playerName = "foo";
		String itemName = "bar";
		gameMasterApi.giveItem(gm, playerName, itemName, 5);
		verifyOnce().on(innerApi).giveItem(gm, playerName, itemName, 5);
	}

	@Test
	public void krkaGiveItem() throws Exception {
		String playerName = "foo";
		String itemName = "bar";
		try {
			gameMasterApi.giveItem(krka, playerName, itemName, 5);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).giveItem(krka, playerName, itemName, 5);
		}
	}

	@Test
	public void gmSpawnMob() throws Exception {
		String mobName = "bar";
		String brainName = "bar";
		gameMasterApi.spawnMob(gm, mobName, brainName);
		verifyOnce().on(innerApi).spawnMob(gm, mobName, brainName);
	}

	@Test
	public void krkaSpawnMob() throws Exception {
		String mobName = "foo";
		String brainName = "bar";
		try {
			gameMasterApi.spawnMob(krka, mobName, brainName);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).spawnMob(krka, mobName, brainName);
		}
	}


	@Test
	public void gmSummon() throws Exception {
		String playerName = "foo";
		gameMasterApi.summonEntity(gm, playerName);
		verifyOnce().on(innerApi).summonEntity(gm, playerName);
	}

	@Test
	public void krkaSummon() throws Exception {
		String playerName = "foo";
		try {
			gameMasterApi.summonEntity(krka, playerName);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).summonEntity(krka, playerName);
		}
	}

	@Test
	public void gmReloadMob() throws Exception {
		gameMasterApi.reloadMob(gm, krka);
		verifyOnce().on(innerApi).reloadMob(gm, krka);
	}

	@Test
	public void krkaReloadMob() throws Exception {
		try {
			gameMasterApi.reloadMob(krka, gm);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).reloadMob(krka, gm);
		}
	}

	@Test
	public void gmReloadContent() throws Exception {
		gameMasterApi.reloadServerContent(gm);
		verifyOnce().on(innerApi).reloadServerContent(gm);
	}

	@Test
	public void krkaReloadContent() throws Exception {
		try {
			gameMasterApi.reloadServerContent(krka);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).reloadServerContent(krka);
		}
	}

	@Test
	public void gmVisit() throws Exception {
		String playerName = "foo";
		gameMasterApi.visit(gm, playerName);
		verifyOnce().on(innerApi).visit(gm, playerName);
	}

	@Test
	public void krkaVisit() throws Exception {
		String playerName = "foo";
		try {
			gameMasterApi.visit(krka, playerName);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).visit(krka, playerName);
		}
	}

	@Test
	public void gmAiInfo() throws Exception {
		gameMasterApi.requestAiInfo(gm, krka);
		verifyOnce().on(innerApi).requestAiInfo(gm, krka);
	}

	@Test
	public void krkaAiInfo() throws Exception {
		try {
			gameMasterApi.requestAiInfo(krka, gm);
			fail();
		} catch (Exception e) {
			verifyNever().on(innerApi).requestAiInfo(krka, gm);
		}
	}




}
