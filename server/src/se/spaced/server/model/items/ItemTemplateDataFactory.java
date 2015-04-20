package se.spaced.server.model.items;

import se.spaced.messages.protocol.ItemTemplateData;

public class ItemTemplateDataFactory {
	private ItemTemplateDataFactory() {
	}

	public static ItemTemplateData create(ServerItemTemplate template) {
		return new ItemTemplateData(template.getPk(),
				template.getName(),
				template.getAppearanceData().asSharedAppearanceData(),
				template.getItemTypes(),
				template.getEquipAuras(),
				template.getSellsFor().asMoney(), template.getSpell());
	}
}
