package se.spaced.client.model.listener;

import se.spaced.client.model.ClientEntity;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.shared.model.items.ContainerType;

public interface EquipmentListener {
	void itemEquipped(ClientEntity clientEntity, ItemTemplateData clientItem, ContainerType container);

	void itemUnequipped(ClientEntity clientEntity, ContainerType container);
}
