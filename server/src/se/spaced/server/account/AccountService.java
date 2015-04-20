package se.spaced.server.account;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.Player;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.ObjectNotFoundException;
import se.spaced.shared.network.webservices.informationservice.ServerAccountLoadStatus;

import java.util.List;

public interface AccountService {
	Account createAccount(UUID uuid, AccountType type) throws DuplicateObjectException;

	Account getAccount(UUID uuid) throws ObjectNotFoundException;

	List<Account> getAllAccounts();

	ServerAccountLoadStatus getServerAccountLoadStatus();

	void bindCharacterToAccount(Account account, Player player);

	int getNumberOfAccounts();

	void deleteAll();
}
