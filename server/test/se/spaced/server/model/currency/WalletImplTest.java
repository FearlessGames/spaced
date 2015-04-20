package se.spaced.server.model.currency;

import org.junit.Before;
import org.junit.Test;
import se.spaced.server.persistence.dao.interfaces.Persistable;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;


public class WalletImplTest {
	private PersistedCurrency networkCredits;
	private PersistedCurrency republicanCredits;
	private Wallet wallet;
	private WalletCompartment networkCompartment;
	private WalletCompartment republican;

	@Before
	public void setUp() throws Exception {
		networkCredits = mock(PersistedCurrency.class);
		republicanCredits = mock(PersistedCurrency.class);
		wallet = new Wallet(mock(Persistable.class));
		networkCompartment = wallet.getCompartment(networkCredits);
		republican = wallet.getCompartment(republicanCredits);
	}

	@Test
	public void testGetAmountForCurrency() throws Exception {

		assertEquals(0, networkCompartment.getMoney().getAmount());
		assertEquals(networkCredits, networkCompartment.getMoney().getCurrency());

		networkCompartment.add(new PersistedMoney(networkCredits, 1000L));

		assertEquals(1000L, networkCompartment.getMoney().getAmount());
		assertEquals(networkCredits, networkCompartment.getMoney().getCurrency());

		assertEquals(0, republican.getMoney().getAmount());
		assertEquals(republicanCredits, republican.getMoney().getCurrency());
	}

	@Test(expected = WrongCurrencyException.class)
	public void testMixingPearsAndApples() throws Exception {
		networkCompartment.add(new PersistedMoney(republicanCredits, 100L));
	}

	@Test(expected = MoneyUnderflowException.class)
	public void testNegativeMoney() throws Exception {
		networkCompartment.add(new PersistedMoney(networkCredits, -1L));
	}
}
