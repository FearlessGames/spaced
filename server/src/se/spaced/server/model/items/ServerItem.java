package se.spaced.server.model.items;

import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.impl.PersistableBase;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.items.ItemType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Set;

/**
 * Base class for any item such as weapons and food
 */
@Entity
public class ServerItem extends PersistableBase implements SpacedItem {
	@ManyToOne(optional = false)
	private ServerItemTemplate template;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private ServerEntity owner;

	protected ServerItem() {
	}

	public ServerItem(ServerItemTemplate template) {
		this.template = template;
	}

	public String getName() {
		return template.getName();
	}

	public ServerItemTemplate getTemplate() {
		return template;
	}

	public PersistedMoney getSellsFor() {
		return template.getSellsFor();
	}


	public AppearanceData getAppearanceData() {
		return template.getAppearanceData().asSharedAppearanceData();
	}

	public PersistedAppearanceData getPersistedAppearanceData() {
		return template.getAppearanceData();
	}

	public Set<ItemType> getItemTypes() {
		return template.getItemTypes();
	}

	public ServerSpell getSpell() {
		return template.getSpell();
	}

	public ServerEntity getOwner() {
		return owner;
	}

	public void setOwner(ServerEntity owner) {
		this.owner = owner;
	}

	@Override
	public ItemTemplate getItemTemplate() {
		return template;
	}

}
