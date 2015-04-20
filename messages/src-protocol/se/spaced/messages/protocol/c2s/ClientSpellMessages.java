package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Spell;

import java.util.List;

@SmrtProtocol
public interface ClientSpellMessages {
	void requestSpellBook();

	void requestSpellInfo(List<? extends Spell> dummy);
}
