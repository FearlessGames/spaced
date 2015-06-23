package se.spaced.server.net.listeners.auth;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.Salts;
import se.spaced.messages.protocol.c2s.ClientConnectionMessages;
import se.spaced.messages.protocol.s2c.ServerConnectionMessages;
import se.spaced.server.account.Account;
import se.spaced.server.account.AccountService;
import se.spaced.server.account.AccountType;
import se.spaced.server.model.Player;
import se.spaced.server.model.items.EquipmentService;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.player.RemotePlayerService;
import se.spaced.server.model.world.TimeService;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.persistence.ObjectNotFoundException;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.util.List;
import java.util.Map;

public class ClientConnectionMessageAuth implements ClientConnectionMessages, AuthCallback {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final AccountService accountService;
	private final RemotePlayerService remotePlayerService;
	private final ServerConnectionMessages response;

	private final ClientConnection clientConnection;
	private final AuthenticatorService authenticatorService;
	private final EquipmentService equipmentService;
	private final TimeService timeService;

	public ClientConnectionMessageAuth(
			ClientConnection clientConnection,
			AccountService accountService,
			ServerConnectionMessages response,
			RemotePlayerService remotePlayerService,
			AuthenticatorService authenticatorService, EquipmentService equipmentService, TimeService timeService) {
		this.clientConnection = clientConnection;
		this.accountService = accountService;
		this.response = response;
		this.remotePlayerService = remotePlayerService;
		this.authenticatorService = authenticatorService;
		this.equipmentService = equipmentService;
		this.timeService = timeService;
	}

	@Override
	public void loginAccount(String accountName, String hash, String address) {
		authenticatorService.authenticate(accountName, hash, address, this);
	}

	@Override
	public void authenticateAccount(String accountName, String hash, String address, String key) {
		authenticatorService.authenticateWithAuthenticator(accountName, hash, address, key, this);
	}

	private Account getOrCreateAccount(UUID uuid, AccountType accountType) {
		try {
			Account account = accountService.getAccount(uuid);
			return account;
		} catch (ObjectNotFoundException e) {
			return accountService.createAccount(uuid, accountType);
		}
	}

	@Override
	public void requestPlayerList() {
		Account account = clientConnection.getAccount();
		if (account == null) {
			// TODO: add appropiate error response
			return;
		}

		List<EntityData> entityData = Lists.newArrayList();
		for (Player playerCharacter : account.getPlayerCharacters()) {
			entityData.add(playerCharacter.createEntityData());
		}
		response.playerListResponse(entityData);
	}

	@Override
	public void requestPlayerInfo(final UUID playerId) {
		Account account = clientConnection.getAccount();
		if (account == null) {
			// TODO: add appropiate error response
			return;
		}
		Iterable<Player> playerCharacters = account.getPlayerCharacters();
		Player player = Iterables.find(playerCharacters, new Predicate<Player>() {
			@Override
			public boolean apply(Player player) {
				return player.getPk().equals(playerId);
			}
		});
		EquippedItems equippedItems = equipmentService.getEquippedItems(player);
		Map<ContainerType, ServerItem> items = equippedItems.getEquippedItems();
		Map<ContainerType, String> modelPaths = Maps.newHashMap();

		for (Map.Entry<ContainerType, ServerItem> itemEntry : items.entrySet()) {
			modelPaths.put(itemEntry.getKey(), itemEntry.getValue().getAppearanceData().getModelName());
		}

		response.playerInfoResponse(playerId, modelPaths, player.isGm());
	}

	@Override
	public void requestLoginSalts(String userName) {
		authenticatorService.requestAuthSalts(userName, new AuthSaltsCallback() {
			@Override
			public void receievedSalts(Salts salts) {
				response.requestSaltsResponse(salts);
			}
		});
	}

	@Override
	public void loginCharacter(UUID playerId) {
		Account account = clientConnection.getAccount();
		if (account == null) {
			// TODO: add appropiate error response
			return;
		}
		Player player = null;
		for (Player p : account.getPlayerCharacters()) {
			log.info("Player with id " + p.getPk());
			if (p.getPk().equals(playerId)) {
				player = p;
				break;
			}
		}

		if (player == null) {
			// TODO: add appropiate error response
			return;
		}

		remotePlayerService.playerLoggedIn(player, clientConnection);
	}

	@Override
	public void logout() {
		Player player = clientConnection.getPlayer();
		if (player != null) {
			remotePlayerService.playerLoggedOut(player, clientConnection);
		}
	}

	@Override
	public void getLocation(UUID playerId) {
		Account account = clientConnection.getAccount();
		if (account == null) {
			// TODO: add appropiate error response
			return;
		}

		Player player = null;
		for (Player p : account.getPlayerCharacters()) {
			log.info("Player with id " + p.getPk());
			if (p.getPk().equals(playerId)) {
				player = p;
				break;
			}
		}

		if (player == null) {
			// TODO: add appropiate error response
			return;
		}
		clientConnection.getReceiver().connection().locationResponse(playerId,
				"zone/spacebattle/outerSpace.zone",
				player.getPositionalData().toPositionalData(),
				timeService.getDayInfo("outerSpace"),
				timeService.getCurrentDayOffsetInMillis("outerSpace"));
	}

	@Override
	public void successfullyLoggedIn(String accountName, ExternalAccount externalAccount) {

		AccountType accountType = AccountType.REGULAR;
		if (externalAccount.getType() == 1) {
			accountType = AccountType.GM;
		}

		Account account = getOrCreateAccount(externalAccount.getUuid(), accountType);
		clientConnection.setAccount(account);
		response.accountLoginResponse(accountName, true, "hooray");
	}

	@Override
	public void authenticationFailed(String accountName) {
		response.accountLoginResponse(accountName, false, "invalid account name or password");
	}

	@Override
	public void basicAuthFailed(String accountName) {
		response.accountLoginResponse(accountName, false, "Serverside error");
	}

	@Override
	public void needsAuthenticator() {
		response.needsAuthenticator();
	}


}
