package se.spaced.server.model.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.Action;
import se.spaced.server.model.action.SpellListener;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.model.items.Usage;

public class UseItemAction extends Action {
	private static final Logger log = LoggerFactory.getLogger(UseItemAction.class);

	private final ServerSpell spell;
	private final ServerItem item;

	private final ItemService itemService;
	private final ServerEntity owner;
	private final ServerEntity target;
	private final long now;
	private final SpellCombatService spellCombatService;

	public UseItemAction(
			ItemService itemService,
			ServerItem item,
			SpellCombatService spellCombatService,
			ServerEntity owner,
			ServerEntity target,
			long now) {
		super(now);
		this.itemService = itemService;
		this.item = item;
		this.spell = item.getSpell();
		this.spellCombatService = spellCombatService;
		this.owner = owner;
		this.target = target;
		this.now = now;
	}

	@Override
	public void perform() {
		log.info("using Item {} with spell {}", item, spell);
		if (spell != null && owner.isAlive()) {
			SpellListener listener = null;
			if (isConsumable(item.getItemTypes())) {
				listener = new SpellListener() {
					@Override
					public void notifySpellCompleted() {
						itemService.deleteItem(item);
					}
				};
			}
			spellCombatService.startSpellCast(owner, target, spell, now, listener);
		}
	}

	private boolean isConsumable(final Iterable<ItemType> itemTypes) {
		for (ItemType itemType : itemTypes) {
			if (itemType.getUsage() == Usage.CONSUME) {
				return true;
			}
		}

		return false;
	}
}
