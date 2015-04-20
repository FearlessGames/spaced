package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.SpellDirectory;
import se.spaced.client.model.player.PlayerActions;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class SpellApi {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final SpellDirectory spellDirectory;
	private final PlayerActions playerActions;

	@Inject
	public SpellApi(SpellDirectory spellDirectory, PlayerActions playerActions) {
		this.spellDirectory = spellDirectory;
		this.playerActions = playerActions;
	}

	@LuaMethod(global = true, name = "GetAllSpells")
	public List<ClientSpell> getAllSpells() {
		return new ArrayList<ClientSpell>(spellDirectory.getUsersSpells());
	}

	@LuaMethod(global = true, name = "Cast")
	public void castSpell(ClientSpell spell) {
		if (spell != null) {
			playerActions.startSpellCast(spell);
		} else {
			// TODO: fail in some way. I don't know how to do it
		}
	}


}
