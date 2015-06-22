package se.spaced.server.account;

import com.google.common.collect.Lists;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.Player;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Account extends ExternalPersistableBase {

	@OneToMany(fetch = FetchType.EAGER)
	private List<Player> characters;

	private AccountType type;

	public Account() {
	}

	public Account(UUID uuid, AccountType type) {
		super(uuid);
		this.type = type;
		characters = Lists.newArrayList();
	}

	public Iterable<Player> getPlayerCharacters() {
		return characters;
	}

	public void addPlayerCharacter(Player player) {
		characters.add(player);
	}

	public AccountType getType() {
		return type;
	}
}
