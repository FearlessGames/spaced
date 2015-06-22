package se.spaced.server.model.currency;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.dao.interfaces.CurrencyDao;
import se.spaced.server.persistence.dao.interfaces.WalletDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class MoneyServiceImpl implements MoneyService {
	private static final Function<PersistedCurrency, String> GET_CURRENCY_NAME = new Function<PersistedCurrency, String>() {
		@Override
		public String apply(PersistedCurrency currency) {
			return currency.getName();
		}
	};
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final WalletDao walletDao;
	private final CurrencyDao currencyDao;

	@Inject
	public MoneyServiceImpl(SmrtBroadcaster<S2CProtocol> smrtBroadcaster, WalletDao walletDao, CurrencyDao currencyDao) {
		this.smrtBroadcaster = smrtBroadcaster;
		this.walletDao = walletDao;
		this.currencyDao = currencyDao;
	}

	@Override
	@AutoTransaction
	public Wallet createWallet(ServerEntity entity) {
		Wallet wallet = walletDao.findByOwner(entity);
		if (wallet != null) {
			throw new DuplicateObjectException("Wallet already existed for " + entity);
		}
		wallet = new Wallet(entity);
		walletDao.persist(wallet);
		return wallet;
	}

	@Override
	@AutoTransaction
	public void awardMoney(ServerEntity receiver, PersistedMoney money) {
		if (money.isZero()) {
			return;
		}
		Wallet wallet = walletDao.findByOwner(receiver);
		if (wallet == null) {
			log.info("No wallet for the receiving entity, throwing {} into cyberspace", money);
			return;
		}

		WalletCompartment compartment = wallet.getCompartment(money.getCurrency());
		if (compartment == null) {
			compartment = new WalletCompartment(money);
			wallet.addCompartment(compartment);
		} else {
			try {
				PersistedMoney total = compartment.add(money);
				send(receiver, money, total);
			} catch (WrongCurrencyException | MoneyUnderflowException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@AutoTransaction
	@Override
	public void subtractMoney(PersistedMoney money, ServerEntity owner) throws MoneyUnderflowException {
		if (money.isZero()) {
			return;
		}
		Wallet wallet = walletDao.findByOwner(owner);
		if (wallet == null) {
			log.info("No wallet for the receiving entity, throwing {} into cyberspace", money);
			return;
		}

		WalletCompartment compartment = wallet.getCompartment(money.getCurrency());

		try {
			PersistedMoney total = compartment.subtract(money);
			smrtBroadcaster.create().to(owner).send().trade().playerMoneySubtracted(total.getCurrency().getName(),
					money.getAmount(),
					total.getAmount());
		} catch (WrongCurrencyException e) {
			throw new RuntimeException(e);
		}
	}

	private void send(ServerEntity receiver, PersistedMoney money, PersistedMoney total) {
		smrtBroadcaster.create().to(receiver).send().trade().playerMoneyUpdate(total.getCurrency().getName(),
				money.getAmount(),
				total.getAmount());
	}

	@Override
	@AutoTransaction
	public void notifyEntity(ServerEntity entity) {
		Wallet wallet = walletDao.findByOwner(entity);
		if (wallet != null) {
			for (WalletCompartment compartment : wallet.getCompartments()) {
				PersistedMoney money = compartment.getMoney();
				send(entity, PersistedMoney.ZERO, money);
			}
		}

	}

	@Override
	@AutoTransaction
	public ImmutableMap<String, PersistedCurrency> getCurrencies() {
		List<PersistedCurrency> all = currencyDao.findAll();
		return Maps.uniqueIndex(all, GET_CURRENCY_NAME);
	}
}
