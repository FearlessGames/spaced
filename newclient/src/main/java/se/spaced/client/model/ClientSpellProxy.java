package se.spaced.client.model;

import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.Spell;

public class ClientSpellProxy implements Spell {
	private final UUID pk;

	public ClientSpellProxy(UUID pk) {
		this.pk = pk;
	}

	@Override
	public UUID getPk() {
		return pk;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Spell)) {
			return false;
		}

		Spell that = (Spell) o;
		return pk.equals(that.getPk());
	}

	@Override
	public int hashCode() {
		return pk.hashCode();
	}

	@Override
	public String toString() {
		return "ClientSpellProxy{" +
				"pk=" + pk +
				'}';
	}
}