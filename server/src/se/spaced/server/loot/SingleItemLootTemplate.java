package se.spaced.server.loot;

import com.google.common.collect.Lists;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.shared.util.random.RandomProvider;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Collection;

@Entity
public class SingleItemLootTemplate extends PersistableLootTemplate {
	@ManyToOne(fetch = FetchType.EAGER)
	private ServerItemTemplate itemTemplate;

	public SingleItemLootTemplate() {
		itemTemplate = null;
	}

	public SingleItemLootTemplate(UUID uuid, String templateName, ServerItemTemplate itemTemplate) {
		super(uuid, templateName);
		this.itemTemplate = itemTemplate;
	}

	public ServerItemTemplate getItemTemplate() {
		return itemTemplate;
	}

	public void setItemTemplate(ServerItemTemplate itemTemplate) {
		this.itemTemplate = itemTemplate;
	}

	@Override
	public Collection<Loot> generateLoot(RandomProvider randomProvider) {
		return Lists.newArrayList(new Loot(itemTemplate));
	}
}
