package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.shared.model.Gender;

@SmrtProtocol
public interface ClientAccountMessages {
	void createCharacter(String name, Gender gender);
}
