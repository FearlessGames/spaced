package se.spaced.server.model.currency;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Player;
import se.spaced.server.persistence.DuplicateObjectException;

import static se.mockachino.Mockachino.verifyNever;
import static se.mockachino.Mockachino.verifyOnce;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.anyLong;

public class MoneyServiceImplTest extends ScenarioTestBase {

	private PersistedCurrency networkCredits;
	private PersistedCurrency republicanCredits;

	private Player player1;
	private Player player2;
	private S2CProtocol player1Receiver;
	private S2CProtocol player2Receiver;


	@Before
	public void setUp() throws Exception {
		player1Receiver = MockUtil.deepMock(S2CProtocol.class);
		player2Receiver = MockUtil.deepMock(S2CProtocol.class);

		networkCredits = new PersistedCurrency("Network credits");
		republicanCredits = new PersistedCurrency("Republican credits");

		player1 = playerFactory.createPlayer("Player1");
		player2 = playerFactory.createPlayer("Player2");

		entityService.addEntity(player1, player1Receiver);
		entityService.addEntity(player2, player2Receiver);
		moneyService.createWallet(player1);
		moneyService.createWallet(player2);
	}

	@Test
	public void awardMoneyToPlayer() throws Exception {
		moneyService.awardMoney(player1, new PersistedMoney(networkCredits, 5000L));

		verifyOnce().on(player1Receiver.trade()).playerMoneyUpdate(networkCredits.getName(), 5000L, 5000L);
		verifyNever().on(player2Receiver.trade()).playerMoneyUpdate(any(String.class), anyLong(), anyLong());

		moneyService.awardMoney(player1, new PersistedMoney(networkCredits, 100L));

		verifyOnce().on(player1Receiver.trade()).playerMoneyUpdate(networkCredits.getName(), 100L, 5100L);
	}

	@Test(expected = DuplicateObjectException.class)
	public void cantCreateMoreThanOneWallet() throws Exception {
		moneyService.createWallet(player1);
	}
}
