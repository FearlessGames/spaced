package se.spaced.server.persistence.migrator;

import com.google.inject.Inject;
import se.fearlessgames.common.util.Digester;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.account.Account;
import se.spaced.server.account.AccountService;
import se.spaced.server.account.AccountType;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.dao.interfaces.AccountDao;
import se.spaced.server.player.PlayerCreationService;
import se.spaced.shared.model.Gender;

import java.util.List;

public class DevAccountPopulator implements Migrator {

	private final AccountService accountService;
	private final PlayerCreationService playerCreationService;
	private final AccountDao accountDao;
	private final Digester fameDigester = new Digester("beefcake");

	@Inject
	public DevAccountPopulator(
			AccountService accountService,
			PlayerCreationService playerCreationService,
			AccountDao accountDao) {
		this.accountService = accountService;
		this.playerCreationService = playerCreationService;
		this.accountDao = accountDao;
	}

	@Override
	public void execute() {
		try {
			List<Account> all = accountDao.findAll();
			if (!all.isEmpty()) {
				return;
			}

			addSingleCharAccount("hiflyer", "hiflyer", Gender.MALE);
			addSingleCharAccount("snylt", "snylt", Gender.MALE);
			addSingleCharAccount("krka", "krka", Gender.MALE);
			addSingleCharAccount("bronzon", "bronzon", Gender.MALE);
			addSingleCharAccount("dema", "Dem", Gender.MALE);
			addSingleCharAccount("spoo", "Spoo", Gender.MALE);
			addSingleCharAccount("fegg", "Trollman", Gender.MALE);
			addSingleCharAccount("hierbas", "grogg", Gender.FEMALE);
			addSingleCharAccount("fern", "Fernan", Gender.MALE);
			addSingleCharAccount("thecookie", "cookie", Gender.FEMALE);
			addSingleCharAccount("devour", "Devour", Gender.MALE);
			addSingleCharAccount("knifh", "Knifhen", Gender.MALE);
			addSingleCharAccount("elaida", "Elaida", Gender.FEMALE);
			addSingleCharAccount("milara", "milara", Gender.FEMALE);


		} catch (DuplicateObjectException e) {
			throw new RuntimeException(e);
		}
	}

	private Account addSingleCharAccount(
			String accountName,
			String charName,
			Gender gender) throws DuplicateObjectException {
		byte[] bytes = fameDigester.md5(accountName);
		UUID accountPk = UUID.nameUUIDFromBytes(bytes);
		Account account = accountService.createAccount(accountPk, AccountType.GM);

		createPlayerHelper(account, charName, gender, PlayerType.REGULAR);
		createPlayerHelper(account, charName + "gm", gender, PlayerType.GM);

		return account;
	}

	private Player createPlayerHelper(Account account, String name, Gender gender, PlayerType type) {
		try {
			return playerCreationService.createDefaultPlayer(account, name, gender, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
