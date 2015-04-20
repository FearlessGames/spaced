package se.spaced.server.loot;

import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.items.ServerItemTemplate;

public class Loot {
	private final ServerItemTemplate template;
	private final PersistedMoney money;

	public Loot(ServerItemTemplate template) {
		this(template, PersistedMoney.ZERO);
	}

	public Loot(ServerItemTemplate template, PersistedMoney money) {
		this.template = template;
		this.money = money;
	}

	public ServerItemTemplate getItemTemplate() {
		return template;
	}

	public PersistedMoney getMoney() {
		return money;
	}
}
