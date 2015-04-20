package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;

import java.util.Collection;

@SmrtProtocol
public interface ServerSpellMessages {
	void spellBookInfo(Collection<? extends Spell> spells);

	void spellData(Collection<SpellData> spellData);

	void spellAdded(Spell spell);

	void auraInfo(AuraTemplate template);
}
