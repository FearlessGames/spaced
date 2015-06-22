package se.spaced.server.model.spell;

import com.google.common.collect.Sets;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.persistence.dao.impl.OwnedPersistableBase;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.Collections;
import java.util.Set;

@Entity
public class SpellBook extends OwnedPersistableBase {
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	private final Set<ServerSpell> spells = Sets.newHashSet();

	public SpellBook(ServerEntity owner) {
		super(owner);
	}

	public SpellBook() {
	}

	public void addSpell(ServerSpell spell) {
		spells.add(spell);
	}

	public Set<ServerSpell> getSpells() {
		return Collections.unmodifiableSet(spells);
	}

	@LuaMethod(name = "GetSpell")
	public ServerSpell getSpell(String spellName) {
		for (ServerSpell spell : spells) {
			if (spellName.equals(spell.getName())) {
				return spell;
			}
		}
		return null;
	}
}
